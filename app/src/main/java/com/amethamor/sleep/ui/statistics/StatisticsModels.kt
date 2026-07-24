package com.amethamor.sleep.ui.statistics

import java.time.LocalDate

data class TrendWindow(
    val startDate: LocalDate,
    val endDate: LocalDate
)

data class DailyTrendPoint(
    val date: LocalDate,
    val dateLabel: String,
    val valueMinutes: Int?,
    val valueLabel: String?
)

data class DailyDurationPoint(
    val date: LocalDate,
    val dayLabel: String,
    val durationMinutes: Int?
)

data class NapSummary(
    val count: Int,
    val averageMinutes: Int?,
    val dreamCount: Int,
    val wakeUpCount: Int,
    val nightmareCount: Int
)
