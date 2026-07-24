package com.amethamor.sleep.ui.statistics.charts

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.statistics.DailyDurationPoint
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun MiniBarChart(
    points: List<DailyDurationPoint>,
    modifier: Modifier = Modifier,
    height: Dp = 176.dp,
    barColors: List<Color>? = null,
    maxDurationMinutes: Int? = null
) {
    val colors = SleepTheme.colors
    val textPaint = remember { Paint(Paint.ANTI_ALIAS_FLAG) }
    val barGradientColors = remember(colors.chartPrimary, colors.primary, barColors) {
        barColors ?: listOf(
            colors.chartPrimary.copy(alpha = 0.92f),
            colors.primary.copy(alpha = 0.08f)
        )
    }
    val markerDayLabels = remember { setOf("1", "10", "20", "30") }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        if (points.isEmpty()) return@Canvas

        val horizontalPadding = 16.dp.toPx()
        val leftPadding = horizontalPadding
        val rightPadding = horizontalPadding
        val topPadding = 16.dp.toPx()
        val bottomPadding = 24.dp.toPx()
        val chartLeft = leftPadding
        val chartRight = size.width - rightPadding
        val chartTop = topPadding
        val chartBottom = size.height - bottomPadding
        val chartWidth = chartRight - chartLeft
        val chartHeight = chartBottom - chartTop
        var maxValue = maxDurationMinutes ?: 60
        points.forEach { point ->
            val duration = point.durationMinutes ?: return@forEach
            maxValue = maxOf(maxValue, duration)
        }
        val slotWidth = chartWidth / points.size
        val barWidth = (slotWidth * 0.18f).coerceAtMost(5.dp.toPx()).coerceAtLeast(3.dp.toPx())

        drawLine(
            color = colors.textTertiary.copy(alpha = 0.34f),
            start = Offset(chartLeft, chartBottom),
            end = Offset(chartRight, chartBottom),
            strokeWidth = 1.dp.toPx()
        )

        points.forEachIndexed { index, point ->
            val duration = point.durationMinutes
            val centerX = chartLeft + slotWidth * index + slotWidth / 2f
            if (duration != null) {
                val barHeight = (duration.toFloat() / maxValue) * chartHeight
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = barGradientColors,
                        startY = chartBottom - barHeight,
                        endY = chartBottom
                    ),
                    topLeft = Offset(centerX - barWidth / 2f, chartBottom - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                )
            }

            if (point.dayLabel in markerDayLabels) {
                drawChartText(
                    paint = textPaint,
                    text = point.dayLabel,
                    x = centerX,
                    y = size.height - 6.dp.toPx(),
                    color = colors.textTertiary.copy(alpha = 0.70f),
                    size = 12.sp.toPx(),
                    align = Paint.Align.CENTER
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawChartText(
    paint: Paint,
    text: String,
    x: Float,
    y: Float,
    color: Color,
    size: Float,
    align: Paint.Align
) {
    paint.textSize = size
    paint.color = color.toArgb()
    paint.textAlign = align
    drawContext.canvas.nativeCanvas.drawText(
        text,
        x,
        y,
        paint
    )
}
