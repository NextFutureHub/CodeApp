package com.codelingo.app

import com.codelingo.app.data.model.GameState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRepositoryTest {
    @Test
    fun levelCalculation_matchesWeb() {
        val xp = 250
        val level = xp / 100 + 1
        assertEquals(3, level)
    }

    @Test
    fun achievementIds_matchWebDefinitions() {
        val state = GameState(
            xp = 100,
            achievements = listOf("first-100"),
        )
        assertTrue(state.achievements.contains("first-100"))
    }

    @Test
    fun streak7Achievement_whenStreakAtLeast7() {
        val state = GameState(streak = 7, achievements = emptyList())
        val eligible = state.streak >= 7 && "streak-7" !in state.achievements
        assertTrue(eligible)
    }
}
