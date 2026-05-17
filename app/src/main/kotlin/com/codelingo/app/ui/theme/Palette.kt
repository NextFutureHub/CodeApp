package com.codelingo.app.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CodeLingoPalette(
    val background: Color,
    val foreground: Color,
    val card: Color,
    val cardElevated: Color,
    val primary: Color,
    val primaryGlow: Color,
    val primaryForeground: Color,
    val secondary: Color,
    val muted: Color,
    val mutedForeground: Color,
    val success: Color,
    val warning: Color,
    val destructive: Color,
    val streak: Color,
    val xp: Color,
    val border: Color,
)

val LocalCodeLingoPalette = staticCompositionLocalOf { DarkPalette }

val DarkPalette = CodeLingoPalette(
    background = hsl(240f, 15f, 9f),
    foreground = hsl(0f, 0f, 95f),
    card = hsl(240f, 12f, 13f),
    cardElevated = hsl(240f, 12f, 16f),
    primary = hsl(262f, 83f, 58f),
    primaryGlow = hsl(262f, 90f, 65f),
    primaryForeground = Color.White,
    secondary = hsl(240f, 12f, 18f),
    muted = hsl(240f, 10f, 20f),
    mutedForeground = hsl(240f, 5f, 55f),
    success = hsl(142f, 71f, 45f),
    warning = hsl(38f, 92f, 50f),
    destructive = hsl(0f, 72f, 51f),
    streak = hsl(25f, 95f, 53f),
    xp = hsl(199f, 89f, 48f),
    border = hsl(240f, 10f, 20f),
)

val LightPalette = CodeLingoPalette(
    background = hsl(240f, 25f, 97f),
    foreground = hsl(240f, 20f, 14f),
    card = Color.White,
    cardElevated = hsl(240f, 20f, 95f),
    primary = hsl(262f, 83f, 52f),
    primaryGlow = hsl(262f, 88f, 58f),
    primaryForeground = Color.White,
    secondary = hsl(240f, 18f, 92f),
    muted = hsl(240f, 15f, 90f),
    mutedForeground = hsl(240f, 8f, 42f),
    success = hsl(142f, 65f, 38f),
    warning = hsl(32f, 95f, 44f),
    destructive = hsl(0f, 72f, 48f),
    streak = hsl(22f, 92f, 48f),
    xp = hsl(199f, 85f, 42f),
    border = hsl(240f, 12f, 86f),
)
