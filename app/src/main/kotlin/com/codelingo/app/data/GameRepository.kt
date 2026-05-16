package com.codelingo.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.codelingo.app.data.model.GameState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Date

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "codelingo_prefs")

class GameRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }
    private val stateKey = stringPreferencesKey("codelingo-state")

  companion object {
        private const val XP_PER_LEVEL = 100
    }

    val state: Flow<GameState> = context.dataStore.data.map { prefs ->
        val raw = prefs[stateKey]
        if (raw != null) {
            try {
                json.decodeFromString<GameState>(raw)
            } catch (_: Exception) {
                GameState()
            }
        } else {
            GameState()
        }
    }

    suspend fun initializeStreak() {
        val current = state.first()
        val today = Date().toString()
        val yesterday = Date(System.currentTimeMillis() - 86_400_000).toString()
        val updated = when {
            current.lastActiveDate == today -> current
            current.lastActiveDate == yesterday -> current.copy(
                streak = current.streak + 1,
                lastActiveDate = today,
            )
            else -> current.copy(
                streak = if (current.lastActiveDate.isNotEmpty()) 0 else current.streak,
                lastActiveDate = today,
            )
        }
        val final = applyStreakAchievement(updated)
        if (final != current) saveState(final)
    }

    private fun applyStreakAchievement(state: GameState): GameState {
        if (state.streak >= 7 && "streak-7" !in state.achievements) {
            return state.copy(achievements = state.achievements + "streak-7")
        }
        return state
    }

    suspend fun addXp(amount: Int) {
        val prev = state.first()
        val newXp = prev.xp + amount
        val newLevel = newXp / XP_PER_LEVEL + 1
        val newAchievements = prev.achievements.toMutableList()
        if (newXp >= 100 && "first-100" !in newAchievements) newAchievements.add("first-100")
        if (newXp >= 500 && "xp-500" !in newAchievements) newAchievements.add("xp-500")
        if (newLevel >= 5 && "level-5" !in newAchievements) newAchievements.add("level-5")
        saveState(prev.copy(xp = newXp, level = newLevel, achievements = newAchievements))
    }

    suspend fun loseLife() {
        val prev = state.first()
        saveState(prev.copy(lives = maxOf(0, prev.lives - 1)))
    }

    suspend fun completeLesson(lessonId: String) {
        val prev = state.first()
        if (lessonId in prev.completedLessons) return
        val newCompleted = prev.completedLessons + lessonId
        val newAchievements = prev.achievements.toMutableList()
        if (newCompleted.size >= 5 && "lessons-5" !in newAchievements) newAchievements.add("lessons-5")
        if (newCompleted.size >= 10 && "lessons-10" !in newAchievements) newAchievements.add("lessons-10")
        saveState(prev.copy(completedLessons = newCompleted, achievements = newAchievements))
    }

    suspend fun restoreLives() {
        val prev = state.first()
        saveState(prev.copy(lives = prev.maxLives))
    }

    private suspend fun saveState(gameState: GameState) {
        context.dataStore.edit { prefs ->
            prefs[stateKey] = json.encodeToString(gameState)
        }
    }
}
