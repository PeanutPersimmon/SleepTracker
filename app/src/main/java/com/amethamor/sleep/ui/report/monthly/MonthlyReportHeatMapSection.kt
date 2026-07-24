package com.amethamor.sleep.ui.report.monthly

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.ui.components.SleepCard
import com.amethamor.sleep.ui.statistics.heatmap.HeatMapColors
import com.amethamor.sleep.ui.statistics.heatmap.HeatMapDay
import com.amethamor.sleep.ui.statistics.heatmap.HeatMapType
import com.amethamor.sleep.util.DateTimeUtils
import com.amethamor.sleep.ui.theme.SleepTheme
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun MonthlyReportHeatMapSection(data: MonthlyReportData) {
    SleepCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(MonthlyReportDesignTokens.CardInnerSpacing)) {
            MonthlyReportSectionTitle(text = "睡眠状态热力图")
            MonthlyHeatMapRow("做梦", data.yearMonth, data.nightRecords, MonthlyHeatType.Dream)
            MonthlyHeatMapRow("噩梦", data.yearMonth, data.nightRecords, MonthlyHeatType.Nightmare)
            MonthlyHeatMapRow("夜醒", data.yearMonth, data.nightRecords, MonthlyHeatType.WakeUp)
        }
    }
}

@Composable
private fun MonthlyHeatMapRow(
    title: String,
    yearMonth: YearMonth,
    records: List<SleepRecord>,
    type: MonthlyHeatType
) {
    val latestByDate = remember(records) {
        records.mapNotNull { record ->
            DateTimeUtils.parseRecordDateOrNull(record.recordDate)?.let { it to record }
        }.groupBy({ it.first }, { it.second }).mapValues { (_, daily) -> daily.maxBy { it.createdAt } }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.width(MonthlyReportDesignTokens.HeatMapLabelWidth),
            color = SleepTheme.colors.textPrimary,
            style = MaterialTheme.typography.titleSmall,
            fontSize = MonthlyReportDesignTokens.HeatMapTitleFontSize,
            lineHeight = MonthlyReportDesignTokens.HeatMapTitleLineHeight,
            fontWeight = FontWeight.SemiBold
        )
        MonthlyHeatGrid(
            yearMonth = yearMonth, 
            latestByDate = latestByDate, 
            type = type,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MonthlyHeatGrid(
    yearMonth: YearMonth,
    latestByDate: Map<LocalDate, SleepRecord>,
    type: MonthlyHeatType,
    modifier: Modifier = Modifier
) {
    val days = (1..yearMonth.lengthOfMonth()).map { yearMonth.atDay(it) }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEach { date ->
            MonthlyHeatCell(
                date = date,
                record = latestByDate[date],
                type = type
            )
        }
    }
}

@Composable
private fun MonthlyHeatCell(
    date: LocalDate,
    record: SleepRecord?,
    type: MonthlyHeatType
) {
    val colors = SleepTheme.colors
    val level = record?.levelFor(type)
    val color = HeatMapColors.colorFor(
        type = type.toHeatMapType(),
        day = HeatMapDay(
            date = date,
            hasRecord = record != null,
            isFuture = date.isAfter(LocalDate.now()),
            level = level,
            text = null
        ),
        colors = colors
    )
    Box(
        modifier = Modifier
            .size(MonthlyReportDesignTokens.HeatMapCellSize)
            .clip(RoundedCornerShape(3.dp))
            .background(color)
    )
}

private enum class MonthlyHeatType {
    Dream,
    Nightmare,
    WakeUp
}

private fun SleepRecord.levelFor(type: MonthlyHeatType): Int {
    return when (type) {
        MonthlyHeatType.Dream -> if (hasDream) 1 else 0
        MonthlyHeatType.Nightmare -> if (hasNightmare) 1 else 0
        MonthlyHeatType.WakeUp -> wakeUpCount.coerceIn(0, 3)
    }
}

private fun MonthlyHeatType.toHeatMapType(): HeatMapType {
    return when (this) {
        MonthlyHeatType.Dream -> HeatMapType.Dream
        MonthlyHeatType.Nightmare -> HeatMapType.Nightmare
        MonthlyHeatType.WakeUp -> HeatMapType.WakeUp
    }
}
