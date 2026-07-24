package com.amethamor.sleep.util

import com.amethamor.sleep.data.SleepType
import java.util.Calendar

object SleepCalculateUtils {
    fun calculateDurationMinutes(
        bedTimeMillis: Long?,
        wakeTimeMillis: Long?
    ): Int? {
        if (bedTimeMillis == null || wakeTimeMillis == null) return null
        
        val diff = if (wakeTimeMillis >= bedTimeMillis) {
            wakeTimeMillis - bedTimeMillis
        } else {
            wakeTimeMillis + (24 * 60 * 60 * 1000L) - bedTimeMillis
        }
        
        return (diff / (60 * 1000)).toInt()
    }

    fun calculateRecordDate(
        bedTimeMillis: Long?,
        wakeTimeMillis: Long?
    ): String {
        return when {
            wakeTimeMillis != null -> DateTimeUtils.millisToDateString(wakeTimeMillis)
            bedTimeMillis != null -> DateTimeUtils.millisToDateString(bedTimeMillis)
            else -> DateTimeUtils.millisToDateString(DateTimeUtils.currentMillis())
        }
    }

    fun inferSleepType(
        bedTimeMillis: Long?,
        wakeTimeMillis: Long?,
        durationMinutes: Int?
    ): String {
        if (bedTimeMillis == null || wakeTimeMillis == null || durationMinutes == null) {
            return SleepType.NIGHT
        }

        val bedCalendar = Calendar.getInstance().apply { timeInMillis = bedTimeMillis }
        val wakeCalendar = Calendar.getInstance().apply { timeInMillis = wakeTimeMillis }
        val sameLocalDay =
            bedCalendar.get(Calendar.YEAR) == wakeCalendar.get(Calendar.YEAR) &&
                bedCalendar.get(Calendar.DAY_OF_YEAR) == wakeCalendar.get(Calendar.DAY_OF_YEAR)
        val bedHour = bedCalendar.get(Calendar.HOUR_OF_DAY)

        return if (sameLocalDay && durationMinutes < 240 && bedHour in 9..20) {
            SleepType.NAP
        } else {
            SleepType.NIGHT
        }
    }

    fun resolveSleepType(
        bedTimeMillis: Long?,
        wakeTimeMillis: Long?,
        durationMinutes: Int?,
        selectedSleepType: String,
        sleepTypeManuallySet: Boolean
    ): String {
        return if (sleepTypeManuallySet) {
            selectedSleepType
        } else {
            inferSleepType(bedTimeMillis, wakeTimeMillis, durationMinutes)
        }
    }
}
