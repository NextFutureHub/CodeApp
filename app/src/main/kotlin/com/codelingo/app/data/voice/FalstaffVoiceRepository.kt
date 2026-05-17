package com.codelingo.app.data.voice

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import com.codelingo.app.BuildConfig
import com.codelingo.app.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
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

private val json = Json { ignoreUnknownKeys = true }

class FalstaffVoiceRepository(context: Context) {
    private val appContext = context.applicationContext
    private val http = HttpClient(Android)
    private val urlCache = ConcurrentHashMap<String, String>()
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var mediaPlayer: MediaPlayer? = null

    init {
        TextToSpeech(appContext) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
            if (ttsReady) {
                tts?.language = Locale("ru", "RU")
                tts?.setSpeechRate(0.95f)
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
            playUrl(audioUrl)
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
        if (!SupabaseProvider.isConfigured) return null
        return runCatching {
            val token = SupabaseProvider.client.auth.currentSessionOrNull()?.accessToken
                ?: BuildConfig.SUPABASE_ANON_KEY
            val responseBody = http.post("${BuildConfig.SUPABASE_URL}/functions/v1/falstaff-tts") {
                header("Authorization", "Bearer $token")
                header("apikey", BuildConfig.SUPABASE_ANON_KEY)
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(TtsRequest(lessonId, beatId, text)))
            }.body<String>()
            val response = json.decodeFromString<TtsResponse>(responseBody)
            response.url?.also { urlCache[cacheKey] = it }
        }.getOrNull()
    }

    private suspend fun playUrl(url: String) = suspendCancellableCoroutine { cont ->
        val player = MediaPlayer()
        mediaPlayer = player
        player.setDataSource(url)
        player.setOnPreparedListener {
            if (cont.isActive) {
                player.start()
            }
        }
        player.setOnCompletionListener {
            if (cont.isActive) cont.resume(Unit)
        }
        player.setOnErrorListener { _, _, _ ->
            if (cont.isActive) cont.resume(Unit)
            true
        }
        cont.invokeOnCancellation {
            runCatching { player.release() }
        }
        player.prepareAsync()
    }

    private fun speakWithTts(text: String) {
        if (!ttsReady) return
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "falstaff-${text.hashCode()}")
    }
}
