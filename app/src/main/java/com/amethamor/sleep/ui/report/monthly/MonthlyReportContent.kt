package com.amethamor.sleep.ui.report.monthly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun MonthlyReportContent(
    data: MonthlyReportData,
    exportMode: Boolean,
    modifier: Modifier = Modifier
) {
    val padding = if (exportMode) {
        PaddingValues(MonthlyReportDesignTokens.PagePadding)
    } else {
        PaddingValues(16.dp)
    }
    val itemSpacing = if (exportMode) MonthlyReportDesignTokens.SectionSpacing else 14.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SleepTheme.colors.background)
            .padding(padding),
        verticalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        MonthlyReportTitle(data)
        MonthlyReportSummarySection(data)
        MonthlyReportTrendSection(data)
        MonthlyReportNightDurationSection(data)
        MonthlyReportNapSection(data)
        MonthlyReportPieSection(data)
        MonthlyReportCalendarSection(data)
        MonthlyReportHeatMapSection(data)
        MonthlyReportConclusionSection(data)
    }
}
