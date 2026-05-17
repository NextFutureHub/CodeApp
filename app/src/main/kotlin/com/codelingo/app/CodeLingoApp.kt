package com.codelingo.app

import android.app.Application
import com.codelingo.app.data.CourseRepository
import com.codelingo.app.data.GameRepository
import com.codelingo.app.data.SettingsRepository

class CodeLingoApp : Application() {
    lateinit var courseRepository: CourseRepository
        private set
    lateinit var gameRepository: GameRepository
        private set
    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        courseRepository = CourseRepository(this)
        gameRepository = GameRepository(this)
        settingsRepository = SettingsRepository(this)
    }
}
