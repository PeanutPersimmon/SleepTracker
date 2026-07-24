package com.amethamor.sleep.ui.statistics.heatmap

import java.time.LocalDate

enum class HeatMapType {
    Dream,
    WakeUp,
    Nightmare,
    WakeFeeling
}

data class HeatMapDay(
    val date: LocalDate,
    val hasRecord: Boolean,
    val isFuture: Boolean,
    val level: Int?,
    val text: String? = null
)

data class HeatMapYearData(
    val year: Int,
    val days: List<HeatMapDay>
)

data class HeatMapWeekColumn(
    val weekIndex: Int,
    val dates: List<LocalDate?>,
    val monthLabel: String?
)
