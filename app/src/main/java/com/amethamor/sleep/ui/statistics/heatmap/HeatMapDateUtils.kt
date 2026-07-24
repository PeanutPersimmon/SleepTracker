package com.amethamor.sleep.ui.statistics.heatmap

import java.time.LocalDate
import java.time.Month
import java.time.Year

object HeatMapDateUtils {
    private val monthNames = listOf(
        "一月", "二月", "三月", "四月", "五月", "六月",
        "七月", "八月", "九月", "十月", "十一月", "十二月"
    )

    fun datesInYear(year: Int): List<LocalDate> {
        val days = Year.of(year).length()
        val firstDay = LocalDate.of(year, 1, 1)
        return (0 until days).map { firstDay.plusDays(it.toLong()) }
    }

    fun monthName(month: Month): String {
        return monthNames[month.value - 1]
    }

    fun formatYear(year: Int): String = "${year}年"

    fun buildYearWeekColumns(year: Int): List<HeatMapWeekColumn> {
        val firstDayOfYear = LocalDate.of(year, 1, 1)
        val lastDayOfYear = LocalDate.of(year, 12, 31)

        val startMonday = firstDayOfYear.minusDays(
            (firstDayOfYear.dayOfWeek.value - 1).toLong()
        )
        val endSunday = lastDayOfYear.plusDays(
            (7 - lastDayOfYear.dayOfWeek.value).toLong()
        )

        val weekColumns = mutableListOf<HeatMapWeekColumn>()
        var currentMonday = startMonday
        var weekIndex = 0

        while (!currentMonday.isAfter(endSunday)) {
            val dates = (0 until 7).map { dayOffset ->
                val date = currentMonday.plusDays(dayOffset.toLong())
                if (date.year == year) date else null
            }

            val monthLabel = getMonthLabelForWeek(dates, year)

            weekColumns.add(HeatMapWeekColumn(
                weekIndex = weekIndex,
                dates = dates,
                monthLabel = monthLabel
            ))

            currentMonday = currentMonday.plusWeeks(1)
            weekIndex++
        }

        return weekColumns
    }

    fun getMonthLabelForWeek(weekDates: List<LocalDate?>, year: Int): String? {
        for (date in weekDates) {
            if (date != null && date.year == year && date.dayOfMonth == 1) {
                return monthName(date.month)
            }
        }
        return null
    }

    fun findInitialWeekIndexForMonth(year: Int, month: Int): Int {
        val firstDayOfMonth = LocalDate.of(year, month, 1)

        val firstDayOfYear = LocalDate.of(year, 1, 1)
        val startMonday = firstDayOfYear.minusDays(
            (firstDayOfYear.dayOfWeek.value - 1).toLong()
        )

        val daysSinceStart = firstDayOfMonth.toEpochDay() - startMonday.toEpochDay()
        return (daysSinceStart / 7).toInt()
    }

    fun buildDayMap(days: List<HeatMapDay>): Map<LocalDate, HeatMapDay> {
        return days.associateBy { it.date }
    }
}
