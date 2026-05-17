package com.codelingo.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TaskType {
    @SerialName("quiz") QUIZ,
    @SerialName("code") CODE,
    @SerialName("blocks") BLOCKS,
    @SerialName("debug") DEBUG,
    @SerialName("match") MATCH,
    @SerialName("fill") FILL,
}

@Serializable
data class MatchPair(
    val left: String,
    val right: String,
)

@Serializable
data class Task(
    val id: String,
    val type: TaskType,
    val question: String,
    val options: List<String>? = null,
    val correctAnswer: String? = null,
    val starterCode: String? = null,
    val expectedOutput: String? = null,
    val hint: String? = null,
    val blocks: List<String>? = null,
    val correctOrder: List<String>? = null,
    val buggyCode: String? = null,
    val fixedCode: String? = null,
    val pairs: List<MatchPair>? = null,
    val textParts: List<String>? = null,
    val fillOptions: List<String>? = null,
    val correctFill: List<String>? = null,
)

@Serializable
data class Lesson(
    val id: String,
    val title: String,
    val description: String,
    val xpReward: Int,
    val tasks: List<Task>,
    val theory: String? = null,
    val storyIntro: List<StoryBeat>? = null,
    val storyOutro: List<StoryBeat>? = null,
    val miniScene: MiniScene? = null,
)

fun Lesson.hasStoryIntro(): Boolean = !storyIntro.isNullOrEmpty()

fun Lesson.hasStoryOutro(): Boolean = !storyOutro.isNullOrEmpty()

fun Lesson.hasMiniScene(): Boolean = miniScene != null && !miniScene!!.hotspots.isEmpty()

@Serializable
data class CourseLevel(
    val id: String,
    val title: String,
    val lessons: List<Lesson>,
)

@Serializable
data class Course(
    val id: String,
    val title: String,
    val icon: String,
    val color: String,
    val levels: List<CourseLevel>,
)

@Serializable
data class GameState(
    val xp: Int = 0,
    val level: Int = 1,
    val streak: Int = 0,
    val lives: Int = 5,
    val maxLives: Int = 5,
    val lastActiveDate: String = "",
    val completedLessons: List<String> = emptyList(),
    val achievements: List<String> = emptyList(),
    val userName: String = "Ученик",
)

data class AchievementDef(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
)

val ACHIEVEMENTS = listOf(
    AchievementDef("first-100", "Первые 100 XP", "Набери 100 очков опыта", "💯"),
    AchievementDef("xp-500", "XP Мастер", "Набери 500 очков опыта", "🌟"),
    AchievementDef("level-5", "Уровень 5", "Достигни 5 уровня", "🏅"),
    AchievementDef("lessons-5", "Ученик", "Пройди 5 уроков", "📚"),
    AchievementDef("lessons-10", "Знаток", "Пройди 10 уроков", "🎓"),
    AchievementDef("streak-7", "Неделя подряд", "7 дней streak", "🔥"),
)
