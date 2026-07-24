package com.amethamor.sleep.ui.statistics

import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.data.SleepType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class SleepStatisticsCalculatorTest {
    @Test
    fun averageBedTime_unwrapsTimesAcrossMidnight() {
        val records = listOf(
            record("2026-07-01", bedTimeMillis = millis(2026, 6, 30, 23, 30)),
            record("2026-07-02", bedTimeMillis = millis(2026, 7, 2, 0, 30)),
            record("2026-07-03", bedTimeMillis = millis(2026, 7, 3, 1, 30))
        )

        assertEquals(30, SleepStatisticsCalculator.calculateAverageBedTime(records))
    }

    @Test
    fun nightStatistics_keepOnlyLatestUpdatedRecordForEachDate() {
        val older = record(
            date = "2026-07-01",
            bedTimeMillis = millis(2026, 6, 30, 23, 0),
            wakeTimeMillis = millis(2026, 7, 1, 6, 0),
            durationMinutes = 420,
            updatedAt = 1
        )
        val latest = record(
            date = "2026-07-01",
            bedTimeMillis = millis(2026, 6, 30, 23, 30),
            wakeTimeMillis = millis(2026, 7, 1, 8, 0),
            durationMinutes = 510,
            updatedAt = 2
        )

        assertEquals(listOf(latest), SleepStatisticsCalculator.selectNightRecords(listOf(older, latest)))
        assertEquals(510, SleepStatisticsCalculator.calculateAverageSleepDuration(listOf(older, latest)))
    }

    @Test
    fun averageNapDuration_keepsMultipleNapsOnSameDate() {
        val records = listOf(
            record("2026-07-01", durationMinutes = 30, sleepType = SleepType.NAP),
            record("2026-07-01", durationMinutes = 40, sleepType = SleepType.NAP)
        )

        assertEquals(35, SleepStatisticsCalculator.calculateAverageNapDuration(records))
        assertEquals(1, SleepStatisticsCalculator.countRecordDays(records))
    }

    @Test
    fun averages_skipOnlyRecordsMissingTheRequestedField() {
        val records = listOf(
            record("2026-05-01", bedTimeMillis = millis(2026, 4, 30, 23, 0)),
            record("2026-05-02", wakeTimeMillis = millis(2026, 5, 2, 7, 0))
        )

        assertEquals(23 * 60, SleepStatisticsCalculator.calculateAverageBedTime(records))
        assertEquals(7 * 60, SleepStatisticsCalculator.calculateAverageWakeTime(records))
    }

    @Test
    fun averages_areRoundedInsteadOfTruncated() {
        val records = listOf(
            record("2026-01-01", durationMinutes = 487),
            record("2026-01-02", durationMinutes = 488)
        )

        assertEquals(488, SleepStatisticsCalculator.calculateAverageSleepDuration(records))
    }

    @Test
    fun noonBedTime_isNotExpanded() {
        val records = listOf(
            record("2026-07-01", bedTimeMillis = millis(2026, 7, 1, 12, 0)),
            record("2026-07-02", bedTimeMillis = millis(2026, 7, 2, 23, 0))
        )

        assertEquals(17 * 60 + 30, SleepStatisticsCalculator.calculateAverageBedTime(records))
    }

    @Test
    fun elevenFiftyNineBedTime_isExpanded() {
        val records = listOf(
            record("2026-07-01", bedTimeMillis = millis(2026, 7, 1, 11, 59)),
            record("2026-07-02", bedTimeMillis = millis(2026, 7, 2, 23, 0))
        )

        assertEquals(5 * 60 + 30, SleepStatisticsCalculator.calculateAverageBedTime(records))
    }

    private fun record(
        date: String,
        bedTimeMillis: Long? = null,
        wakeTimeMillis: Long? = null,
        durationMinutes: Int? = null,
        sleepType: String = SleepType.NIGHT,
        updatedAt: Long = 1
    ) = SleepRecord(
        recordDate = date,
        bedTimeMillis = bedTimeMillis,
        wakeTimeMillis = wakeTimeMillis,
        durationMinutes = durationMinutes,
        sleepType = sleepType,
        createdAt = updatedAt,
        updatedAt = updatedAt
    )

    private fun millis(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        return LocalDateTime.of(year, month, day, hour, minute)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}
