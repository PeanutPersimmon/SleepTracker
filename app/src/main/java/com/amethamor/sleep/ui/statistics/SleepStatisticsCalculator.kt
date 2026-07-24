package com.amethamor.sleep.ui.statistics

import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.data.SleepType
import java.time.Instant
import java.time.ZoneId
import kotlin.math.roundToInt

object SleepStatisticsCalculator {
    fun selectNightRecords(records: List<SleepRecord>): List<SleepRecord> {
        return records
            .asSequence()
            .filter { it.sleepType == SleepType.NIGHT }
            .groupBy { it.recordDate }
            .values
            .map { dailyRecords -> dailyRecords.maxBy { it.updatedAt } }
    }

    fun selectNapRecords(records: List<SleepRecord>): List<SleepRecord> {
        return records.filter { it.sleepType == SleepType.NAP }
    }

    fun calculateAverageBedTime(records: List<SleepRecord>): Int? {
        val minutes = selectNightRecords(records).mapNotNull { record ->
            record.bedTimeMillis?.let(::sleepDayOffsetMinute)
        }
        return minutes.averageRoundedOrNull()?.let(::normalizeMinuteOfDay)
    }

    fun calculateAverageWakeTime(records: List<SleepRecord>): Int? {
        val minutes = selectNightRecords(records).mapNotNull { record ->
            record.wakeTimeMillis?.let(::minuteOfDay)
        }
        return minutes.averageRoundedOrNull()
    }

    fun calculateAverageSleepDuration(records: List<SleepRecord>): Int? {
        return selectNightRecords(records)
            .mapNotNull { it.durationMinutes }
            .averageRoundedOrNull()
    }

    fun calculateAverageNapDuration(records: List<SleepRecord>): Int? {
        return selectNapRecords(records)
            .mapNotNull { it.durationMinutes }
            .averageRoundedOrNull()
    }

    fun countRecordDays(records: List<SleepRecord>): Int {
        return records.map { it.recordDate }.distinct().size
    }

    fun countCompleteNightDays(records: List<SleepRecord>): Int {
        return selectNightRecords(records).count { record ->
            record.bedTimeMillis != null &&
                record.wakeTimeMillis != null &&
                record.durationMinutes != null
        }
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

    private fun normalizeMinuteOfDay(minutes: Int): Int = ((minutes % 1440) + 1440) % 1440

    private fun List<Int>.averageRoundedOrNull(): Int? {
        return takeIf { it.isNotEmpty() }?.average()?.roundToInt()
    }
}
