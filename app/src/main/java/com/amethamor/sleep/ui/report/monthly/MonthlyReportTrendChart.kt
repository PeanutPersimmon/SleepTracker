package com.amethamor.sleep.ui.report.monthly

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.statistics.DailyTrendPoint
import com.amethamor.sleep.ui.statistics.StatisticsFormatters
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun MonthlyReportTrendChart(
    bedPoints: List<DailyTrendPoint>,
    wakePoints: List<DailyTrendPoint>,
    modifier: Modifier = Modifier,
    height: Dp = 190.dp
) {
    val colors = SleepTheme.colors
    val textPaint = remember { Paint(Paint.ANTI_ALIAS_FLAG) }
    Canvas(modifier = modifier.fillMaxWidth().height(height)) {
        val points = bedPoints.takeIf { it.isNotEmpty() } ?: wakePoints
        if (points.isEmpty()) return@Canvas

        val left = 24.dp.toPx()
        val right = size.width - 18.dp.toPx()
        val top = 24.dp.toPx()
        val bottom = size.height - 28.dp.toPx()
        val width = right - left
        val chartHeight = bottom - top
        val values = (bedPoints + wakePoints).mapNotNull { it.valueMinutes }
        val rangeStart = (values.minOrNull() ?: 0) - 60
        val rangeEnd = (values.maxOrNull() ?: 1) + 60
        val range = (rangeEnd - rangeStart).coerceAtLeast(1)

        repeat(4) { index ->
            val y = top + chartHeight * index / 3f
            drawLine(colors.divider.copy(alpha = 0.45f), Offset(left, y), Offset(right, y), 1.dp.toPx())
        }

        drawSeries(bedPoints, left, bottom, width, chartHeight, rangeStart, range, colors.chartPrimary, textPaint)
        drawSeries(wakePoints, left, bottom, width, chartHeight, rangeStart, range, colors.awake, textPaint)

        val labels = setOf(1, 10, 20, 30)
        points.forEachIndexed { index, point ->
            if (point.date.dayOfMonth in labels) {
                val x = left + width * index / (points.size - 1).coerceAtLeast(1)
                drawChartText(
                    textPaint,
                    point.date.dayOfMonth.toString(),
                    x,
                    size.height - 7.dp.toPx(),
                    colors.textTertiary,
                    12.sp.toPx()
                )
            }
        }

        drawLegend(textPaint, "入睡", left, 12.dp.toPx(), colors.chartPrimary)
        drawLegend(textPaint, "起床", left + 68.dp.toPx(), 12.dp.toPx(), colors.awake)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSeries(
    points: List<DailyTrendPoint>,
    left: Float,
    bottom: Float,
    width: Float,
    height: Float,
    rangeStart: Int,
    range: Int,
    color: Color,
    textPaint: Paint
) {
    val step = width / (points.size - 1).coerceAtLeast(1)
    var path = Path()
    var hasStart = false
    points.forEachIndexed { index, point ->
        val value = point.valueMinutes
        if (value == null) {
            if (hasStart) drawPath(path, color, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
            path = Path()
            hasStart = false
            return@forEachIndexed
        }
        val x = left + step * index
        val y = bottom - ((value - rangeStart).toFloat() / range) * height
        if (hasStart) path.lineTo(x, y) else path.moveTo(x, y)
        hasStart = true
        drawCircle(color.copy(alpha = 0.90f), 3.dp.toPx(), Offset(x, y))
        if (index == points.indexOfLast { it.valueMinutes != null }) {
            drawChartText(textPaint, StatisticsFormatters.formatTimeFromMinutes(value), x, y - 8.dp.toPx(), color, 10.sp.toPx())
        }
    }
    if (hasStart) drawPath(path, color, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLegend(
    paint: Paint,
    text: String,
    x: Float,
    y: Float,
    color: Color
) {
    drawCircle(color, 4.dp.toPx(), Offset(x, y - 4.dp.toPx()))
    drawChartText(paint, text, x + 10.dp.toPx(), y, color, 11.sp.toPx(), Paint.Align.LEFT)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawChartText(
    paint: Paint,
    text: String,
    x: Float,
    y: Float,
    color: Color,
    size: Float,
    align: Paint.Align = Paint.Align.CENTER
) {
    paint.textSize = size
    paint.color = color.toArgb()
    paint.textAlign = align
    drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)
}
