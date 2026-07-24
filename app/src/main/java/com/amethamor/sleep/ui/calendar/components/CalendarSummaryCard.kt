package com.amethamor.sleep.ui.calendar.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.calendar.CalendarDateUtils
import com.amethamor.sleep.ui.calendar.CalendarMode
import com.amethamor.sleep.ui.components.SleepCard
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun CalendarSummaryCard(
    mode: CalendarMode,
    statistics: CalendarDateUtils.MonthStatistics
) {
    SleepCard(
        modifier = Modifier.fillMaxWidth(),
        padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            when (mode) {
                CalendarMode.BedTime -> {
                    SummaryItem(
                        label = "平均入睡",
                        value = statistics.averageBedTime ?: "--",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryItem(
                        label = "完整记录",
                        value = "${statistics.completeDays}天",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryItem(
                        label = "有缺失",
                        value = "${statistics.incompleteDays}天",
                        modifier = Modifier.weight(1f)
                    )
                }
                CalendarMode.WakeTime -> {
                    SummaryItem(
                        label = "平均起床",
                        value = statistics.averageWakeTime ?: "--",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryItem(
                        label = "完整记录",
                        value = "${statistics.completeDays}天",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryItem(
                        label = "有缺失",
                        value = "${statistics.incompleteDays}天",
                        modifier = Modifier.weight(1f)
                    )
                }
                CalendarMode.Duration -> {
                    SummaryItem(
                        label = "平均时长",
                        value = statistics.averageDuration ?: "--",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryItem(
                        label = "完整记录",
                        value = "${statistics.completeDays}天",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryItem(
                        label = "有缺失",
                        value = "${statistics.incompleteDays}天",
                        modifier = Modifier.weight(1f)
                    )
                }
                CalendarMode.Nap -> {
                    SummaryItem(
                        label = "\u5e73\u5747\u5348\u7761",
                        value = statistics.averageNapDuration ?: "--",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryItem(
                        label = "\u5348\u7761\u6b21\u6570",
                        value = "${statistics.napCount}\u6b21",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colors = SleepTheme.colors
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = colors.textSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = value,
            color = colors.primaryDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
