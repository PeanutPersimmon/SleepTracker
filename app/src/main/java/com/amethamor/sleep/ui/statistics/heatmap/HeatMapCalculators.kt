package com.amethamor.sleep.ui.statistics.heatmap

import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.ui.statistics.StatisticsRecordUtils
import java.time.LocalDate

object HeatMapCalculators {
    fun buildYearData(
        latestByDate: Map<LocalDate, SleepRecord>,
        year: Int,
        type: HeatMapType
    ): HeatMapYearData {
        val today = LocalDate.now()
        val days = HeatMapDateUtils.datesInYear(year).map { date ->
            val record = latestByDate[date]
            val isFuture = date.isAfter(today)
            HeatMapDay(
                date = date,
                hasRecord = record != null,
                isFuture = isFuture,
                level = record?.let { levelFor(type, it) },
                text = record?.let { textFor(type, it) }
            )
        }
        return HeatMapYearData(year = year, days = days)
    }

    fun buildYearData(
        records: List<SleepRecord>,
        year: Int,
        type: HeatMapType
    ): HeatMapYearData = buildYearData(StatisticsRecordUtils.latestRecordsByDate(records), year, type)

    private fun levelFor(
        type: HeatMapType,
        record: SleepRecord
    ): Int? {
        return when (type) {
            HeatMapType.Dream -> if (record.hasDream) 1 else 0
            HeatMapType.WakeUp -> record.wakeUpCount.coerceAtLeast(0).coerceAtMost(3)
            HeatMapType.Nightmare -> if (record.hasNightmare) 1 else 0
            HeatMapType.WakeFeeling -> wakeFeelingLevel(record.wakeFeeling)
        }
    }

    private fun textFor(
        type: HeatMapType,
        record: SleepRecord
    ): String? {
        if (type != HeatMapType.WakeUp || record.wakeUpCount <= 0) return null
        return if (record.wakeUpCount >= 3) "3+" else record.wakeUpCount.toString()
    }

    private fun wakeFeelingLevel(value: String?): Int? {
        return when (value) {
            "清醒", "很好" -> 1
            "还行", "好" -> 2
            "一般" -> 3
            "困倦", "差" -> 4
            "很累", "很差" -> 5
            else -> null
        }
    }
}
