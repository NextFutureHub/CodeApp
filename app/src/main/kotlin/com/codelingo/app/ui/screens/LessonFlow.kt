package com.codelingo.app.ui.screens

import com.codelingo.app.data.model.Lesson
import com.codelingo.app.data.model.hasMiniScene
import com.codelingo.app.data.model.hasStoryIntro
import com.codelingo.app.data.model.hasStoryOutro

enum class LessonStage {
    Theory,
    StoryIntro,
    Task,
    MiniWorld,
    StoryOutro,
    Finished,
}

fun Lesson.initialStage(): LessonStage = when {
    !theory.isNullOrBlank() -> LessonStage.Theory
    hasStoryIntro() -> LessonStage.StoryIntro
    tasks.isNotEmpty() -> LessonStage.Task
    hasMiniScene() -> LessonStage.MiniWorld
    hasStoryOutro() -> LessonStage.StoryOutro
    else -> LessonStage.Finished
}

fun Lesson.nextStageAfter(current: LessonStage): LessonStage = when (current) {
    LessonStage.Theory -> when {
        hasStoryIntro() -> LessonStage.StoryIntro
        tasks.isNotEmpty() -> LessonStage.Task
        hasMiniScene() -> LessonStage.MiniWorld
        hasStoryOutro() -> LessonStage.StoryOutro
        else -> LessonStage.Finished
    }
    LessonStage.StoryIntro -> when {
        tasks.isNotEmpty() -> LessonStage.Task
        hasMiniScene() -> LessonStage.MiniWorld
        hasStoryOutro() -> LessonStage.StoryOutro
        else -> LessonStage.Finished
    }
    LessonStage.Task -> when {
        hasMiniScene() -> LessonStage.MiniWorld
        hasStoryOutro() -> LessonStage.StoryOutro
        else -> LessonStage.Finished
    }
    LessonStage.MiniWorld -> when {
        hasStoryOutro() -> LessonStage.StoryOutro
        else -> LessonStage.Finished
    }
    LessonStage.StoryOutro -> LessonStage.Finished
    LessonStage.Finished -> LessonStage.Finished
}

fun Lesson.totalProgressSteps(): Int {
    var steps = 0
    if (!theory.isNullOrBlank()) steps++
    if (hasStoryIntro()) steps++
    steps += tasks.size
    if (hasMiniScene()) steps++
    if (hasStoryOutro()) steps++
    steps++
    return steps.coerceAtLeast(1)
}

fun Lesson.progressFor(stage: LessonStage, taskIndex: Int): Float {
    var done = 0f
    val total = totalProgressSteps().toFloat()
    if (!theory.isNullOrBlank()) {
        if (stage == LessonStage.Theory) return done / total
        done++
    }
    if (hasStoryIntro()) {
        if (stage == LessonStage.StoryIntro) return (done + 0.4f) / total
        if (stage.ordinal < LessonStage.StoryIntro.ordinal) return done / total
        done++
    }
    if (tasks.isNotEmpty()) {
        if (stage == LessonStage.Task) return (done + (taskIndex + 1f) / tasks.size.coerceAtLeast(1)) / total
        if (stage.ordinal < LessonStage.Task.ordinal) return done / total
        done += tasks.size
    }
    if (hasMiniScene()) {
        if (stage == LessonStage.MiniWorld) return (done + 0.5f) / total
        if (stage.ordinal < LessonStage.MiniWorld.ordinal) return done / total
        done++
    }
    if (hasStoryOutro()) {
        if (stage == LessonStage.StoryOutro) return (done + 0.4f) / total
        if (stage.ordinal < LessonStage.StoryOutro.ordinal) return done / total
        done++
    }
    return 1f
}
