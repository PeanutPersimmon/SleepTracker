package com.amethamor.sleep.ui.calendar

import java.time.LocalDate
import java.time.LocalTime

enum class CalendarMode(val label: String) {
    BedTime("\u5165\u7761"),
    WakeTime("\u8d77\u5e8a"),
    Duration("\u65f6\u957f"),
    Nap("\u5348\u7761")
}

data class CalendarDayUiModel(
    val date: LocalDate?,
    val dayNumber: Int?,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isFuture: Boolean,
    val bedTime: LocalTime?,
    val wakeTime: LocalTime?,
    val durationMinutes: Int?,
    val napTotalMinutes: Int? = null,
    val napCount: Int = 0
)
