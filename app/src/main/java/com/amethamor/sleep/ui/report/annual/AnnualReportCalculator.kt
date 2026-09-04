package com.amethamor.sleep.ui.report.annual

import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.ui.statistics.SleepStatisticsCalculator
import com.amethamor.sleep.util.DateTimeUtils

object AnnualReportCalculator {
    fun buildAnnualSleepReport(records: List<SleepRecord>, year: Int): AnnualSleepReport {
        val yearRecords = records.filter {
            DateTimeUtils.parseRecordDateOrNull(it.recordDate)?.year == year
        }
        val nightRecords = SleepStatisticsCalculator.selectNightRecords(yearRecords)
        val napRecords = SleepStatisticsCalculator.selectNapRecords(yearRecords)
        val completeNightRecords = nightRecords.filter {
            it.bedTimeMillis != null && it.wakeTimeMillis != null && it.durationMinutes != null
        }
        val allRecordDays = SleepStatisticsCalculator.countRecordDays(yearRecords)
        val totalRecordDays = SleepStatisticsCalculator.countRecordDays(nightRecords)
        val completeNightDays = SleepStatisticsCalculator.countCompleteNightDays(nightRecords)
        val completeRate = ratio(completeNightDays, totalRecordDays)
        val averageBedMinutes = SleepStatisticsCalculator.calculateAverageBedTime(nightRecords)
        val averageWakeMinutes = SleepStatisticsCalculator.calculateAverageWakeTime(nightRecords)
        val averageNightDuration = SleepStatisticsCalculator.calculateAverageSleepDuration(nightRecords)
        val nightDurationDistribution = AnnualReportTextUtils.buildPieSlices(
            listOf(
                "<6h" to completeNightRecords.count { (it.durationMinutes ?: 0) < 6 * 60 },
                "6-7h" to completeNightRecords.count { (it.durationMinutes ?: 0) in (6 * 60) until (7 * 60) },
                "7-9h" to completeNightRecords.count { (it.durationMinutes ?: 0) in (7 * 60) until (9 * 60) },
                "9-10h" to completeNightRecords.count { (it.durationMinutes ?: 0) in (9 * 60) until (10 * 60) },
                ">10h" to completeNightRecords.count { (it.durationMinutes ?: 0) >= 10 * 60 }
            )
        )
        val wakeStateDistribution = AnnualReportTextUtils.buildPieSlices(
            listOf(
                "清醒" to nightRecords.count { normalizeWakeFeeling(it.wakeFeeling) == "清醒" },
                "一般" to nightRecords.count { normalizeWakeFeeling(it.wakeFeeling) == "一般" },
                "困倦" to nightRecords.count { normalizeWakeFeeling(it.wakeFeeling) == "困倦" },
                "疲惫" to nightRecords.count { normalizeWakeFeeling(it.wakeFeeling) == "疲惫" }
            )
        )
        val wakeUpDays = nightRecords.filter { it.wakeUpCount > 0 }.map { it.recordDate }.distinct().size
        val dreamDays = nightRecords.filter { it.hasDream }.map { it.recordDate }.distinct().size
        val nightmareDays = nightRecords.filter { it.hasNightmare }.map { it.recordDate }.distinct().size
        val napDays = napRecords.map { it.recordDate }.distinct().size
        val averageNapDuration = SleepStatisticsCalculator.calculateAverageNapDuration(napRecords)
        val napDurationDistribution = AnnualReportTextUtils.buildPieSlices(
            listOf(
                "0-30 分钟" to napRecords.count { (it.durationMinutes ?: -1) in 0..30 },
                "30-60 分钟" to napRecords.count { (it.durationMinutes ?: -1) in 31..60 },
                "60-90 分钟" to napRecords.count { (it.durationMinutes ?: -1) in 61..90 },
                "90 分钟以上" to napRecords.count { (it.durationMinutes ?: -1) > 90 }
            )
        )
        val dreamRate = ratio(dreamDays, completeNightDays)
        val nightmareRate = ratio(nightmareDays, completeNightDays)
        val summaryInput = AnnualSummaryInput(
            totalRecordDays = totalRecordDays,
            completeNightDays = completeNightDays,
            completeRate = completeRate,
            mainNightDurationLabel = nightDurationDistribution.maxByOrNull { it.value }?.label,
            dreamDaysRate = dreamRate,
            nightmareDaysRate = nightmareRate,
            napDays = napDays,
            mainNapDurationLabel = napDurationDistribution.maxByOrNull { it.value }?.label
        )
        return AnnualSleepReport(
            year = year,
            totalRecordDays = totalRecordDays,
            completeNightDays = completeNightDays,
            completeRate = completeRate,
            averageBedTimeText = AnnualReportTextUtils.formatTimeFromMinutes(averageBedMinutes),
            averageWakeTimeText = AnnualReportTextUtils.formatTimeFromMinutes(averageWakeMinutes),
            averageNightDurationText = AnnualReportTextUtils.formatDuration(averageNightDuration),
            nightDurationDistribution = nightDurationDistribution,
            wakeStateDistribution = wakeStateDistribution,
            wakeUpDays = wakeUpDays,
            wakeUpDaysRate = ratio(wakeUpDays, completeNightDays),
            dreamDays = dreamDays,
            dreamDaysRate = dreamRate,
            nightmareDays = nightmareDays,
            nightmareDaysRate = nightmareRate,
            napDays = napDays,
            napDaysRate = ratio(napDays, allRecordDays),
            averageNapDurationText = AnnualReportTextUtils.formatNapDuration(averageNapDuration),
            napDurationDistribution = napDurationDistribution,
            sleepWakeSummary = AnnualReportTextUtils.buildSleepWakeSummary(
                averageBedMinutes = averageBedMinutes,
                averageWakeMinutes = averageWakeMinutes,
                totalRecordDays = totalRecordDays,
                completeNightDays = completeNightDays
            ),
            annualSummary = AnnualReportTextUtils.buildAnnualSummary(summaryInput)
        )
    }

    private fun ratio(value: Int, total: Int): Float = if (total > 0) value.toFloat() / total else 0f

    private fun normalizeWakeFeeling(value: String?): String? {
        return when (value?.trim()) {
            "清醒", "很好" -> "清醒"
            "还行", "一般" -> "一般"
            "困倦", "差" -> "困倦"
            "很累", "疲惫" -> "疲惫"
            else -> null
        }
    }
}
