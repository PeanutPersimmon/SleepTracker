package com.amethamor.sleep.ui.calendar

import com.amethamor.sleep.ui.calendar.components.CalendarSleepIconType
import com.amethamor.sleep.ui.calendar.components.calendarBedTimeVisual
import com.amethamor.sleep.ui.calendar.components.calendarDurationTextColor
import com.amethamor.sleep.ui.calendar.components.calendarNapColor
import com.amethamor.sleep.ui.calendar.components.calendarNapIconType
import com.amethamor.sleep.ui.calendar.components.calendarWakeTimeVisual
import com.amethamor.sleep.ui.theme.DefaultSleepColorScheme
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalTime

class CalendarVisualRulesTest {
    private val colors = DefaultSleepColorScheme

    @Test
    fun bedTimeBoundaries_areUnchanged() {
        assertBedTime(22, 59, CalendarSleepIconType.BedEarly, colors.success)
        assertBedTime(23, 0, CalendarSleepIconType.BedNormal, colors.sleepNormal)
        assertBedTime(0, 59, CalendarSleepIconType.BedNormal, colors.sleepNormal)
        assertBedTime(1, 0, CalendarSleepIconType.BedLate, colors.warning)
        assertBedTime(2, 59, CalendarSleepIconType.BedLate, colors.warning)
        assertBedTime(3, 0, CalendarSleepIconType.BedVeryLate, colors.danger)
        assertBedTime(11, 59, CalendarSleepIconType.BedVeryLate, colors.danger)
        assertBedTime(12, 0, CalendarSleepIconType.BedEarly, colors.success)
    }

    @Test
    fun wakeTimeBoundaries_areUnchanged() {
        assertWakeTime(7, 59, CalendarSleepIconType.WakeEarly, colors.success)
        assertWakeTime(8, 0, CalendarSleepIconType.WakeNormal, colors.sleepNormal)
        assertWakeTime(9, 59, CalendarSleepIconType.WakeNormal, colors.sleepNormal)
        assertWakeTime(10, 0, CalendarSleepIconType.WakeLate, colors.warning)
        assertWakeTime(11, 59, CalendarSleepIconType.WakeLate, colors.warning)
        assertWakeTime(12, 0, CalendarSleepIconType.WakeVeryLate, colors.danger)
    }

    @Test
    fun durationBoundaries_areUnchanged() {
        assertEquals(colors.danger, calendarDurationTextColor(359, colors))
        assertEquals(colors.sleepNormal, calendarDurationTextColor(360, colors))
        assertEquals(colors.sleepNormal, calendarDurationTextColor(419, colors))
        assertEquals(colors.success, calendarDurationTextColor(420, colors))
        assertEquals(colors.success, calendarDurationTextColor(540, colors))
        assertEquals(colors.sleepNormal, calendarDurationTextColor(541, colors))
        assertEquals(colors.sleepNormal, calendarDurationTextColor(600, colors))
        assertEquals(colors.danger, calendarDurationTextColor(601, colors))
    }

    @Test
    fun napBoundaries_areUnchanged() {
        assertNap(30, CalendarSleepIconType.WakeEarly, colors.success)
        assertNap(31, CalendarSleepIconType.WakeNormal, colors.sleepNormal)
        assertNap(60, CalendarSleepIconType.WakeNormal, colors.sleepNormal)
        assertNap(61, CalendarSleepIconType.WakeLate, colors.warning)
        assertNap(90, CalendarSleepIconType.WakeLate, colors.warning)
        assertNap(91, CalendarSleepIconType.WakeVeryLate, colors.danger)
    }

    private fun assertBedTime(
        hour: Int,
        minute: Int,
        iconType: CalendarSleepIconType,
        color: androidx.compose.ui.graphics.Color
    ) {
        val visual = calendarBedTimeVisual(LocalTime.of(hour, minute), colors)
        assertEquals(iconType, visual.iconType)
        assertEquals(color, visual.textColor)
    }

    private fun assertWakeTime(
        hour: Int,
        minute: Int,
        iconType: CalendarSleepIconType,
        color: androidx.compose.ui.graphics.Color
    ) {
        val visual = calendarWakeTimeVisual(LocalTime.of(hour, minute), colors)
        assertEquals(iconType, visual.iconType)
        assertEquals(color, visual.textColor)
    }

    private fun assertNap(
        minutes: Int,
        iconType: CalendarSleepIconType,
        color: androidx.compose.ui.graphics.Color
    ) {
        assertEquals(iconType, calendarNapIconType(minutes))
        assertEquals(color, calendarNapColor(minutes, colors))
    }
}
