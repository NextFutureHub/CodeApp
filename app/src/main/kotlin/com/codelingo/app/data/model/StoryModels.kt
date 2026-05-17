package com.codelingo.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StoryChoice(
    val text: String,
    val response: String? = null,
)

@Serializable
data class StoryBeat(
    val id: String? = null,
    val speaker: String = "falstaff",
    val text: String,
    val emotion: String = "neutral",
    val audioUrl: String? = null,
    val choices: List<StoryChoice>? = null,
)

@Serializable
data class MiniHotspot(
    val id: String,
    val label: String,
    val x: Float,
    val y: Float,
    val width: Float = 0.14f,
    val height: Float = 0.12f,
    val task: Task,
    val doneLabel: String? = null,
)

@Serializable
data class MiniScene(
    val title: String,
    val subtitle: String? = null,
    val background: String = "room",
    val hotspots: List<MiniHotspot> = emptyList(),
)
