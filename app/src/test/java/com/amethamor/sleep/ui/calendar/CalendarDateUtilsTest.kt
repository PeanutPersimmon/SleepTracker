package com.amethamor.sleep.ui.calendar

import com.amethamor.sleep.data.SleepRecord
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.YearMonth

class CalendarDateUtilsTest {
    @Test
    fun calculateMonthStatistics_skipsInvalidRecordDateWithoutCrashing() {
        val invalidRecord = SleepRecord(
            recordDate = "2026-99-99",
            bedTimeMillis = null,
            wakeTimeMillis = null,
            durationMinutes = null,
            createdAt = 1,
            updatedAt = 1
        )

        val statistics = CalendarDateUtils.calculateMonthStatistics(
            month = YearMonth.of(2026, 7),
            records = listOf(invalidRecord)
        )

        assertEquals(0, statistics.completeDays)
        assertEquals(0, statistics.incompleteDays)
        assertEquals(0, statistics.napCount)
    }
}
