package com.amethamor.sleep.ui.report.annual

data class AnnualSleepReport(
    val year: Int,
    val totalRecordDays: Int,
    val completeNightDays: Int,
    val completeRate: Float,
    val averageBedTimeText: String,
    val averageWakeTimeText: String,
    val averageNightDurationText: String,
    val nightDurationDistribution: List<PieSliceData>,
    val wakeStateDistribution: List<PieSliceData>,
    val wakeUpDays: Int,
    val wakeUpDaysRate: Float,
    val dreamDays: Int,
    val dreamDaysRate: Float,
    val nightmareDays: Int,
    val nightmareDaysRate: Float,
    val napDays: Int,
    val napDaysRate: Float,
    val averageNapDurationText: String,
    val napDurationDistribution: List<PieSliceData>,
    val sleepWakeSummary: List<String>,
    val annualSummary: List<String>
)

data class PieSliceData(
    val label: String,
    val value: Int,
    val percent: Float
)
