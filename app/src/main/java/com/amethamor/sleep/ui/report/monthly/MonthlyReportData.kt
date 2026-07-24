package com.amethamor.sleep.ui.report.monthly

import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.ui.calendar.CalendarDayUiModel
import com.amethamor.sleep.ui.statistics.DailyDurationPoint
import com.amethamor.sleep.ui.statistics.DailyTrendPoint
import com.amethamor.sleep.ui.statistics.heatmap.HeatMapYearData
import java.time.YearMonth

data class MonthlyReportData(
    val yearMonth: YearMonth,
    val nightRecords: List<SleepRecord>,
    val napRecords: List<SleepRecord>,
    val summary: MonthlyReportSummaryData,
    val bedTrendPoints: List<DailyTrendPoint>,
    val wakeTrendPoints: List<DailyTrendPoint>,
    val nightDurationPoints: List<DailyDurationPoint>,
    val napDurationPoints: List<DailyDurationPoint>,
    val durationPieData: List<MonthlyReportPieSlice>,
    val wakeStatusPieData: List<MonthlyReportPieSlice>,
    val calendarDays: List<CalendarDayUiModel>,
    val heatMapData: MonthlyReportHeatMapData,
    val conclusion: String,
    val suggestions: List<String>
) {
    val hasAnyRecord: Boolean = nightRecords.isNotEmpty() || napRecords.isNotEmpty()
}

data class MonthlyReportSummaryData(
    val nightRecordDays: Int,
    val averageBedTimeText: String,
    val averageWakeTimeText: String,
    val averageNightDurationText: String,
    val napCount: Int,
    val averageNapDurationText: String,
    val averageBedTimeMinutes: Int?,
    val averageNightDurationMinutes: Int?,
    val averageNapDurationMinutes: Int?,
    val totalWakeUpCount: Int
)

data class MonthlyReportPieSlice(
    val label: String,
    val value: Int
)

data class MonthlyReportHeatMapData(
    val dream: HeatMapYearData,
    val nightmare: HeatMapYearData,
    val wakeUp: HeatMapYearData
)
