package com.amethamor.sleep.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun currentMillis(): Long {
        return System.currentTimeMillis()
    }

    fun millisToDateString(millis: Long): String {
        return dateFormat.format(Date(millis))
    }

    fun parseRecordDateOrNull(date: String): LocalDate? {
        return runCatching { LocalDate.parse(date) }.getOrNull()
    }

    fun millisToTimeString(millis: Long?): String {
        if (millis == null) return "未记录"
        return timeFormat.format(Date(millis))
    }

    fun formatDuration(minutes: Int?): String {
        if (minutes == null) return "待补全"
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) {
            "${hours}小时${mins}分"
        } else {
            "${mins}分"
        }
    }
}
