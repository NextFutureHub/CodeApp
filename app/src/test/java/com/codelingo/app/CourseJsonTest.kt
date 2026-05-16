package com.codelingo.app

import com.codelingo.app.data.model.Course
import com.codelingo.app.data.model.TaskType
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CourseJsonTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesSampleCourseStructure() {
        val sample = """
            [{
              "id": "html",
              "title": "HTML",
              "icon": "🌐",
              "color": "232 78% 55%",
              "levels": [{
                "id": "html-basics",
                "title": "Основы HTML",
                "lessons": [{
                  "id": "html-1",
                  "title": "Что такое HTML?",
                  "description": "Знакомство",
                  "xpReward": 15,
                  "tasks": [{
                    "id": "html-1-1",
                    "type": "quiz",
                    "question": "Q?",
                    "options": ["A","B"],
                    "correctAnswer": "A"
                  }]
                }]
              }]
            }]
        """.trimIndent()
        val courses = json.decodeFromString<List<Course>>(sample)
        assertEquals(1, courses.size)
        assertEquals("html", courses[0].id)
        assertEquals(TaskType.QUIZ, courses[0].levels[0].lessons[0].tasks[0].type)
    }

    @Test
    fun allTaskTypesExist() {
        assertEquals(6, TaskType.entries.size)
    }
}
