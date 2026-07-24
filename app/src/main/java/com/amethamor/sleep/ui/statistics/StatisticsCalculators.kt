package com.amethamor.sleep.ui.statistics

import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.data.SleepType
import com.amethamor.sleep.util.DateTimeUtils
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import kotlin.math.roundToInt

object StatisticsCalculators {
    fun buildWakeTrendPoints(
        latestByDate: Map<LocalDate, SleepRecord>,
        window: TrendWindow
    ): List<DailyTrendPoint> {
        return StatisticsDateUtils.datesInWindow(window).map { date ->
            val minutes = latestByDate[date]?.wakeTimeMillis?.let(::minuteOfDay)
            DailyTrendPoint(
                date = date,
                dateLabel = StatisticsFormatters.formatDateLabel(date),
                valueMinutes = minutes,
                valueLabel = minutes?.let(StatisticsFormatters::formatTimeFromMinutes)
            )
        }
    }

    fun buildBedTrendPoints(
        latestByDate: Map<LocalDate, SleepRecord>,
        window: TrendWindow
    ): List<DailyTrendPoint> {
        return StatisticsDateUtils.datesInWindow(window).map { date ->
            val minutes = latestByDate[date]?.bedTimeMillis?.let(::sleepDayOffsetMinute)
            DailyTrendPoint(
                date = date,
                dateLabel = StatisticsFormatters.formatDateLabel(date),
                valueMinutes = minutes,
                valueLabel = minutes?.let(StatisticsFormatters::formatTimeFromMinutes)
            )
        }
    }

    fun buildDurationPoints(
        latestByDate: Map<LocalDate, SleepRecord>,
        yearMonth: YearMonth
    ): List<DailyDurationPoint> {
        return StatisticsDateUtils.datesInMonth(yearMonth).map { date ->
            DailyDurationPoint(
                date = date,
                dayLabel = StatisticsFormatters.formatDayLabel(date),
                durationMinutes = latestByDate[date]?.durationMinutes
            )
        }
    }

    fun buildWakeTrendPoints(
        records: List<SleepRecord>,
        window: TrendWindow
    ): List<DailyTrendPoint> = buildWakeTrendPoints(StatisticsRecordUtils.latestRecordsByDate(records), window)

    fun buildBedTrendPoints(
        records: List<SleepRecord>,
        window: TrendWindow
    ): List<DailyTrendPoint> = buildBedTrendPoints(StatisticsRecordUtils.latestRecordsByDate(records), window)

    fun buildDurationPoints(
        records: List<SleepRecord>,
        yearMonth: YearMonth
    ): List<DailyDurationPoint> = buildDurationPoints(StatisticsRecordUtils.latestRecordsByDate(records), yearMonth)

    fun buildNapDurationPoints(
        records: List<SleepRecord>,
        yearMonth: YearMonth
    ): List<DailyDurationPoint> {
        val napMinutesByDate = records
            .filter { it.sleepType == SleepType.NAP }
            .mapNotNull { record ->
                val date = DateTimeUtils.parseRecordDateOrNull(record.recordDate)
                val duration = record.durationMinutes
                if (date != null && duration != null) date to duration else null
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, durations) -> durations.sum() }

        return StatisticsDateUtils.datesInMonth(yearMonth).map { date ->
            DailyDurationPoint(
                date = date,
                dayLabel = StatisticsFormatters.formatDayLabel(date),
                durationMinutes = napMinutesByDate[date]
            )
        }
    }

    fun napSummary(records: List<SleepRecord>): NapSummary {
        val napRecords = records.filter { it.sleepType == SleepType.NAP }
        return NapSummary(
            count = napRecords.size,
            averageMinutes = SleepStatisticsCalculator.calculateAverageNapDuration(napRecords),
            dreamCount = napRecords.count { it.hasDream },
            wakeUpCount = napRecords.sumOf { it.wakeUpCount },
            nightmareCount = napRecords.count { it.hasNightmare }
        )
    }

    fun averageTimeText(points: List<DailyTrendPoint>): String {
        val values = points.mapNotNull { it.valueMinutes }
        if (values.isEmpty()) return "暂无数据"
        return StatisticsFormatters.formatTimeFromMinutes(values.average().roundToInt())
    }

    fun averageDurationText(points: List<DailyDurationPoint>): String {
        val values = points.mapNotNull { it.durationMinutes }
        if (values.isEmpty()) return "暂无数据"
        return StatisticsFormatters.formatDuration(values.average().roundToInt())
    }

    private fun minuteOfDay(millis: Long): Int {
        val time = Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
        return time.hour * 60 + time.minute
    }

    private fun sleepDayOffsetMinute(millis: Long): Int {
        val minute = minuteOfDay(millis)
        return if (minute < 12 * 60) minute + 24 * 60 else minute
    }
}
