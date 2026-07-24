package com.amethamor.sleep.ui.statistics

import java.time.LocalDate

object StatisticsFormatters {
    fun formatTimeFromMinutes(minutes: Int): String {
        val normalized = ((minutes % 1440) + 1440) % 1440
        val hour = normalized / 60
        val minute = normalized % 60
        return "%02d:%02d".format(hour, minute)
    }

    fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) {
            "${hours}小时${mins}分"
        } else {
            "${mins}分"
        }
    }

    fun formatDateLabel(date: LocalDate): String {
        return "${date.monthValue}-${date.dayOfMonth}"
    }

    fun formatDayLabel(date: LocalDate): String {
        return date.dayOfMonth.toString()
    }
}
