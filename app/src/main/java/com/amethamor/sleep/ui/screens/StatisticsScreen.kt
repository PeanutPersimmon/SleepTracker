package com.amethamor.sleep.ui.screens

import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.ui.SleepViewModel
import com.amethamor.sleep.ui.calendar.CalendarDateUtils
import com.amethamor.sleep.ui.components.SleepCard
import com.amethamor.sleep.ui.statistics.NapSummary
import com.amethamor.sleep.ui.statistics.SleepStatisticsCalculator
import com.amethamor.sleep.ui.statistics.StatisticsFormatters
import com.amethamor.sleep.ui.statistics.StatisticsCalculators
import com.amethamor.sleep.ui.statistics.StatisticsDateUtils
import com.amethamor.sleep.ui.statistics.StatisticsRecordUtils
import com.amethamor.sleep.ui.statistics.charts.MiniBarChart
import com.amethamor.sleep.ui.statistics.charts.MiniLineChart
import com.amethamor.sleep.ui.statistics.components.StatisticSectionTitle
import com.amethamor.sleep.ui.statistics.components.TrendChartCard
import com.amethamor.sleep.ui.statistics.components.YearHeatMapCard
import com.amethamor.sleep.ui.statistics.heatmap.HeatMapCalculators
import com.amethamor.sleep.ui.statistics.heatmap.HeatMapType
import com.amethamor.sleep.ui.theme.SleepTheme
import com.amethamor.sleep.util.DateTimeUtils
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun StatisticsScreen(viewModel: SleepViewModel) {
    val colors = SleepTheme.colors
    val allRecords by viewModel.allRecords.collectAsState()
    val currentMonth = remember { StatisticsDateUtils.getCurrentYearMonth() }

    var wakeChartMonth by remember { mutableStateOf(currentMonth) }
    var wakeTrendWindow by remember {
        mutableStateOf(StatisticsDateUtils.getDefaultWindowForMonth(currentMonth))
    }
    var bedChartMonth by remember { mutableStateOf(currentMonth) }
    var bedTrendWindow by remember {
        mutableStateOf(StatisticsDateUtils.getDefaultWindowForMonth(currentMonth))
    }
    var durationChartMonth by remember { mutableStateOf(currentMonth) }
    var dreamHeatMapYear by remember { mutableStateOf(currentMonth.year) }
    var wakeCountHeatMapYear by remember { mutableStateOf(currentMonth.year) }
    var nightmareHeatMapYear by remember { mutableStateOf(currentMonth.year) }
    var wakeFeelingHeatMapYear by remember { mutableStateOf(currentMonth.year) }

    val nightRecords = remember(allRecords) {
        SleepStatisticsCalculator.selectNightRecords(allRecords)
    }
    val napRecords = remember(allRecords) {
        SleepStatisticsCalculator.selectNapRecords(allRecords)
    }

    val latestByDate = remember(nightRecords) {
        StatisticsRecordUtils.latestRecordsByDate(nightRecords)
    }

    val wakePoints = remember(latestByDate, wakeTrendWindow) {
        StatisticsCalculators.buildWakeTrendPoints(latestByDate, wakeTrendWindow)
    }
    val bedPoints = remember(latestByDate, bedTrendWindow) {
        StatisticsCalculators.buildBedTrendPoints(latestByDate, bedTrendWindow)
    }
    val durationPoints = remember(latestByDate, durationChartMonth) {
        StatisticsCalculators.buildDurationPoints(latestByDate, durationChartMonth)
    }
    val napDurationPoints = remember(napRecords, durationChartMonth) {
        StatisticsCalculators.buildNapDurationPoints(napRecords, durationChartMonth)
    }
    val wakeAverageRecords = remember(latestByDate, wakeTrendWindow) {
        recordsInWindow(latestByDate, wakeTrendWindow)
    }
    val bedAverageRecords = remember(latestByDate, bedTrendWindow) {
        recordsInWindow(latestByDate, bedTrendWindow)
    }
    val durationAverageRecords = remember(latestByDate, durationChartMonth) {
        latestByDate.filterKeys { YearMonth.from(it) == durationChartMonth }.values.toList()
    }
    val visibleNapRecords = remember(napRecords, durationChartMonth) {
        napRecords.filter { record ->
            DateTimeUtils.parseRecordDateOrNull(record.recordDate)?.let(YearMonth::from) == durationChartMonth
        }
    }
    val napSummary = remember(visibleNapRecords) {
        StatisticsCalculators.napSummary(visibleNapRecords)
    }
    val dreamHeatMapData = remember(latestByDate, dreamHeatMapYear) {
        HeatMapCalculators.buildYearData(latestByDate, dreamHeatMapYear, HeatMapType.Dream)
    }
    val wakeCountHeatMapData = remember(latestByDate, wakeCountHeatMapYear) {
        HeatMapCalculators.buildYearData(latestByDate, wakeCountHeatMapYear, HeatMapType.WakeUp)
    }
    val nightmareHeatMapData = remember(latestByDate, nightmareHeatMapYear) {
        HeatMapCalculators.buildYearData(latestByDate, nightmareHeatMapYear, HeatMapType.Nightmare)
    }
    val wakeFeelingHeatMapData = remember(latestByDate, wakeFeelingHeatMapYear) {
        HeatMapCalculators.buildYearData(latestByDate, wakeFeelingHeatMapYear, HeatMapType.WakeFeeling)
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { StatisticSectionTitle(text = "趋势") }

        item {
            TrendChartCard(
                title = "起床时间分布",
                metaText = trendMetaText(wakeChartMonth, wakeTrendWindow),
                averageLabel = "平均起床时间",
                averageValue = SleepStatisticsCalculator.calculateAverageWakeTime(wakeAverageRecords)
                    ?.let(StatisticsFormatters::formatTimeFromMinutes) ?: "暂无数据",
                onPreviousClick = {
                    wakeTrendWindow = StatisticsDateUtils.moveSevenDayWindowAcrossMonths(
                        window = wakeTrendWindow,
                        direction = -1
                    )
                    wakeChartMonth = YearMonth.from(wakeTrendWindow.startDate)
                },
                onNextClick = {
                    wakeTrendWindow = StatisticsDateUtils.moveSevenDayWindowAcrossMonths(
                        window = wakeTrendWindow,
                        direction = 1
                    )
                    wakeChartMonth = YearMonth.from(wakeTrendWindow.startDate)
                }
            ) {
                MiniLineChart(points = wakePoints)
            }
        }

        item {
            TrendChartCard(
                title = "入睡时间分布",
                metaText = trendMetaText(bedChartMonth, bedTrendWindow),
                averageLabel = "平均入睡时间",
                averageValue = SleepStatisticsCalculator.calculateAverageBedTime(bedAverageRecords)
                    ?.let(StatisticsFormatters::formatTimeFromMinutes) ?: "暂无数据",
                onPreviousClick = {
                    bedTrendWindow = StatisticsDateUtils.moveSevenDayWindowAcrossMonths(
                        window = bedTrendWindow,
                        direction = -1
                    )
                    bedChartMonth = YearMonth.from(bedTrendWindow.startDate)
                },
                onNextClick = {
                    bedTrendWindow = StatisticsDateUtils.moveSevenDayWindowAcrossMonths(
                        window = bedTrendWindow,
                        direction = 1
                    )
                    bedChartMonth = YearMonth.from(bedTrendWindow.startDate)
                }
            ) {
                MiniLineChart(points = bedPoints)
            }
        }

        item {
            TrendChartCard(
                title = "睡眠时长分布",
                metaText = StatisticsDateUtils.formatYearMonth(durationChartMonth),
                averageLabel = "平均睡眠时长",
                averageValue = SleepStatisticsCalculator.calculateAverageSleepDuration(durationAverageRecords)
                    ?.let(StatisticsFormatters::formatDuration) ?: "暂无数据",
                onPreviousClick = { durationChartMonth = durationChartMonth.minusMonths(1) },
                onNextClick = { durationChartMonth = durationChartMonth.plusMonths(1) }
            ) {
                MiniBarChart(points = durationPoints)
            }
        }

        item { StatisticSectionTitle(text = "状态热力图") }

        item {
            YearHeatMapCard(
                title = "做梦热力图",
                type = HeatMapType.Dream,
                data = dreamHeatMapData,
                onPreviousClick = { dreamHeatMapYear -= 1 },
                onNextClick = { dreamHeatMapYear += 1 }
            )
        }

        item {
            YearHeatMapCard(
                title = "夜醒热力图",
                type = HeatMapType.WakeUp,
                data = wakeCountHeatMapData,
                onPreviousClick = { wakeCountHeatMapYear -= 1 },
                onNextClick = { wakeCountHeatMapYear += 1 }
            )
        }

        item {
            YearHeatMapCard(
                title = "噩梦热力图",
                type = HeatMapType.Nightmare,
                data = nightmareHeatMapData,
                onPreviousClick = { nightmareHeatMapYear -= 1 },
                onNextClick = { nightmareHeatMapYear += 1 }
            )
        }

        item {
            YearHeatMapCard(
                title = "醒来状态热力图",
                type = HeatMapType.WakeFeeling,
                data = wakeFeelingHeatMapData,
                onPreviousClick = { wakeFeelingHeatMapYear -= 1 },
                onNextClick = { wakeFeelingHeatMapYear += 1 }
            )
        }

        item { StatisticSectionTitle(text = "\u5348\u7761\u7edf\u8ba1") }

        item {
            TrendChartCard(
                title = "\u5348\u7761\u65f6\u957f\u5206\u5e03",
                metaText = StatisticsDateUtils.formatYearMonth(durationChartMonth),
                averageLabel = "\u5e73\u5747\u5348\u7761",
                averageValue = SleepStatisticsCalculator.calculateAverageNapDuration(visibleNapRecords)
                    ?.let(StatisticsFormatters::formatDuration) ?: "\u6682\u65e0\u6570\u636e",
                onPreviousClick = { durationChartMonth = durationChartMonth.minusMonths(1) },
                onNextClick = { durationChartMonth = durationChartMonth.plusMonths(1) }
            ) {
                MiniBarChart(
                    points = napDurationPoints,
                    height = 110.dp,
                    barColors = listOf(
                        colors.chartSecondary.copy(alpha = 0.90f),
                        colors.chartSecondary.copy(alpha = 0.08f)
                    )
                )
            }
        }

        item { NapSummaryCard(summary = napSummary) }

        item { RecordCount(records = nightRecords) }
        item { Spacer(modifier = Modifier.height(28.dp)) }
    }
}

