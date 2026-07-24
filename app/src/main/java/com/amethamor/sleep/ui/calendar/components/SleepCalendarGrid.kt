package com.amethamor.sleep.ui.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.calendar.CalendarDayUiModel
import com.amethamor.sleep.ui.calendar.CalendarMode
import com.amethamor.sleep.ui.components.SleepCard
import com.amethamor.sleep.ui.theme.SleepTheme

private val weekdayLabels = listOf(
    "\u4e00",
    "\u4e8c",
    "\u4e09",
    "\u56db",
    "\u4e94",
    "\u516d",
    "\u65e5"
)

@Composable
fun SleepCalendarGrid(
    days: List<CalendarDayUiModel>,
    mode: CalendarMode,
    modifier: Modifier = Modifier
) {
    val bottomPadding = if (mode == CalendarMode.Duration) 10.dp else 6.dp
    SleepCard(
        modifier = modifier.fillMaxWidth(),
        padding = PaddingValues(start = 6.dp, end = 6.dp, top = 10.dp, bottom = bottomPadding)
    ) {
        Column {
            WeekdayHeader()
            Spacer(modifier = Modifier.height(6.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                days.chunked(7).forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        week.forEach { day ->
                            CalendarDayCell(
                                day = day,
                                mode = mode,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekdayHeader() {
    val colors = SleepTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        weekdayLabels.forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                color = colors.textSecondary,
                style = MaterialTheme.typography.labelMedium,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}
