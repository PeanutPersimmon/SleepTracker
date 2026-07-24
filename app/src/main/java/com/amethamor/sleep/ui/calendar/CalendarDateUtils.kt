package com.amethamor.sleep.ui.calendar

import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.ui.statistics.SleepStatisticsCalculator
import com.amethamor.sleep.ui.statistics.StatisticsFormatters
import com.amethamor.sleep.util.DateTimeUtils
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object CalendarDateUtils {
    private val monthFormatter = DateTimeFormatter.ofPattern("yyyy/MM", Locale.CHINA)

    fun currentMonth(): YearMonth = YearMonth.now()

    fun formatMonth(month: YearMonth): String = month.format(monthFormatter)

    fun buildMonthGrid(
        month: YearMonth,
        records: List<SleepRecord> = emptyList()
    ): List<CalendarDayUiModel> {
        val today = LocalDate.now()
        val firstDay = month.atDay(1)
        val firstDayOffset = firstDay.dayOfWeek.value - 1
        val daysInMonth = month.lengthOfMonth()
        val actualCells = firstDayOffset + daysInMonth
        val gridDayCount = (actualCells + 6) / 7 * 7

        val nightRecordsByDate = SleepStatisticsCalculator.selectNightRecords(records)
            .associateBy { it.recordDate }
        val napRecordsByDate = SleepStatisticsCalculator.selectNapRecords(records)
            .groupBy { it.recordDate }

        return List(gridDayCount) { index ->
            val dayNumber = index - firstDayOffset + 1
            if (dayNumber in 1..daysInMonth) {
                val date = month.atDay(dayNumber)
                val record = nightRecordsByDate[date.toString()]
                val naps = napRecordsByDate[date.toString()].orEmpty()
                val napTotalMinutes = naps.mapNotNull { it.durationMinutes }.sum().takeIf { it > 0 }
                CalendarDayUiModel(
                    date = date,
                    dayNumber = dayNumber,
                    isCurrentMonth = true,
                    isToday = date == today,
                    isFuture = date.isAfter(today),
                    bedTime = record?.bedTimeMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalTime()
                    },
                    wakeTime = record?.wakeTimeMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalTime()
                    },
                    durationMinutes = record?.durationMinutes,
                    napTotalMinutes = napTotalMinutes,
                    napCount = naps.size
                )
            } else {
                CalendarDayUiModel(
                    date = null,
                    dayNumber = null,
                    isCurrentMonth = false,
                    isToday = false,
                    isFuture = false,
                    bedTime = null,
                    wakeTime = null,
                    durationMinutes = null,
                    napTotalMinutes = null,
                    napCount = 0
                )
            }
        }
    }

    data class MonthStatistics(
        val averageBedTime: String?,
        val averageWakeTime: String?,
        val averageDuration: String?,
        val completeDays: Int,
        val incompleteDays: Int,
        val averageNapDuration: String?,
        val napCount: Int
    )

    fun calculateMonthStatistics(
        month: YearMonth,
        records: List<SleepRecord>
    ): MonthStatistics {
        val monthRecords = records.filter { record ->
            val recordDate = DateTimeUtils.parseRecordDateOrNull(record.recordDate)
            recordDate != null && recordDate.year == month.year && recordDate.monthValue == month.monthValue
        }
        val nightRecords = SleepStatisticsCalculator.selectNightRecords(monthRecords)
        val napRecords = SleepStatisticsCalculator.selectNapRecords(monthRecords)
        val averageBedTime = SleepStatisticsCalculator.calculateAverageBedTime(nightRecords)
            ?.let(StatisticsFormatters::formatTimeFromMinutes)
        val averageWakeTime = SleepStatisticsCalculator.calculateAverageWakeTime(nightRecords)
            ?.let(StatisticsFormatters::formatTimeFromMinutes)
        val averageDuration = SleepStatisticsCalculator.calculateAverageSleepDuration(nightRecords)
            ?.let(::formatNapDurationShort)
        val averageNapDuration = SleepStatisticsCalculator.calculateAverageNapDuration(napRecords)
            ?.let(::formatNapDurationShort)
        val completeDays = SleepStatisticsCalculator.countCompleteNightDays(nightRecords)
        val incompleteDays = SleepStatisticsCalculator.countRecordDays(nightRecords) - completeDays

        return MonthStatistics(
            averageBedTime = averageBedTime,
            averageWakeTime = averageWakeTime,
            averageDuration = averageDuration,
            completeDays = completeDays,
            incompleteDays = incompleteDays,
            averageNapDuration = averageNapDuration,
            napCount = napRecords.size
        )
    }

    fun formatNapDurationShort(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0 && mins > 0) {
            "${hours}h${mins}m"
        } else if (hours > 0) {
            "${hours}h"
        } else {
            "${mins}m"
        }
    }
}
