package com.codelingo.app.data

import android.content.Context
import com.codelingo.app.data.model.Course
import com.codelingo.app.data.model.Lesson
import kotlinx.serialization.json.Json

class CourseRepository(context: Context) {
    private val json = Json { ignoreUnknownKeys = true }
    private val allCourses: List<Course> by lazy {
        val text = context.assets.open("courses.json").bufferedReader().use { it.readText() }
        json.decodeFromString<List<Course>>(text)
    }

    fun getCourses(): List<Course> = allCourses

    fun getCourse(courseId: String): Course? = allCourses.find { it.id == courseId }

    fun getLesson(courseId: String, lessonId: String): Lesson? {
        val course = getCourse(courseId) ?: return null
        for (level in course.levels) {
            val lesson = level.lessons.find { it.id == lessonId }
            if (lesson != null) return lesson
        }
        return null
    }

    fun getAllLessons(course: Course): List<Lesson> =
        course.levels.flatMap { it.lessons }

    fun totalLessonCount(): Int = allCourses.sumOf { getAllLessons(it).size }
}
