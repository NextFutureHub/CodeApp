package com.codelingo.app.navigation

object Routes {
    const val AUTH = "auth"
    const val HOME = "home"
    const val COURSES = "courses"
    const val COURSE = "course/{courseId}"
    const val LESSON = "lesson/{courseId}/{lessonId}"
    const val ACHIEVEMENTS = "achievements"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"

    fun course(courseId: String) = "course/$courseId"
    fun lesson(courseId: String, lessonId: String) = "lesson/$courseId/$lessonId"
}
