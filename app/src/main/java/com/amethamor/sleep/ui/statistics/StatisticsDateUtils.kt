package com.amethamor.sleep.ui.statistics

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

object StatisticsDateUtils {
    private val windowFormatter = DateTimeFormatter.ofPattern("M月d日")

    fun getCurrentYearMonth(): YearMonth = YearMonth.now()

    fun getDefaultWindowForMonth(
        yearMonth: YearMonth,
        today: LocalDate = LocalDate.now()
    ): TrendWindow {
        if (YearMonth.from(today) != yearMonth) {
            return getFirstWindowOfMonth(yearMonth)
        }

        val monthStart = yearMonth.atDay(1)
        val initialStart = today.minusDays(6)
        val start = initialStart.coerceAtLeast(monthStart)
        val end = if (initialStart.isBefore(monthStart)) {
            start.plusDays(6).coerceAtMost(yearMonth.atEndOfMonth())
        } else {
            today
        }
        return TrendWindow(startDate = start, endDate = end)
    }

    fun moveSevenDayWindowAcrossMonths(
        window: TrendWindow,
        direction: Int
    ): TrendWindow {
        val currentMonth = YearMonth.from(window.startDate)
        return when {
            direction < 0 && window.startDate.isAfter(currentMonth.atDay(1)) -> {
                val start = window.startDate.minusDays(7).coerceAtLeast(currentMonth.atDay(1))
                TrendWindow(
                    startDate = start,
                    endDate = start.plusDays(6).coerceAtMost(currentMonth.atEndOfMonth())
                )
            }
            direction < 0 -> getLastWindowOfMonth(currentMonth.minusMonths(1))
            direction > 0 && window.endDate.isBefore(currentMonth.atEndOfMonth()) -> {
                val start = window.startDate.plusDays(7)
                TrendWindow(
                    startDate = start,
                    endDate = start.plusDays(6).coerceAtMost(currentMonth.atEndOfMonth())
                )
            }
            direction > 0 -> getFirstWindowOfMonth(currentMonth.plusMonths(1))
            else -> window
        }
    }

    fun getFirstWindowOfMonth(yearMonth: YearMonth): TrendWindow {
        val start = yearMonth.atDay(1)
        val end = start.plusDays(6).coerceAtMost(yearMonth.atEndOfMonth())
        return TrendWindow(startDate = start, endDate = end)
    }

    fun getLastWindowOfMonth(yearMonth: YearMonth): TrendWindow {
        val end = yearMonth.atEndOfMonth()
        val start = end.minusDays(6).coerceAtLeast(yearMonth.atDay(1))
        return TrendWindow(startDate = start, endDate = end)
    }

    fun formatYearMonth(yearMonth: YearMonth): String {
        return "%04d年%02d月".format(yearMonth.year, yearMonth.monthValue)
    }

    fun formatWindowRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): String {
        return "${startDate.format(windowFormatter)} - ${endDate.format(windowFormatter)}"
    }

    fun datesInWindow(window: TrendWindow): List<LocalDate> {
        val days = java.time.temporal.ChronoUnit.DAYS.between(window.startDate, window.endDate)
        return (0..days).map { window.startDate.plusDays(it) }
    }

    fun datesInMonth(yearMonth: YearMonth): List<LocalDate> {
        return (1..yearMonth.lengthOfMonth()).map { day -> yearMonth.atDay(day) }
    }
}

private fun LocalDate.coerceAtLeast(minimum: LocalDate): LocalDate {
    return if (isBefore(minimum)) minimum else this
}

private fun LocalDate.coerceAtMost(maximum: LocalDate): LocalDate {
    return if (isAfter(maximum)) maximum else this
}
