package com.amethamor.sleep.util

import com.amethamor.sleep.data.SleepType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class SleepCalculateUtilsTest {
    @Test
    fun editedCompleteRecord_recalculatesDurationAfterBedTimeChange() {
        val changedBedTime = millis(2026, 7, 23, 23, 30)
        val existingWakeTime = millis(2026, 7, 24, 8, 0)

        assertEquals(
            510,
            SleepCalculateUtils.calculateDurationMinutes(changedBedTime, existingWakeTime)
        )
    }

    @Test
    fun editedCompleteRecord_recalculatesDurationAfterWakeTimeChange() {
        val existingBedTime = millis(2026, 7, 23, 23, 0)
        val changedWakeTime = millis(2026, 7, 24, 8, 30)

        assertEquals(
            570,
            SleepCalculateUtils.calculateDurationMinutes(existingBedTime, changedWakeTime)
        )
    }

    @Test
    fun editedCrossDayRecord_usesWakeDateAsRecordDate() {
        val changedBedTime = millis(2026, 7, 31, 23, 30)
        val changedWakeTime = millis(2026, 8, 1, 7, 30)

        assertEquals(
            "2026-08-01",
            SleepCalculateUtils.calculateRecordDate(changedBedTime, changedWakeTime)
        )
    }

    @Test
    fun manuallyLockedNap_isNotOverriddenAfterTimeChange() {
        val changedBedTime = millis(2026, 7, 23, 23, 0)
        val changedWakeTime = millis(2026, 7, 24, 7, 0)
        val duration = SleepCalculateUtils.calculateDurationMinutes(changedBedTime, changedWakeTime)

        assertEquals(
            SleepType.NAP,
            SleepCalculateUtils.resolveSleepType(
                bedTimeMillis = changedBedTime,
                wakeTimeMillis = changedWakeTime,
                durationMinutes = duration,
                selectedSleepType = SleepType.NAP,
                sleepTypeManuallySet = true
            )
        )
    }

    private fun millis(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        return LocalDateTime.of(year, month, day, hour, minute)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}
