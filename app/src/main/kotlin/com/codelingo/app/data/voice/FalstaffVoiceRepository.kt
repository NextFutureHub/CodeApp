package com.codelingo.app.data.voice

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.codelingo.app.BuildConfig
import com.codelingo.app.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume

@Serializable
private data class TtsRequest(val lessonId: String, val beatId: String, val text: String)

@Serializable
private data class TtsResponse(val url: String? = null, val cached: Boolean = false, val fallback: String? = null)

enum class VoiceSource {
    ElevenLabs,
    AndroidTts,
    None,
}

private val json = Json { ignoreUnknownKeys = true }

class FalstaffVoiceRepository(context: Context) {
    private val appContext = context.applicationContext
    private val http = HttpClient(OkHttp)
    private val urlCache = ConcurrentHashMap<String, String>()
    private var tts: TextToSpeech? = null
    @Volatile
    private var ttsReady = false
    private var mediaPlayer: MediaPlayer? = null

    var lastSource: VoiceSource = VoiceSource.None
        private set

    init {
        TextToSpeech(appContext) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
            if (ttsReady) {
                tts?.language = Locale("ru", "RU")
                tts?.setSpeechRate(0.92f)
                tts?.setPitch(0.85f)
            }
        }.also { tts = it }
    }

    suspend fun speak(
        lessonId: String,
        beatId: String,
        text: String,
        existingUrl: String? = null,
    ) = withContext(Dispatchers.Main) {
        stop()
        val audioUrl = existingUrl?.takeIf { it.isNotBlank() }
            ?: resolveAudioUrl(lessonId, beatId, text)
        if (!audioUrl.isNullOrBlank()) {
            val played = playUrl(audioUrl)
            if (played) {
                lastSource = VoiceSource.ElevenLabs
            } else {
                speakWithTts(text)
            }
        } else {
            speakWithTts(text)
        }
    }

    fun stop() {
        runCatching {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
        mediaPlayer = null
        tts?.stop()
    }

    fun release() {
        stop()
        tts?.shutdown()
        tts = null
    }

    private suspend fun resolveAudioUrl(lessonId: String, beatId: String, text: String): String? {
        val cacheKey = "$lessonId:$beatId"
        urlCache[cacheKey]?.let { return it }
        if (!SupabaseProvider.isConfigured) {
            Log.w(TAG, "Supabase not configured — using Android TTS")
            return null
        }
        return runCatching {
            val token = SupabaseProvider.client.auth.currentSessionOrNull()?.accessToken
                ?: BuildConfig.SUPABASE_ANON_KEY
            val httpResponse = http.post("${BuildConfig.SUPABASE_URL}/functions/v1/falstaff-tts") {
                header("Authorization", "Bearer $token")
                header("apikey", BuildConfig.SUPABASE_ANON_KEY)
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(TtsRequest(lessonId, beatId, text)))
            }
            val raw = httpResponse.bodyAsText()
            if (!httpResponse.status.isSuccess()) {
                Log.w(TAG, "falstaff-tts HTTP ${httpResponse.status.value}: $raw")
                return@runCatching null
            }
            val response = json.decodeFromString<TtsResponse>(raw)
            if (response.fallback == "tts") {
                Log.w(TAG, "Edge function: ElevenLabs unavailable (set ELEVENLABS_API_KEY and deploy)")
                return@runCatching null
            }
            response.url?.also { urlCache[cacheKey] = it }
        }.onFailure { Log.e(TAG, "falstaff-tts failed", it) }.getOrNull()
    }

    private suspend fun playUrl(url: String): Boolean = suspendCancellableCoroutine { cont ->
        val player = MediaPlayer()
        mediaPlayer = player
        try {
            player.setDataSource(url)
            player.setOnPreparedListener {
                if (cont.isActive) player.start()
            }
            player.setOnCompletionListener {
                if (cont.isActive) cont.resume(true)
            }
            player.setOnErrorListener { _, what, extra ->
                Log.e(TAG, "MediaPlayer error what=$what extra=$extra url=$url")
                if (cont.isActive) cont.resume(false)
                true
            }
            cont.invokeOnCancellation {
                runCatching { player.release() }
            }
            player.prepareAsync()
        } catch (e: Exception) {
            Log.e(TAG, "setDataSource failed: $url", e)
            runCatching { player.release() }
            if (cont.isActive) cont.resume(false)
        }
    }

    private suspend fun speakWithTts(text: String) {
        waitForTtsReady()
        if (!ttsReady) {
            lastSource = VoiceSource.None
            return
        }
        lastSource = VoiceSource.AndroidTts
        suspendCancellableCoroutine { cont ->
            val utteranceId = "falstaff-${text.hashCode()}"
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) = Unit
                override fun onDone(utteranceId: String?) {
                    if (cont.isActive) cont.resume(Unit)
                }
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    if (cont.isActive) cont.resume(Unit)
                }
            })
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    private suspend fun waitForTtsReady() {
        if (ttsReady) return
        repeat(30) {
            if (ttsReady) return
            kotlinx.coroutines.delay(100)
        }
    }

    companion object {
        private const val TAG = "FalstaffVoice"
    }
}
