package com.codelingo.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    @SerialName("display_name") val displayName: String,
    val role: String = "student",
    @SerialName("avatar_url") val avatarUrl: String? = null,
)

@Serializable
data class UserProgressDto(
    @SerialName("user_id") val userId: String,
    val xp: Int = 0,
    val level: Int = 1,
    val streak: Int = 0,
    val lives: Int = 5,
    @SerialName("max_lives") val maxLives: Int = 5,
    @SerialName("last_active_date") val lastActiveDate: String? = null,
    @SerialName("completed_lessons") val completedLessons: List<String> = emptyList(),
    val achievements: List<String> = emptyList(),
)

@Serializable
data class ProfileUpdateDto(
    @SerialName("display_name") val displayName: String,
)