private fun recordsInWindow(
    latestByDate: Map<LocalDate, SleepRecord>,
    window: com.amethamor.sleep.ui.statistics.TrendWindow
): List<SleepRecord> {
    return latestByDate.filterKeys { date ->
        !date.isBefore(window.startDate) && !date.isAfter(window.endDate)
    }.values.toList()
}

private fun trendMetaText(
    month: YearMonth,
    window: com.amethamor.sleep.ui.statistics.TrendWindow
): String {
    return "${StatisticsDateUtils.formatYearMonth(month)} · ${
        StatisticsDateUtils.formatWindowRange(window.startDate, window.endDate)
    }"
}

@Composable
private fun RecordCount(records: List<SleepRecord>) {
    Text(
        text = "已记录：${records.size} 条",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        color = SleepTheme.colors.textTertiary,
        style = MaterialTheme.typography.bodySmall,
        fontSize = 12.sp
    )
}

@Composable
private fun NapSummaryCard(summary: NapSummary) {
    SleepCard(padding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)) {
        val hasNap = summary.count > 0
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NapMetric(label = "\u6b21\u6570", value = if (hasNap) "${summary.count}\u6b21" else "--", modifier = Modifier.weight(1f))
            NapMetric(
                label = "\u5e73\u5747",
                value = summary.averageMinutes?.let { CalendarDateUtils.formatNapDurationShort(it) } ?: "--",
                modifier = Modifier.weight(1f)
            )
            NapMetric(label = "\u505a\u68a6", value = if (hasNap) "${summary.dreamCount}\u6b21" else "--", modifier = Modifier.weight(1f))
            NapMetric(label = "\u591c\u9192", value = if (hasNap) "${summary.wakeUpCount}\u6b21" else "--", modifier = Modifier.weight(1f))
            NapMetric(label = "\u5669\u68a6", value = if (hasNap) "${summary.nightmareCount}\u6b21" else "--", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun NapMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colors = SleepTheme.colors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            color = colors.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            color = colors.textSecondary,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )
    }
}
