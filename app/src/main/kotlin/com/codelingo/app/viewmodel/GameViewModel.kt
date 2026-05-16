package com.codelingo.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codelingo.app.data.GameRepository
import com.codelingo.app.data.model.GameState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {
    val state: StateFlow<GameState> = repository.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GameState(),
    )

    init {
        viewModelScope.launch { repository.initializeStreak() }
    }

    fun addXp(amount: Int) = viewModelScope.launch { repository.addXp(amount) }
    fun loseLife() = viewModelScope.launch { repository.loseLife() }
    fun completeLesson(lessonId: String) = viewModelScope.launch { repository.completeLesson(lessonId) }

    class Factory(private val repository: GameRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
                return GameViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
