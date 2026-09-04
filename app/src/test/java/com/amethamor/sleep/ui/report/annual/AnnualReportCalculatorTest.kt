package com.amethamor.sleep.ui.report.annual

import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.data.SleepType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AnnualReportCalculatorTest {
    @Test
    fun nightAndNapOnSameDate_countAsOneNightRecordDay() {
        val records = listOf(
            nightRecord(date = "2026-01-01", updatedAt = 1),
            napRecord(date = "2026-01-01", durationMinutes = 30, updatedAt = 2),
            nightRecord(date = "2026-01-02", updatedAt = 3)
        )

        val report = AnnualReportCalculator.buildAnnualSleepReport(records, 2026)

        assertEquals(2, report.totalRecordDays)
        assertEquals(2, report.completeNightDays)
        assertEquals(1, report.napDays)
    }

    @Test
    fun napOnlyDate_isExcludedFromNightRecordDays() {
        val records = listOf(
            nightRecord(date = "2026-01-01", updatedAt = 1),
            napRecord(date = "2026-01-02", durationMinutes = 30, updatedAt = 2)
        )

        val report = AnnualReportCalculator.buildAnnualSleepReport(records, 2026)

        assertEquals(1, report.totalRecordDays)
        assertEquals(1, report.completeNightDays)
        assertEquals(1, report.napDays)
    }

    @Test
    fun completionRate_usesNightRecordDaysOnly() {
        val records = listOf(
            nightRecord(date = "2026-01-01", updatedAt = 1),
            nightRecord(date = "2026-01-02", complete = false, updatedAt = 2),
            nightRecord(date = "2026-01-03", updatedAt = 3),
            napRecord(date = "2026-01-04", durationMinutes = 30, updatedAt = 4),
            napRecord(date = "2026-01-05", durationMinutes = 60, updatedAt = 5)
        )

        val report = AnnualReportCalculator.buildAnnualSleepReport(records, 2026)

        assertEquals(3, report.totalRecordDays)
        assertEquals(2, report.completeNightDays)
        assertEquals(2f / 3f, report.completeRate, 0.0001f)
    }

    @Test
    fun napOnlyYear_hasSafeNightMetricsAndKeepsNapStatistics() {
        val records = listOf(
            napRecord(date = "2026-01-01", durationMinutes = 30, updatedAt = 1),
            napRecord(date = "2026-01-01", durationMinutes = 45, updatedAt = 2),
            napRecord(date = "2026-01-02", durationMinutes = 60, updatedAt = 3)
        )

        val report = AnnualReportCalculator.buildAnnualSleepReport(records, 2026)

        assertEquals(0, report.totalRecordDays)
        assertEquals(0, report.completeNightDays)
        assertEquals(0f, report.completeRate, 0f)
        assertFalse(report.completeRate.isNaN())
        assertEquals(2, report.napDays)
        assertEquals(1f, report.napDaysRate, 0f)
        assertEquals("45分钟", report.averageNapDurationText)
        assertFalse(report.napDurationDistribution.isEmpty())
    }

    @Test
    fun buildAnnualSleepReport_usesOnlyRecordsFromRequestedYear() {
        val records = listOf(
            nightRecord(date = "2025-01-02", durationMinutes = 480, updatedAt = 1),
            napRecord(date = "2025-06-03", durationMinutes = 30, updatedAt = 2),
            nightRecord(date = "2026-01-02", durationMinutes = 600, updatedAt = 3)
        )

        val report = AnnualReportCalculator.buildAnnualSleepReport(records, 2025)

        assertEquals(2025, report.year)
        assertEquals(1, report.totalRecordDays)
        assertEquals(1, report.completeNightDays)
        assertEquals(1, report.napDays)
        assertEquals("8小时0分", report.averageNightDurationText)
    }

    @Test
    fun annualReportCopy_doesNotReferToCurrentYear() {
        val lines = AnnualReportTextUtils.buildSleepWakeSummary(
            averageBedMinutes = null,
            averageWakeMinutes = null,
            totalRecordDays = 0
        ) + AnnualReportTextUtils.buildAnnualSummary(
            AnnualSummaryInput(
                totalRecordDays = 0,
                completeNightDays = 0,
                completeRate = 0f,
                mainNightDurationLabel = null,
                dreamDaysRate = 0f,
                nightmareDaysRate = 0f,
                napDays = 0,
                mainNapDurationLabel = null
            )
        )

        assertFalse(lines.any { "今年" in it })
    }

    private fun nightRecord(
        date: String,
        durationMinutes: Int = 480,
        complete: Boolean = true,
        updatedAt: Long
    ) = SleepRecord(
        recordDate = date,
        bedTimeMillis = if (complete) 1L else null,
        wakeTimeMillis = if (complete) 2L else null,
        durationMinutes = if (complete) durationMinutes else null,
        sleepType = SleepType.NIGHT,
        createdAt = updatedAt,
        updatedAt = updatedAt
    )

    private fun napRecord(
        date: String,
        durationMinutes: Int,
        updatedAt: Long
    ) = SleepRecord(
        recordDate = date,
        bedTimeMillis = null,
        wakeTimeMillis = null,
        durationMinutes = durationMinutes,
        sleepType = SleepType.NAP,
        createdAt = updatedAt,
        updatedAt = updatedAt
    )
}
