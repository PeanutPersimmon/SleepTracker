package com.amethamor.sleep.ui.calendar.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.annotation.DrawableRes
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.R
import com.amethamor.sleep.ui.calendar.CalendarDateUtils
import com.amethamor.sleep.ui.calendar.CalendarDayUiModel
import com.amethamor.sleep.ui.calendar.CalendarMode
import com.amethamor.sleep.ui.theme.SleepTheme
import java.time.LocalTime

@Composable
fun CalendarDayCell(
    day: CalendarDayUiModel,
    mode: CalendarMode,
    modifier: Modifier = Modifier
) {
    val colors = SleepTheme.colors
    val shape = RoundedCornerShape(12.dp)
    val borderModifier = if (day.isToday) {
        Modifier.border(1.dp, colors.calendarSelected.copy(alpha = 0.45f), shape)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .height(68.dp)
            .then(borderModifier),
        contentAlignment = Alignment.Center
    ) {
        if (day.isCurrentMonth) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = day.dayNumber?.toString().orEmpty(),
                    color = when {
                        day.isFuture -> colors.calendarFutureText
                        else -> colors.textPrimary
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 11.sp,
                    fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(1.dp))
                if (day.isFuture) {
                    Spacer(modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.size(1.dp))
                } else {
                    when (mode) {
                        CalendarMode.BedTime -> TimeContent(
                            time = day.bedTime,
                            visualProvider = ::bedTimeVisual
                        )
                        CalendarMode.WakeTime -> TimeContent(
                            time = day.wakeTime,
                            visualProvider = ::wakeTimeVisual
                        )
                        CalendarMode.Duration -> DurationContent(
                            durationMinutes = day.durationMinutes
                        )
                        CalendarMode.Nap -> NapContent(
                            totalMinutes = day.napTotalMinutes,
                            count = day.napCount
                        )
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.fillMaxSize())
        }
    }
}

private fun formatDuration(minutes: Int?): String {
    if (minutes == null) return ""
    val hours = minutes / 60
    val mins = minutes % 60
    return when {
        hours > 0 && mins > 0 -> "${hours}h${mins}m"
        hours > 0 -> "${hours}h"
        else -> "${mins}m"
    }
}

@Composable
private fun NapContent(
    totalMinutes: Int?,
    count: Int
) {
    if (totalMinutes == null || totalMinutes <= 0) {
        Spacer(modifier = Modifier.size(26.dp))
        return
    }

    val color = napColor(totalMinutes)
    val iconType = napIconType(totalMinutes)
    CalendarSleepIcon(type = iconType, color = color)
    Spacer(modifier = Modifier.size(1.dp))
    CellValueText(
        text = if (count > 1) "${CalendarDateUtils.formatNapDurationShort(totalMinutes)} / ${count}次" else CalendarDateUtils.formatNapDurationShort(totalMinutes),
        color = color
    )
}

private fun napIconType(totalMinutes: Int): CalendarSleepIconType {
    return when {
        totalMinutes <= 30 -> CalendarSleepIconType.WakeEarly
        totalMinutes <= 60 -> CalendarSleepIconType.WakeNormal
        totalMinutes <= 90 -> CalendarSleepIconType.WakeLate
        else -> CalendarSleepIconType.WakeVeryLate
    }
}

@Composable
private fun TimeContent(
    time: LocalTime?,
    visualProvider: (LocalTime, Color, Color, Color, Color) -> TimeCellVisual
) {
    if (time == null) {
        Spacer(modifier = Modifier.size(26.dp))
        return
    }

    val colors = SleepTheme.colors
    val visual = visualProvider(time, colors.success, colors.sleepNormal, colors.warning, colors.danger)
    MoodBlock(visual = visual)
    Spacer(modifier = Modifier.size(1.dp))
    CellValueText(
        text = formatTime(time),
        color = visual.textColor.copy(alpha = 0.85f)
    )
}

@Composable
private fun DurationContent(durationMinutes: Int?) {
    if (durationMinutes == null) {
        Spacer(modifier = Modifier.size(26.dp))
        return
    }

    val durationText = formatDuration(durationMinutes)
    DurationProgressRing(durationMinutes = durationMinutes)
    Spacer(modifier = Modifier.size(1.dp))
    DurationValueText(
        text = durationText,
        color = durationTextColor(durationMinutes)
    )
}

@Composable
private fun MoodBlock(visual: TimeCellVisual) {
    CalendarSleepIcon(
        type = visual.iconType,
        color = visual.textColor
    )
}

