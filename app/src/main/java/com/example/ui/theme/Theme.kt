package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MagicalColorScheme = darkColorScheme(
    primary = MagicCyan,
    onPrimary = MagicDeepNavy,
    primaryContainer = MagicCardBg,
    onPrimaryContainer = MagicCyan,
    secondary = MagicPurple,
    onSecondary = MagicDeepNavy,
    secondaryContainer = MagicCardBg,
    onSecondaryContainer = MagicPurple,
    tertiary = MagicGold,
    onTertiary = MagicDeepNavy,
    background = MagicDeepNavy,
    onBackground = MagicTextPrimary,
    surface = MagicSurface,
    onSurface = MagicTextPrimary,
    surfaceVariant = MagicCardBg,
    onSurfaceVariant = MagicTextSecondary,
    outline = MagicCardBorder,
    error = MagicCrimson,
    onError = MagicTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = MagicalColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
