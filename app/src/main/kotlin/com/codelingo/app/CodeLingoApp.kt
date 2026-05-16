package com.codelingo.app

import android.app.Application
import com.codelingo.app.data.CourseRepository
import com.codelingo.app.data.GameRepository

class CodeLingoApp : Application() {
    lateinit var courseRepository: CourseRepository
        private set
    lateinit var gameRepository: GameRepository
        private set

    override fun onCreate() {
        super.onCreate()
        courseRepository = CourseRepository(this)
        gameRepository = GameRepository(this)
    }
}
