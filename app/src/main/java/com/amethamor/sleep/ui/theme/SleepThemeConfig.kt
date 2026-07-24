package com.amethamor.sleep.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val SleepCheckAppColorScheme = lightColorScheme(
    background = SleepBackground,
    surface = SleepSurface,
    primary = SleepPrimary,
    onPrimary = SleepSurface,
    primaryContainer = SleepPrimaryLight,
    onPrimaryContainer = SleepPrimaryDark,
    onBackground = SleepTextPrimary,
    onSurface = SleepTextPrimary,
    error = SleepDanger,
    onError = SleepSurface,
    secondary = SleepPrimaryLight,
    onSecondary = SleepPrimaryDark,
    outline = SleepDivider
)

val ThemeName = "晨雾蓝"