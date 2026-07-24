package com.amethamor.sleep.ui.calendar.components

import androidx.compose.ui.graphics.Color
import com.amethamor.sleep.ui.theme.SleepColorScheme
import java.time.LocalTime

data class CalendarTimeVisual(
    val iconType: CalendarSleepIconType,
    val textColor: Color
)

fun calendarBedTimeVisual(time: LocalTime, colors: SleepColorScheme): CalendarTimeVisual {
    val sleepHour = if (time.hour < 12) time.hour + 24 else time.hour
    return when {
        sleepHour < 23 -> CalendarTimeVisual(CalendarSleepIconType.BedEarly, colors.success)
        sleepHour < 25 -> CalendarTimeVisual(CalendarSleepIconType.BedNormal, colors.sleepNormal)
        sleepHour < 27 -> CalendarTimeVisual(CalendarSleepIconType.BedLate, colors.warning)
        else -> CalendarTimeVisual(CalendarSleepIconType.BedVeryLate, colors.danger)
    }
}

fun calendarWakeTimeVisual(time: LocalTime, colors: SleepColorScheme): CalendarTimeVisual {
    return when {
        time.hour < 8 -> CalendarTimeVisual(CalendarSleepIconType.WakeEarly, colors.success)
        time.hour < 10 -> CalendarTimeVisual(CalendarSleepIconType.WakeNormal, colors.sleepNormal)
        time.hour < 12 -> CalendarTimeVisual(CalendarSleepIconType.WakeLate, colors.warning)
        else -> CalendarTimeVisual(CalendarSleepIconType.WakeVeryLate, colors.danger)
    }
}

fun calendarDurationTextColor(durationMinutes: Int, colors: SleepColorScheme): Color {
    return when {
        durationMinutes in (7 * 60)..(9 * 60) -> colors.success
        durationMinutes in (6 * 60) until (7 * 60) -> colors.sleepNormal
        durationMinutes in ((9 * 60) + 1)..(10 * 60) -> colors.sleepNormal
        else -> colors.danger
    }
}

fun calendarNapColor(totalMinutes: Int, colors: SleepColorScheme): Color {
    return when {
        totalMinutes <= 30 -> colors.success
        totalMinutes <= 60 -> colors.sleepNormal
        totalMinutes <= 90 -> colors.warning
        else -> colors.danger
    }
}

fun calendarNapIconType(totalMinutes: Int): CalendarSleepIconType {
    return when {
        totalMinutes <= 30 -> CalendarSleepIconType.WakeEarly
        totalMinutes <= 60 -> CalendarSleepIconType.WakeNormal
        totalMinutes <= 90 -> CalendarSleepIconType.WakeLate
        else -> CalendarSleepIconType.WakeVeryLate
    }
}
