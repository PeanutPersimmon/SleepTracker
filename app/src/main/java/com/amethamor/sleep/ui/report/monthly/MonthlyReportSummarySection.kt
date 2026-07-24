package com.amethamor.sleep.ui.report.monthly

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.components.SleepCard
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun MonthlyReportTitle(data: MonthlyReportData) {
    Text(
        text = "${data.yearMonth.year}年${data.yearMonth.monthValue}月睡眠月报",
        color = SleepTheme.colors.primaryDark,
        style = MaterialTheme.typography.headlineSmall,
        fontSize = MonthlyReportDesignTokens.ReportTitleFontSize,
        lineHeight = MonthlyReportDesignTokens.ReportTitleLineHeight,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun MonthlyReportSummarySection(data: MonthlyReportData) {
    SleepCard(padding = PaddingValues(MonthlyReportDesignTokens.CardPadding)) {
        Column(verticalArrangement = Arrangement.spacedBy(MonthlyReportDesignTokens.CardInnerSpacing)) {
            MonthlyReportSectionTitle(text = "本月概览")
            if (!data.hasAnyRecord) {
                Text(
                    text = "暂无记录",
                    color = SleepTheme.colors.textSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryRow(
                    SummaryMetric("夜晚记录", "${data.summary.nightRecordDays}天"),
                    SummaryMetric("平均入睡", data.summary.averageBedTimeText),
                    SummaryMetric("平均起床", data.summary.averageWakeTimeText)
                )
                SummaryRow(
                    SummaryMetric("平均睡眠", data.summary.averageNightDurationText),
                    SummaryMetric("午睡次数", "${data.summary.napCount}次"),
                    SummaryMetric("平均午睡", data.summary.averageNapDurationText)
                )
            }
        }
    }
}

@Composable
fun MonthlyReportSectionTitle(text: String) {
    Text(
        text = text,
        color = SleepTheme.colors.primaryDark,
        style = MaterialTheme.typography.titleMedium,
        fontSize = MonthlyReportDesignTokens.SectionTitleFontSize,
        lineHeight = MonthlyReportDesignTokens.SectionTitleLineHeight,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun SummaryRow(
    metric1: SummaryMetric,
    metric2: SummaryMetric,
    metric3: SummaryMetric
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryItem(label = metric1.label, value = metric1.value, modifier = Modifier.weight(1f))
        SummaryItem(label = metric2.label, value = metric2.value, modifier = Modifier.weight(1f))
        SummaryItem(label = metric3.label, value = metric3.value, modifier = Modifier.weight(1f))
    }
}

private data class SummaryMetric(val label: String, val value: String)

@Composable
private fun SummaryItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = value,
            color = SleepTheme.colors.textPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontSize = MonthlyReportDesignTokens.MetricValueFontSize,
            lineHeight = MonthlyReportDesignTokens.MetricValueLineHeight,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            color = SleepTheme.colors.textSecondary,
            style = MaterialTheme.typography.bodySmall,
            fontSize = MonthlyReportDesignTokens.MetricLabelFontSize,
            lineHeight = MonthlyReportDesignTokens.MetricLabelLineHeight
        )
    }
}
