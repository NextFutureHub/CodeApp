package com.codelingo.app.data

import android.content.Context
import com.codelingo.app.data.model.Course
import com.codelingo.app.data.model.Lesson
import com.codelingo.app.data.remote.SupabaseProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.Json

class CourseRepository(context: Context) {
    private val json = Json { ignoreUnknownKeys = true }
    private val bundledCourses: List<Course> by lazy {
        val text = context.assets.open("courses.json").bufferedReader().use { it.readText() }
        json.decodeFromString<List<Course>>(text)
    }
    @Volatile
    private var remoteCourses: List<Course>? = null

    suspend fun refreshFromRemote(): Boolean {
        if (!SupabaseProvider.isConfigured || authRepository?.currentUserId() == null) return false
        return runCatching {
            val courses = SupabaseProvider.client.postgrest
                .rpc("get_published_courses")
                .decodeAs<List<Course>>()
            if (courses.isNotEmpty()) {
                remoteCourses = courses
                true
            } else {
                false
            }
        }.getOrDefault(false)
    }

    var authRepository: AuthRepository? = null

    fun getCourses(): List<Course> = remoteCourses ?: bundledCourses

    fun getCourse(courseId: String): Course? = getCourses().find { it.id == courseId }

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

    fun totalLessonCount(): Int = getCourses().sumOf { getAllLessons(it).size }
}
