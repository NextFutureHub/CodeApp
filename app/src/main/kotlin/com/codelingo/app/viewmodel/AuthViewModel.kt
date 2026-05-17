package com.codelingo.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codelingo.app.data.AuthErrorMessages
import com.codelingo.app.data.AuthRepository
import com.codelingo.app.data.AuthUser
import com.codelingo.app.data.CourseRepository
import com.codelingo.app.data.GameRepository
import com.codelingo.app.data.ProgressSyncRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val progressSync: ProgressSyncRepository,
    private val courseRepository: CourseRepository,
    private val gameRepository: GameRepository,
) : ViewModel() {
    val currentUser: StateFlow<AuthUser?> = authRepository.currentUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val isConfigured: Boolean = authRepository.isConfigured

    var authError by mutableStateOf<String?>(null)
        private set

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authError = null
            authRepository.signIn(email, password)
                .onSuccess {
                    onAuthenticated(onSuccess)
                }
                .onFailure { authError = AuthErrorMessages.fromThrowable(it) }
        }
    }

    fun signUp(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authError = null
            authRepository.signUp(email, password)
                .onSuccess {
                    onAuthenticated(onSuccess)
                }
                .onFailure { authError = AuthErrorMessages.fromThrowable(it) }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            progressSync.stopRealtimeSync()
            gameRepository.setActiveUserId(null)
            authRepository.signOut()
        }
    }

    private suspend fun onAuthenticated(onSuccess: () -> Unit) {
        progressSync.pullAndMerge()
        courseRepository.refreshFromRemote()
        progressSync.startRealtimeSync()
        onSuccess()
    }

    class Factory(
        private val authRepository: AuthRepository,
        private val progressSync: ProgressSyncRepository,
        private val courseRepository: CourseRepository,
        private val gameRepository: GameRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(
                    authRepository,
                    progressSync,
                    courseRepository,
                    gameRepository,
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
