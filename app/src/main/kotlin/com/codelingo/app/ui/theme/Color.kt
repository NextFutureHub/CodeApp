package com.codelingo.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
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

private val palette @Composable @ReadOnlyComposable get() = LocalCodeLingoPalette.current

val Background: Color @Composable @ReadOnlyComposable get() = palette.background
val Foreground: Color @Composable @ReadOnlyComposable get() = palette.foreground
val Card: Color @Composable @ReadOnlyComposable get() = palette.card
val CardElevated: Color @Composable @ReadOnlyComposable get() = palette.cardElevated
val Primary: Color @Composable @ReadOnlyComposable get() = palette.primary
val PrimaryGlow: Color @Composable @ReadOnlyComposable get() = palette.primaryGlow
val PrimaryForeground: Color @Composable @ReadOnlyComposable get() = palette.primaryForeground
val Secondary: Color @Composable @ReadOnlyComposable get() = palette.secondary
val Muted: Color @Composable @ReadOnlyComposable get() = palette.muted
val MutedForeground: Color @Composable @ReadOnlyComposable get() = palette.mutedForeground
val Success: Color @Composable @ReadOnlyComposable get() = palette.success
val Warning: Color @Composable @ReadOnlyComposable get() = palette.warning
val Destructive: Color @Composable @ReadOnlyComposable get() = palette.destructive
val Streak: Color @Composable @ReadOnlyComposable get() = palette.streak
val Xp: Color @Composable @ReadOnlyComposable get() = palette.xp
val Border: Color @Composable @ReadOnlyComposable get() = palette.border
