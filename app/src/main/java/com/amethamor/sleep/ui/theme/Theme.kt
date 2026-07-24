package com.amethamor.sleep.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.material3.lightColorScheme

val LocalSleepColors = staticCompositionLocalOf { DefaultSleepColorScheme }

object SleepTheme {
    val colors: SleepColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalSleepColors.current
}

@Composable
fun SleepCheckAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    baseTheme: SleepBaseTheme = DefaultSleepBaseTheme,
    content: @Composable () -> Unit
) {
    val sleepColors = remember(baseTheme.id) {
        deriveSleepColorScheme(baseTheme)
    }

    val colorScheme = lightColorScheme(
        primary = sleepColors.primary,
        onPrimary = sleepColors.onPrimary,
        primaryContainer = sleepColors.primaryLight,
        onPrimaryContainer = sleepColors.primaryDark,
        secondary = sleepColors.accent,
        onSecondary = sleepColors.onAccent,
        secondaryContainer = sleepColors.accentLight,
        onSecondaryContainer = sleepColors.textPrimary,
        background = sleepColors.background,
        onBackground = sleepColors.textPrimary,
        surface = sleepColors.surface,
        onSurface = sleepColors.textPrimary,
        surfaceVariant = sleepColors.surfaceVariant,
        onSurfaceVariant = sleepColors.textSecondary,
        outline = sleepColors.outline,
        outlineVariant = sleepColors.divider,
        error = sleepColors.danger,
        onError = sleepColors.onPrimary
    )

    val view = LocalView.current
    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        window.statusBarColor = sleepColors.background.toArgb()
        window.navigationBarColor = sleepColors.surface.toArgb()
        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = !darkTheme
        controller.isAppearanceLightNavigationBars = !darkTheme
    }

    CompositionLocalProvider(LocalSleepColors provides sleepColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
