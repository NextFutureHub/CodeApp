package com.codelingo.app.data

import com.codelingo.app.data.model.GameState
import com.codelingo.app.data.remote.SupabaseProvider
import com.codelingo.app.data.remote.dto.ProfileDto
import com.codelingo.app.data.remote.dto.ProfileUpdateDto
import com.codelingo.app.data.remote.dto.UserProgressDto
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProgressSyncRepository(
    private val authRepository: AuthRepository,
    private val gameRepository: GameRepository,
    private val scope: CoroutineScope,
) {
    private var subscribed = false

    fun startRealtimeSync() {
        if (!SupabaseProvider.isConfigured || subscribed) return
        val userId = authRepository.currentUserId() ?: return
        subscribed = true
        scope.launch {
            val channel = SupabaseProvider.client.realtime.channel("progress-$userId")
            val changes = channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
                table = "user_progress"
            }
            channel.subscribe()
            changes.onEach { pullAndMerge() }.launchIn(this)
        }
    }

    fun stopRealtimeSync() {
        subscribed = false
    }

    suspend fun pullAndMerge(): Result<GameState> = runCatching {
        val userId = authRepository.currentUserId() ?: error("Не авторизован")
        val client = SupabaseProvider.client

        val progress = client.postgrest.from("user_progress")
            .select(Columns.ALL) {
                filter { eq("user_id", userId) }
            }
            .decodeSingle<UserProgressDto>()

        val profile = client.postgrest.from("profiles")
            .select(Columns.ALL) {
                filter { eq("id", userId) }
            }
            .decodeSingle<ProfileDto>()

        gameRepository.mergeWithRemote(progress.toGameState(profile.displayName))
    }

    suspend fun push(state: GameState): Result<Unit> = runCatching {
        val userId = authRepository.currentUserId() ?: return@runCatching
        SupabaseProvider.client.postgrest
            .from("user_progress")
            .upsert(state.toProgressDto(userId))
    }

    suspend fun updateDisplayName(name: String): Result<Unit> = runCatching {
        val userId = authRepository.currentUserId() ?: error("Не авторизован")
        SupabaseProvider.client.postgrest.from("profiles").update(ProfileUpdateDto(displayName = name)) {
            filter { eq("id", userId) }
        }
        gameRepository.updateUserName(name)
    }

    private fun UserProgressDto.toGameState(displayName: String) = GameState(
        xp = xp,
        level = level,
        streak = streak,
        lives = lives,
        maxLives = maxLives,
        lastActiveDate = lastActiveDate ?: "",
        completedLessons = completedLessons,
        achievements = achievements,
        userName = displayName,
    )

    private fun GameState.toProgressDto(userId: String) = UserProgressDto(
        userId = userId,
        xp = xp,
        level = level,
        streak = streak,
        lives = lives,
        maxLives = maxLives,
        lastActiveDate = lastActiveDate.takeIf { it.isNotBlank() },
        completedLessons = completedLessons,
        achievements = achievements,
    )
}
