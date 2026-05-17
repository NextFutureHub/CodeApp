package com.codelingo.app

import android.app.Application
import com.codelingo.app.data.AuthRepository
import com.codelingo.app.data.CourseRepository
import com.codelingo.app.data.GameRepository
import com.codelingo.app.data.ProgressSyncRepository
import com.codelingo.app.data.SettingsRepository
import com.codelingo.app.data.voice.FalstaffVoiceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CodeLingoApp : Application() {
    private val appScope = CoroutineScope(SupervisorJob())

    lateinit var courseRepository: CourseRepository
        private set
    lateinit var gameRepository: GameRepository
        private set
    lateinit var settingsRepository: SettingsRepository
        private set
    lateinit var authRepository: AuthRepository
        private set
    lateinit var progressSyncRepository: ProgressSyncRepository
        private set
    lateinit var falstaffVoiceRepository: FalstaffVoiceRepository
        private set

    override fun onCreate() {
        super.onCreate()
        authRepository = AuthRepository()
        gameRepository = GameRepository(this)
        progressSyncRepository = ProgressSyncRepository(authRepository, gameRepository, appScope)
        gameRepository.attachSync(progressSyncRepository)
        courseRepository = CourseRepository(this).also {
            it.authRepository = authRepository
        }
        settingsRepository = SettingsRepository(this)
        falstaffVoiceRepository = FalstaffVoiceRepository(this)
    }
}
