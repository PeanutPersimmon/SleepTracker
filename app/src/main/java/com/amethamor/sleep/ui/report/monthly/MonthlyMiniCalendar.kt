package com.amethamor.sleep.ui.report.monthly

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.calendar.CalendarDayUiModel
import com.amethamor.sleep.ui.calendar.CalendarMode
import com.amethamor.sleep.ui.calendar.components.CalendarSleepIcon
import com.amethamor.sleep.ui.components.SleepCard
import com.amethamor.sleep.ui.calendar.components.calendarBedTimeVisual
import com.amethamor.sleep.ui.calendar.components.calendarDurationTextColor
import com.amethamor.sleep.ui.calendar.components.calendarNapColor
import com.amethamor.sleep.ui.calendar.components.calendarNapIconType
import com.amethamor.sleep.ui.calendar.components.calendarWakeTimeVisual
import com.amethamor.sleep.ui.theme.SleepTheme
import java.time.LocalTime

private val weekdayLabels = listOf("一", "二", "三", "四", "五", "六", "日")

@Composable
fun MonthlyMiniCalendar(
    days: List<CalendarDayUiModel>,
    mode: CalendarMode,
    modifier: Modifier = Modifier
) {
    SleepCard(
        modifier = modifier.fillMaxWidth(),
        padding = androidx.compose.foundation.layout.PaddingValues(8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                weekdayLabels.forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        color = SleepTheme.colors.textSecondary,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = MonthlyReportDesignTokens.CalendarTextFontSize,
                        lineHeight = MonthlyReportDesignTokens.CalendarTextLineHeight,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
            days.chunked(7).forEach { week ->
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    week.forEach { day ->
                        MiniCalendarCell(day = day, mode = mode, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniCalendarCell(
    day: CalendarDayUiModel,
    mode: CalendarMode,
    modifier: Modifier = Modifier
) {
    val valueText = when (mode) {
        CalendarMode.BedTime -> day.bedTime?.formatShort()
        CalendarMode.WakeTime -> day.wakeTime?.formatShort()
        CalendarMode.Duration -> day.durationMinutes?.let(::formatShortDuration)
        CalendarMode.Nap -> day.napTotalMinutes?.let(::formatShortDuration)
    }

    Box(
        modifier = modifier
            .aspectRatio(1.05f)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (day.dayNumber != null && valueText != null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                MiniCalendarVisual(day = day, mode = mode)
                Text(
                    text = valueText,
                    modifier = Modifier.fillMaxWidth(),
                    color = valueColor(day = day, mode = mode),
                    fontSize = MonthlyReportDesignTokens.CalendarTextFontSize,
                    lineHeight = MonthlyReportDesignTokens.CalendarTextLineHeight,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun MiniCalendarVisual(day: CalendarDayUiModel, mode: CalendarMode) {
    val colors = SleepTheme.colors
    when (mode) {
        CalendarMode.BedTime -> day.bedTime?.let { time ->
            val visual = calendarBedTimeVisual(time, colors)
            CalendarSleepIcon(type = visual.iconType, color = visual.textColor, size = 18.dp)
        }
        CalendarMode.WakeTime -> day.wakeTime?.let { time ->
            val visual = calendarWakeTimeVisual(time, colors)
            CalendarSleepIcon(type = visual.iconType, color = visual.textColor, size = 18.dp)
        }
        CalendarMode.Duration -> day.durationMinutes?.let { DurationMiniRing(it) }
        CalendarMode.Nap -> day.napTotalMinutes?.let { totalMinutes ->
            CalendarSleepIcon(
                type = calendarNapIconType(totalMinutes),
                color = calendarNapColor(totalMinutes, colors),
                size = 18.dp
            )
        }
    }
}

@Composable
private fun DurationMiniRing(durationMinutes: Int) {
    val colors = SleepTheme.colors
    val ringColor = calendarDurationTextColor(durationMinutes, colors)
    Canvas(modifier = Modifier.size(18.dp)) {
        val strokeWidth = 3.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2f
        val center = Offset(size.width / 2f, size.height / 2f)
        drawCircle(
            color = colors.calendarMissing.copy(alpha = 0.72f),
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        drawArc(
            color = ringColor,
            startAngle = -90f,
            sweepAngle = 360f * (durationMinutes.toFloat() / (10 * 60)).coerceIn(0f, 1f),
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2f, radius * 2f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun valueColor(day: CalendarDayUiModel, mode: CalendarMode) = when (mode) {
    CalendarMode.BedTime -> day.bedTime?.let { calendarBedTimeVisual(it, SleepTheme.colors).textColor }
    CalendarMode.WakeTime -> day.wakeTime?.let { calendarWakeTimeVisual(it, SleepTheme.colors).textColor }
    CalendarMode.Duration -> day.durationMinutes?.let { calendarDurationTextColor(it, SleepTheme.colors) }
    CalendarMode.Nap -> day.napTotalMinutes?.let { calendarNapColor(it, SleepTheme.colors) }
} ?: SleepTheme.colors.textPrimary

private fun LocalTime.formatShort(): String = "%02d:%02d".format(hour, minute)

private fun formatShortDuration(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0) "${hours}h${mins}m" else "${mins}m"
}
