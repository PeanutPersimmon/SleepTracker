package com.amethamor.sleep.ui.statistics.charts

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.statistics.DailyTrendPoint
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun MiniLineChart(
    points: List<DailyTrendPoint>,
    modifier: Modifier = Modifier,
    height: Dp = 160.dp
) {
    val colors = SleepTheme.colors
    val textPaint = remember { Paint(Paint.ANTI_ALIAS_FLAG) }
    val areaGradientColors = remember(colors.chartPrimary) {
        listOf(
            colors.chartPrimary.copy(alpha = 0.12f),
            colors.chartPrimary.copy(alpha = 0.02f)
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        if (points.isEmpty()) return@Canvas

        val horizontalPadding = 20.dp.toPx()
        val leftPadding = horizontalPadding
        val rightPadding = horizontalPadding
        val topPadding = 28.dp.toPx()
        val bottomPadding = 28.dp.toPx()
        val chartLeft = leftPadding
        val chartRight = size.width - rightPadding
        val chartTop = topPadding
        val chartBottom = size.height - bottomPadding
        val chartWidth = chartRight - chartLeft
        val chartHeight = chartBottom - chartTop

        var minValue: Int? = null
        var maxValue: Int? = null
        points.forEach { point ->
            val value = point.valueMinutes ?: return@forEach
            minValue = minValue?.let { minOf(it, value) } ?: value
            maxValue = maxValue?.let { maxOf(it, value) } ?: value
        }
        val resolvedMinValue = minValue
        val resolvedMaxValue = maxValue
        val rangeStart = if (resolvedMinValue == null || resolvedMaxValue == null) {
            0
        } else {
            resolvedMinValue - 60
        }
        val rangeEnd = if (resolvedMinValue == null || resolvedMaxValue == null) {
            1
        } else {
            resolvedMaxValue + 60
        }
        val valueRange = (rangeEnd - rangeStart).coerceAtLeast(1)

        repeat(4) { index ->
            val y = chartTop + chartHeight * index / 3f
            drawLine(
                color = colors.divider.copy(alpha = 0.45f),
                start = Offset(chartLeft, y),
                end = Offset(chartRight, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        val xStep = if (points.size == 1) 0f else chartWidth / (points.size - 1)

        var segment = Path()
        var area = Path()
        var hasSegmentStart = false
        var segmentStartX = 0f
        var lastX = 0f
        points.forEachIndexed { index, point ->
            val value = point.valueMinutes
            if (value == null) {
                if (hasSegmentStart) {
                    area.lineTo(lastX, chartBottom)
                    area.lineTo(segmentStartX, chartBottom)
                    area.close()
                    drawPath(
                        path = area,
                        brush = Brush.verticalGradient(
                            colors = areaGradientColors,
                            startY = chartTop,
                            endY = chartBottom
                        )
                    )
                    drawPath(
                        path = segment,
                        color = colors.chartPrimary.copy(alpha = 0.82f),
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                segment = Path()
                area = Path()
                hasSegmentStart = false
            } else {
                val x = chartLeft + xStep * index
                val y = chartBottom - ((value - rangeStart).toFloat() / valueRange) * chartHeight
                if (!hasSegmentStart) {
                    segment.moveTo(x, y)
                    area.moveTo(x, chartBottom)
                    area.lineTo(x, y)
                    segmentStartX = x
                    hasSegmentStart = true
                } else {
                    segment.lineTo(x, y)
                    area.lineTo(x, y)
                }
                lastX = x
            }
        }
        if (hasSegmentStart) {
            area.lineTo(lastX, chartBottom)
            area.lineTo(segmentStartX, chartBottom)
            area.close()
            drawPath(
                path = area,
                brush = Brush.verticalGradient(
                    colors = areaGradientColors,
                    startY = chartTop,
                    endY = chartBottom
                )
            )
            drawPath(
                path = segment,
                color = colors.chartPrimary.copy(alpha = 0.82f),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        points.forEachIndexed { index, point ->
            val value = point.valueMinutes ?: return@forEachIndexed
            val x = chartLeft + xStep * index
            val y = chartBottom - ((value - rangeStart).toFloat() / valueRange) * chartHeight
            val offset = Offset(x, y)
            drawCircle(color = colors.chartPrimary.copy(alpha = 0.86f), radius = 3.dp.toPx(), center = offset)
            point.valueLabel?.let { label ->
                drawChartText(
                    paint = textPaint,
                    text = label,
                    x = offset.x,
                    y = offset.y - 10.dp.toPx(),
                    color = colors.chartPrimary,
                    size = 11.sp.toPx(),
                    align = Paint.Align.CENTER
                )
            }
        }

        points.forEachIndexed { index, point ->
            val x = chartLeft + xStep * index
            drawChartText(
                paint = textPaint,
                text = point.dateLabel,
                x = x,
                y = size.height - 6.dp.toPx(),
                color = colors.textTertiary.copy(alpha = 0.82f),
                size = 12.sp.toPx(),
                align = Paint.Align.CENTER
            )
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
