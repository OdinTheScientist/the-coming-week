package com.thecomingweek.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ColorScheme = darkColorScheme(
    primary = Blood,
    onPrimary = Bone,
    primaryContainer = Blood,
    onPrimaryContainer = Bone,
    secondary = Ember,
    onSecondary = Pitch,
    secondaryContainer = Ember,
    onSecondaryContainer = Pitch,
    tertiary = Ember,
    onTertiary = Pitch,
    tertiaryContainer = Ember,
    onTertiaryContainer = Pitch,
    error = Blood,
    onError = Bone,
    errorContainer = Blood,
    onErrorContainer = Bone,
    background = Pitch,
    onBackground = Bone,
    surface = Ash,
    onSurface = Bone,
    surfaceVariant = Ash,
    onSurfaceVariant = Bone,
    surfaceContainer = Ash,
    surfaceContainerLow = Ash,
    surfaceContainerLowest = Pitch,
    surfaceContainerHigh = Ash,
    surfaceContainerHighest = Ash,
    surfaceBright = Ash,
    surfaceDim = Pitch,
    surfaceTint = Pitch,
    inverseSurface = Bone,
    inverseOnSurface = Pitch,
    inversePrimary = Bone,
    outline = Bone.copy(alpha = 0.4f),
    outlineVariant = Bone.copy(alpha = 0.2f),
    scrim = Pitch,
)

@Composable
fun TheComingWeekTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}