@Composable
private fun DurationProgressRing(durationMinutes: Int) {
    val colors = SleepTheme.colors
    val maxMinutes = 10 * 60
    val progress = (durationMinutes.toFloat() / maxMinutes).coerceIn(0f, 1f)
    val ringColor = durationTextColor(durationMinutes)
    val backgroundColor = colors.calendarMissing.copy(alpha = 0.72f)

    Canvas(modifier = Modifier.size(26.dp)) {
        val strokeWidth = 4.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)

        drawCircle(
            color = backgroundColor,
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        if (progress > 0) {
            val sweepAngle = 360 * progress
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
private fun CellValueText(
    text: String,
    color: Color
) {
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelSmall,
        fontSize = 9.sp,
        textAlign = TextAlign.Center,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Clip
    )
}

@Composable
private fun DurationValueText(
    text: String,
    color: Color
) {
    val isLongDuration = text.length >= 6
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        color = color,
        style = MaterialTheme.typography.labelSmall,
        fontSize = if (isLongDuration) 7.5.sp else 8.sp,
        lineHeight = 9.sp,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Clip
    )
}

private data class TimeCellVisual(
    val iconType: CalendarSleepIconType,
    val textColor: Color
)

private fun bedTimeVisual(
    time: LocalTime,
    successColor: Color,
    normalColor: Color,
    warningColor: Color,
    dangerColor: Color
): TimeCellVisual {
    val sleepHour = if (time.hour < 12) time.hour + 24 else time.hour
    return when {
        sleepHour < 23 -> TimeCellVisual(
            iconType = CalendarSleepIconType.BedEarly,
            textColor = successColor
        )
        sleepHour < 25 -> TimeCellVisual(
            iconType = CalendarSleepIconType.BedNormal,
            textColor = normalColor
        )
        sleepHour < 27 -> TimeCellVisual(
            iconType = CalendarSleepIconType.BedLate,
            textColor = warningColor
        )
        else -> TimeCellVisual(
            iconType = CalendarSleepIconType.BedVeryLate,
            textColor = dangerColor
        )
    }
}

private fun wakeTimeVisual(
    time: LocalTime,
    successColor: Color,
    normalColor: Color,
    warningColor: Color,
    dangerColor: Color
): TimeCellVisual {
    return when {
        time.hour < 8 -> TimeCellVisual(
            iconType = CalendarSleepIconType.WakeEarly,
            textColor = successColor
        )
        time.hour < 10 -> TimeCellVisual(
            iconType = CalendarSleepIconType.WakeNormal,
            textColor = normalColor
        )
        time.hour < 12 -> TimeCellVisual(
            iconType = CalendarSleepIconType.WakeLate,
            textColor = warningColor
        )
        else -> TimeCellVisual(
            iconType = CalendarSleepIconType.WakeVeryLate,
            textColor = dangerColor
        )
    }
}

enum class CalendarSleepIconType {
    BedEarly,
    BedNormal,
    BedLate,
    BedVeryLate,
    WakeEarly,
    WakeNormal,
    WakeLate,
    WakeVeryLate
}

@Composable
fun CalendarSleepIcon(
    type: CalendarSleepIconType,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 26.dp
) {
    Icon(
        painter = painterResource(id = type.iconRes()),
        contentDescription = null,
        modifier = modifier.size(size),
        tint = color
    )
}

@DrawableRes
private fun CalendarSleepIconType.iconRes(): Int {
    return when (this) {
        CalendarSleepIconType.BedEarly -> R.drawable.ic_sleep_bed_early
        CalendarSleepIconType.BedNormal -> R.drawable.ic_sleep_bed_normal
        CalendarSleepIconType.BedLate -> R.drawable.ic_sleep_bed_late
        CalendarSleepIconType.BedVeryLate -> R.drawable.ic_sleep_bed_very_late
        CalendarSleepIconType.WakeEarly -> R.drawable.ic_sleep_wake_early
        CalendarSleepIconType.WakeNormal -> R.drawable.ic_sleep_wake_normal
        CalendarSleepIconType.WakeLate -> R.drawable.ic_sleep_wake_late
        CalendarSleepIconType.WakeVeryLate -> R.drawable.ic_sleep_wake_very_late
    }
}

@Composable
private fun durationTextColor(durationMinutes: Int): Color {
    val colors = SleepTheme.colors
    return when {
        durationMinutes in (7 * 60)..(9 * 60) -> colors.success
        durationMinutes in (6 * 60) until (7 * 60) -> colors.sleepNormal
        durationMinutes in ((9 * 60) + 1)..(10 * 60) -> colors.sleepNormal
        else -> colors.danger
    }
}

private fun formatTime(time: LocalTime): String {
    return String.format("%02d:%02d", time.hour, time.minute)
}

@Composable
private fun napColor(totalMinutes: Int): Color {
    val colors = SleepTheme.colors
    return when {
        totalMinutes <= 30 -> colors.success
        totalMinutes <= 60 -> colors.sleepNormal
        totalMinutes <= 90 -> colors.warning
        else -> colors.danger
    }
}
