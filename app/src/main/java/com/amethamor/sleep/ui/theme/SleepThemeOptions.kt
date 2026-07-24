package com.amethamor.sleep.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

data class SleepBaseTheme(
    val id: String,
    val name: String,
    val primary: Color,
    val backgroundTint: Color,
    val accent: Color
)

data class CustomSleepTheme(
    val id: String,
    val name: String,
    val primaryHex: String,
    val backgroundTintHex: String,
    val accentHex: String
)

fun CustomSleepTheme.toBaseTheme(): SleepBaseTheme {
    return SleepBaseTheme(
        id = id,
        name = name,
        primary = colorFromHex(primaryHex) ?: DefaultSleepBaseTheme.primary,
        backgroundTint = colorFromHex(backgroundTintHex) ?: DefaultSleepBaseTheme.backgroundTint,
        accent = colorFromHex(accentHex) ?: DefaultSleepBaseTheme.accent
    )
}

fun normalizeHexColor(input: String): String? {
    val trimmed = input.trim()
    val withoutHash = if (trimmed.startsWith("#")) trimmed.substring(1) else trimmed
    if (withoutHash.length != 6) return null
    if (!withoutHash.matches(Regex("^[0-9A-Fa-f]{6}$"))) return null
    return "#${withoutHash.uppercase()}"
}

fun colorFromHex(hex: String): Color? {
    val normalized = normalizeHexColor(hex) ?: return null
    return try {
        Color(android.graphics.Color.parseColor(normalized))
    } catch (e: Exception) {
        null
    }
}

fun Color.toHexString(): String {
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()
    return String.format("#%02X%02X%02X", red, green, blue)
}

data class SleepColorScheme(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val primaryDark: Color,
    val primaryLight: Color,
    val onPrimary: Color,
    val accent: Color,
    val accentLight: Color,
    val onAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val divider: Color,
    val outline: Color,
    val selectedBackground: Color,
    val selectedText: Color,
    val selectedBorder: Color,
    val chartPrimary: Color,
    val chartSecondary: Color,
    val calendarToday: Color,
    val calendarSelected: Color,
    val calendarMissing: Color,
    val calendarFuture: Color,
    val calendarFutureText: Color,
    val success: Color,
    val sleepNormal: Color,
    val warning: Color,
    val danger: Color,
    val sleepButton: Color,
    val onSleepButton: Color,
    val wakeButton: Color,
    val onWakeButton: Color,
    val dream: Color,
    val nightmare: Color,
    val awake: Color
)

val SleepThemeOptions = listOf(
    SleepBaseTheme(
        id = "morning_mist_blue",
        name = "晨雾蓝",
        primary = Color(0xFF8FA3C8),
        backgroundTint = Color(0xFFEEF3FA),
        accent = Color(0xFFB9C7E6)
    ),
    SleepBaseTheme(
        id = "mint_green",
        name = "薄荷绿",
        primary = Color(0xFF87B99A),
        backgroundTint = Color(0xFFEFF7F2),
        accent = Color(0xFFAED8C2)
    ),
    SleepBaseTheme(
        id = "ink_white",
        name = "墨白",
        primary = Color(0xFF111111),
        backgroundTint = Color(0xFFF6F6F6),
        accent = Color(0xFF666666)
    )
)

val DefaultSleepBaseTheme = SleepThemeOptions.first()

fun sleepBaseThemeOf(id: String?): SleepBaseTheme {
    return SleepThemeOptions.firstOrNull { it.id == id } ?: DefaultSleepBaseTheme
}

val DefaultSleepColorScheme = deriveSleepColorScheme(DefaultSleepBaseTheme)

fun deriveSleepColorScheme(baseTheme: SleepBaseTheme): SleepColorScheme {
    val primaryDark = darken(baseTheme.primary, 0.28f)
    val primaryLight = blend(baseTheme.primary, Color.White, 0.84f)
    val background = blend(baseTheme.backgroundTint, Color.White, 0.72f)
    val surface = Color.White
    val surfaceVariant = blend(baseTheme.backgroundTint, Color.White, 0.58f)
    val accentLight = blend(baseTheme.accent, Color.White, 0.46f)
    val sleepButton = primaryDark
    val wakeButton = wakeButtonFor(baseTheme)

    return SleepColorScheme(
        background = background,
        surface = surface,
        surfaceVariant = surfaceVariant,
        primary = baseTheme.primary,
        primaryDark = primaryDark,
        primaryLight = primaryLight,
        onPrimary = readableOn(baseTheme.primary),
        accent = baseTheme.accent,
        accentLight = accentLight,
        onAccent = readableOn(baseTheme.accent),
        textPrimary = Color(0xFF202124),
        textSecondary = Color(0xFF68707D),
        textTertiary = Color(0xFFA3AAB6),
        divider = blend(baseTheme.backgroundTint, Color(0xFFD8DEE8), 0.42f),
        outline = blend(baseTheme.backgroundTint, Color(0xFFC9D0DD), 0.50f),
        selectedBackground = accentLight,
        selectedText = primaryDark,
        selectedBorder = baseTheme.primary.copy(alpha = 0.55f),
        chartPrimary = primaryDark,
        chartSecondary = baseTheme.accent,
        calendarToday = baseTheme.accent,
        calendarSelected = baseTheme.primary,
        calendarMissing = blend(baseTheme.backgroundTint, Color.White, 0.64f),
        calendarFuture = blend(baseTheme.backgroundTint, Color.White, 0.82f),
        calendarFutureText = Color(0xFFA3AAB6).copy(alpha = 0.68f),
        success = blend(Color(0xFF6FA77A), baseTheme.accent, 0.12f),
        sleepNormal = Color(0xFF7FA6CF),
        warning = blend(Color(0xFFD7A25D), baseTheme.accent, 0.10f),
        danger = blend(Color(0xFFD86B6B), baseTheme.primary, 0.08f),
        sleepButton = sleepButton,
        onSleepButton = readableOn(sleepButton),
        wakeButton = wakeButton,
        onWakeButton = readableOn(wakeButton),
        dream = blend(Color(0xFF8D96C8), baseTheme.accent, 0.16f),
        nightmare = Color(0xFFB86A82),
        awake = blend(Color(0xFFD09A63), baseTheme.accent, 0.12f)
    )
}

fun blend(color1: Color, color2: Color, ratio: Float): Color {
    val amount = ratio.coerceIn(0f, 1f)
    val inverse = 1f - amount
    return Color(
        red = color1.red * inverse + color2.red * amount,
        green = color1.green * inverse + color2.green * amount,
        blue = color1.blue * inverse + color2.blue * amount,
        alpha = color1.alpha * inverse + color2.alpha * amount
    )
}

fun darken(color: Color, ratio: Float): Color {
    return blend(color, Color.Black, ratio)
}

fun lighten(color: Color, ratio: Float): Color {
    return blend(color, Color.White, ratio)
}

private fun wakeButtonFor(baseTheme: SleepBaseTheme): Color {
    return if (baseTheme.id == "ink_white") {
        Color(0xFF6F7782)
    } else {
        baseTheme.accent
    }
}

fun readableOn(color: Color): Color {
    val luminance = 0.299f * color.red + 0.587f * color.green + 0.114f * color.blue
    return if (luminance > 0.78f) Color(0xFF202124) else Color.White
}
