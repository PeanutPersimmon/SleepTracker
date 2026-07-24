package com.amethamor.sleep.ui.report.monthly

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
import com.amethamor.sleep.ui.calendar.CalendarMode
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun MonthlyReportCalendarSection(data: MonthlyReportData) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        MonthlyReportSectionTitle(text = "本月日历回顾")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MiniCalendarCard("入睡日历", CalendarMode.BedTime, data, Modifier.weight(1f))
            MiniCalendarCard("起床日历", CalendarMode.WakeTime, data, Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MiniCalendarCard("时长日历", CalendarMode.Duration, data, Modifier.weight(1f))
            MiniCalendarCard("午睡日历", CalendarMode.Nap, data, Modifier.weight(1f))
        }
    }
}

@Composable
private fun MiniCalendarCard(
    title: String,
    mode: CalendarMode,
    data: MonthlyReportData,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            color = SleepTheme.colors.textPrimary,
            style = MaterialTheme.typography.titleSmall,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        MonthlyMiniCalendar(days = data.calendarDays, mode = mode)
    }
}
