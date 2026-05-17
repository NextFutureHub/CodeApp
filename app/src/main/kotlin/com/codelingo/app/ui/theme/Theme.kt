package com.codelingo.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private fun darkScheme() = darkColorScheme(
    primary = DarkPalette.primary,
    onPrimary = DarkPalette.primaryForeground,
    secondary = DarkPalette.secondary,
    background = DarkPalette.background,
    onBackground = DarkPalette.foreground,
    surface = DarkPalette.card,
    onSurface = DarkPalette.foreground,
    surfaceVariant = DarkPalette.cardElevated,
    onSurfaceVariant = DarkPalette.mutedForeground,
    error = DarkPalette.destructive,
    outline = DarkPalette.border,
)

private fun lightScheme() = lightColorScheme(
    primary = LightPalette.primary,
    onPrimary = LightPalette.primaryForeground,
    secondary = LightPalette.secondary,
    background = LightPalette.background,
    onBackground = LightPalette.foreground,
    surface = LightPalette.card,
    onSurface = LightPalette.foreground,
    surfaceVariant = LightPalette.cardElevated,
    onSurfaceVariant = LightPalette.mutedForeground,
    error = LightPalette.destructive,
    outline = LightPalette.border,
)

@Composable
fun CodeLingoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val palette = if (darkTheme) DarkPalette else LightPalette
    val colorScheme = if (darkTheme) darkScheme() else lightScheme()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalCodeLingoPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = CodeLingoTypography,
            content = content,
        )
    }
}
