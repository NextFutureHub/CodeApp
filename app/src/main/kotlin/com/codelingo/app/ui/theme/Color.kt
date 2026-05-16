package com.codelingo.app.ui.theme

import androidx.compose.ui.graphics.Color

fun hsl(h: Float, s: Float, l: Float, alpha: Float = 1f): Color = Color.hsl(h, s / 100f, l / 100f, alpha)

fun parseHslColor(hslString: String, alpha: Float = 1f): Color {
    val parts = hslString.trim().split(Regex("\\s+"))
    if (parts.size < 3) return Color.Gray
    val h = parts[0].toFloatOrNull() ?: 0f
    val s = parts[1].removeSuffix("%").toFloatOrNull() ?: 0f
    val l = parts[2].removeSuffix("%").toFloatOrNull() ?: 0f
    return hsl(h, s, l, alpha)
}

val Background = hsl(240f, 15f, 9f)
val Foreground = hsl(0f, 0f, 95f)
val Card = hsl(240f, 12f, 13f)
val CardElevated = hsl(240f, 12f, 16f)
val Primary = hsl(262f, 83f, 58f)
val PrimaryGlow = hsl(262f, 90f, 65f)
val PrimaryForeground = Color.White
val Secondary = hsl(240f, 12f, 18f)
val Muted = hsl(240f, 10f, 20f)
val MutedForeground = hsl(240f, 5f, 55f)
val Success = hsl(142f, 71f, 45f)
val Warning = hsl(38f, 92f, 50f)
val Destructive = hsl(0f, 72f, 51f)
val Streak = hsl(25f, 95f, 53f)
val Xp = hsl(199f, 89f, 48f)
val Border = hsl(240f, 10f, 20f)
