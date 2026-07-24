package com.amethamor.sleep.ui.report.monthly

import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.ui.calendar.CalendarDateUtils
import com.amethamor.sleep.ui.statistics.DailyTrendPoint
import com.amethamor.sleep.ui.statistics.SleepStatisticsCalculator
import com.amethamor.sleep.ui.statistics.StatisticsCalculators
import com.amethamor.sleep.ui.statistics.StatisticsDateUtils
import com.amethamor.sleep.ui.statistics.StatisticsFormatters
import com.amethamor.sleep.ui.statistics.StatisticsRecordUtils
import com.amethamor.sleep.ui.statistics.TrendWindow
import com.amethamor.sleep.ui.statistics.heatmap.HeatMapCalculators
import com.amethamor.sleep.ui.statistics.heatmap.HeatMapType
import com.amethamor.sleep.util.DateTimeUtils
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

object MonthlyReportCalculator {
    fun calculate(
        allRecords: List<SleepRecord>,
        yearMonth: YearMonth
    ): MonthlyReportData {
        val today = LocalDate.now()
        val monthRecords = allRecords.filter { record ->
            val date = record.localDateOrNull()
            date != null && YearMonth.from(date) == yearMonth && !date.isAfter(today)
        }
        val nightRecords = SleepStatisticsCalculator.selectNightRecords(monthRecords)
        val napRecords = SleepStatisticsCalculator.selectNapRecords(monthRecords)
        val latestNightByDate = StatisticsRecordUtils.latestRecordsByDate(nightRecords)
        val monthWindow = TrendWindow(yearMonth.atDay(1), yearMonth.atEndOfMonth().coerceAtMost(today))

        val summary = buildSummary(nightRecords, napRecords)
        val conclusion = MonthlyReportTextGenerator.conclusion(
            summary = summary,
            hasAnyRecord = nightRecords.isNotEmpty() || napRecords.isNotEmpty()
        )

        return MonthlyReportData(
            yearMonth = yearMonth,
            nightRecords = nightRecords,
            napRecords = napRecords,
            summary = summary,
            bedTrendPoints = buildBedTrendPoints(latestNightByDate, monthWindow),
            wakeTrendPoints = StatisticsCalculators.buildWakeTrendPoints(latestNightByDate, monthWindow),
            nightDurationPoints = StatisticsCalculators.buildDurationPoints(latestNightByDate, yearMonth),
            napDurationPoints = StatisticsCalculators.buildNapDurationPoints(napRecords, yearMonth),
            durationPieData = buildDurationPie(latestNightByDate.values.toList()),
            wakeStatusPieData = buildWakeStatusPie(latestNightByDate.values.toList()),
            calendarDays = CalendarDateUtils.buildMonthGrid(yearMonth, monthRecords),
            heatMapData = MonthlyReportHeatMapData(
                dream = HeatMapCalculators.buildYearData(nightRecords, yearMonth.year, HeatMapType.Dream),
                nightmare = HeatMapCalculators.buildYearData(nightRecords, yearMonth.year, HeatMapType.Nightmare),
                wakeUp = HeatMapCalculators.buildYearData(nightRecords, yearMonth.year, HeatMapType.WakeUp)
            ),
            conclusion = conclusion,
            suggestions = MonthlyReportTextGenerator.suggestions(summary)
        )
    }

    private fun buildSummary(
        nightRecords: List<SleepRecord>,
        napRecords: List<SleepRecord>
    ): MonthlyReportSummaryData {
        val averageBed = SleepStatisticsCalculator.calculateAverageBedTime(nightRecords)
        val averageWake = SleepStatisticsCalculator.calculateAverageWakeTime(nightRecords)
        val averageNight = SleepStatisticsCalculator.calculateAverageSleepDuration(nightRecords)
        val averageNap = SleepStatisticsCalculator.calculateAverageNapDuration(napRecords)

        return MonthlyReportSummaryData(
            nightRecordDays = SleepStatisticsCalculator.countRecordDays(nightRecords),
            averageBedTimeText = averageBed?.let(StatisticsFormatters::formatTimeFromMinutes) ?: "--",
            averageWakeTimeText = averageWake?.let(StatisticsFormatters::formatTimeFromMinutes) ?: "--",
            averageNightDurationText = averageNight?.let(::formatDuration) ?: "--",
            napCount = napRecords.size,
            averageNapDurationText = averageNap?.let(::formatDuration) ?: "--",
            averageBedTimeMinutes = averageBed,
            averageNightDurationMinutes = averageNight,
            averageNapDurationMinutes = averageNap,
            totalWakeUpCount = nightRecords.sumOf { it.wakeUpCount.coerceAtLeast(0) }
        )
    }

    private fun buildBedTrendPoints(
        latestByDate: Map<LocalDate, SleepRecord>,
        window: TrendWindow
    ): List<DailyTrendPoint> {
        return StatisticsDateUtils.datesInWindow(window).map { date ->
            val minutes = latestByDate[date]?.bedTimeMillis?.let(::sleepDayOffsetMinute)
            DailyTrendPoint(
                date = date,
                dateLabel = StatisticsFormatters.formatDayLabel(date),
                valueMinutes = minutes,
                valueLabel = minutes?.let(StatisticsFormatters::formatTimeFromMinutes)
            )
        }
    }

    private fun buildDurationPie(records: List<SleepRecord>): List<MonthlyReportPieSlice> {
        val buckets = linkedMapOf("<6h" to 0, "6-7h" to 0, "7-9h" to 0, "9-10h" to 0, ">10h" to 0)
        records.mapNotNull { it.durationMinutes }.forEach { minutes ->
            val key = when {
                minutes < 6 * 60 -> "<6h"
                minutes < 7 * 60 -> "6-7h"
                minutes <= 9 * 60 -> "7-9h"
                minutes <= 10 * 60 -> "9-10h"
                else -> ">10h"
            }
            buckets[key] = buckets.getValue(key) + 1
        }
        return buckets.map { MonthlyReportPieSlice(it.key, it.value) }.filter { it.value > 0 }
    }

    private fun buildWakeStatusPie(records: List<SleepRecord>): List<MonthlyReportPieSlice> {
        return records.mapNotNull { it.wakeFeeling?.takeIf(String::isNotBlank) }
            .groupingBy { it }
            .eachCount()
            .map { MonthlyReportPieSlice(it.key, it.value) }
    }

    private fun SleepRecord.localDateOrNull(): LocalDate? {
        return DateTimeUtils.parseRecordDateOrNull(recordDate)
    }

    private fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) "${hours}小时${mins}分" else "${mins}分钟"
    }

    private fun minuteOfDay(millis: Long): Int {
        val time = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalTime()
        return time.hour * 60 + time.minute
    }

    private fun sleepDayOffsetMinute(millis: Long): Int {
        val minute = minuteOfDay(millis)
        return if (minute < 12 * 60) minute + 24 * 60 else minute
    }
}

private fun LocalDate.coerceAtMost(maximum: LocalDate): LocalDate {
    return if (isAfter(maximum)) maximum else this
}
