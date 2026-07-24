package com.amethamor.sleep.ui.report.monthly

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.components.SleepCard
import com.amethamor.sleep.ui.statistics.charts.MiniBarChart
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun MonthlyReportTrendSection(data: MonthlyReportData) {
    ReportChartCard(title = "作息趋势") {
        if (data.bedTrendPoints.none { it.valueMinutes != null } &&
            data.wakeTrendPoints.none { it.valueMinutes != null }
        ) {
            EmptySectionText()
        } else {
            MonthlyReportTrendChart(
                bedPoints = data.bedTrendPoints,
                wakePoints = data.wakeTrendPoints,
                height = MonthlyReportDesignTokens.TrendChartHeight
            )
        }
    }
}

@Composable
fun MonthlyReportNightDurationSection(data: MonthlyReportData) {
    ReportChartCard(title = "夜间睡眠时长") {
        if (data.nightDurationPoints.none { it.durationMinutes != null }) {
            EmptySectionText()
        } else {
            ChartMetricText(
                points = data.nightDurationPoints.mapNotNull { it.durationMinutes },
                averageMinutes = data.summary.averageNightDurationMinutes
            )
            MiniBarChart(
                points = data.nightDurationPoints,
                height = MonthlyReportDesignTokens.NightBarChartHeight,
                maxDurationMinutes = data.sharedDurationAxisMax()
            )
        }
    }
}

@Composable
fun MonthlyReportNapSection(data: MonthlyReportData) {
    val colors = SleepTheme.colors
    ReportChartCard(title = "午睡趋势") {
        if (data.napDurationPoints.none { it.durationMinutes != null }) {
            EmptySectionText()
        } else {
            ChartMetricText(
                points = data.napDurationPoints.mapNotNull { it.durationMinutes },
                averageMinutes = data.summary.averageNapDurationMinutes
            )
            MiniBarChart(
                points = data.napDurationPoints,
                height = MonthlyReportDesignTokens.NapBarChartHeight,
                barColors = listOf(
                    colors.chartSecondary.copy(alpha = 0.90f),
                    colors.chartSecondary.copy(alpha = 0.08f)
                ),
                maxDurationMinutes = data.sharedDurationAxisMax()
            )
        }
    }
}

@Composable
fun MonthlyReportPieSection(data: MonthlyReportData) {
    Column(verticalArrangement = Arrangement.spacedBy(MonthlyReportDesignTokens.CardSpacing)) {
        MonthlyReportSectionTitle(text = "睡眠结构")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SleepCard(
                modifier = Modifier
                    .weight(1f)
                    .height(MonthlyReportDesignTokens.DonutCardHeight)
            ) {
                MonthlyReportPieChart(
                    title = "睡眠时长占比",
                    slices = data.durationPieData,
                    kind = MonthlyReportPieKind.Duration
                )
            }
            SleepCard(
                modifier = Modifier
                    .weight(1f)
                    .height(MonthlyReportDesignTokens.DonutCardHeight)
            ) {
                MonthlyReportPieChart(
                    title = "醒来状态占比",
                    slices = data.wakeStatusPieData,
                    kind = MonthlyReportPieKind.WakeStatus
                )
            }
        }
    }
}

private fun MonthlyReportData.sharedDurationAxisMax(): Int {
    val nightMax = nightDurationPoints.mapNotNull { it.durationMinutes }.maxOrNull() ?: 0
    val napMax = napDurationPoints.mapNotNull { it.durationMinutes }.maxOrNull() ?: 0
    return maxOf(nightMax, napMax, 12 * 60)
}

@Composable
private fun ChartMetricText(points: List<Int>, averageMinutes: Int?) {
    val max = points.maxOrNull() ?: 0
    Text(
        text = "平均 ${formatMinutes(averageMinutes ?: 0)} · 最高 ${formatMinutes(max)}",
        color = SleepTheme.colors.textSecondary,
        style = MaterialTheme.typography.bodySmall,
        fontSize = MonthlyReportDesignTokens.ChartCaptionFontSize,
        lineHeight = MonthlyReportDesignTokens.ChartCaptionLineHeight,
        maxLines = 1
    )
}

private fun formatMinutes(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0) "${hours}小时${mins}分" else "${mins}分钟"
}

@Composable
private fun ReportChartCard(
    title: String,
    content: @Composable () -> Unit
) {
    SleepCard {
        Column(verticalArrangement = Arrangement.spacedBy(MonthlyReportDesignTokens.CardInnerSpacing)) {
            MonthlyReportSectionTitle(text = title)
            content()
        }
    }
}

@Composable
fun EmptySectionText(text: String = "暂无数据") {
    Text(
        text = text,
        color = SleepTheme.colors.textSecondary,
        style = MaterialTheme.typography.bodyMedium,
        fontSize = 13.sp,
        lineHeight = 18.sp
    )
}
