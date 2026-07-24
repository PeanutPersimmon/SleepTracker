package com.amethamor.sleep.ui.report.monthly

import androidx.compose.foundation.layout.width
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun MonthlyReportPieChart(
    title: String,
    slices: List<MonthlyReportPieSlice>,
    kind: MonthlyReportPieKind,
    modifier: Modifier = Modifier
) {
    val colors = SleepTheme.colors
    val palette = remember(kind) {
        slices.map { colorForSlice(kind, it.label) }
    }
    val total = slices.sumOf { it.value }

    Column(
        modifier = modifier.padding(
            start = MonthlyReportDesignTokens.DonutLeftPadding,
            end = MonthlyReportDesignTokens.DonutRightPadding,
            bottom = MonthlyReportDesignTokens.DonutBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(MonthlyReportDesignTokens.CardInnerSpacing)
    ) {
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .height(MonthlyReportDesignTokens.DonutTitleHeight),
            color = colors.textPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontSize = MonthlyReportDesignTokens.CardTitleFontSize,
            lineHeight = MonthlyReportDesignTokens.CardTitleLineHeight,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(MonthlyReportDesignTokens.DonutChartAreaHeight),
            horizontalArrangement = Arrangement.spacedBy(
                MonthlyReportDesignTokens.DonutChartLegendGap,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DonutCanvas(
                slices = slices,
                palette = palette,
                total = total,
                modifier = Modifier.size(MonthlyReportDesignTokens.DonutChartSize)
            )
            Column(
                modifier = Modifier
                    .width(MonthlyReportDesignTokens.DonutLegendWidth),
                verticalArrangement = Arrangement.spacedBy(MonthlyReportDesignTokens.DonutLegendItemGap)
            ) {
                if (slices.isEmpty()) {
                    Text(
                        "暂无数据",
                        color = colors.textSecondary,
                        fontSize = MonthlyReportDesignTokens.DonutLegendFontSize,
                        lineHeight = MonthlyReportDesignTokens.DonutLegendLineHeight
                    )
                } else {
                    slices.forEachIndexed { index, slice ->
                        LegendRow(slice = slice, total = total, color = palette[index % palette.size])
                    }
                }
            }
        }
    }
}

@Composable
private fun DonutCanvas(
    slices: List<MonthlyReportPieSlice>,
    palette: List<Color>,
    total: Int,
    modifier: Modifier = Modifier
) {
    val colors = SleepTheme.colors
    val textPaint = remember { Paint(Paint.ANTI_ALIAS_FLAG) }
    Canvas(modifier = modifier) {
        val strokeWidth = MonthlyReportDesignTokens.DonutStrokeWidth.toPx()
        val centerRadius = size.minDimension / 2f - strokeWidth / 2f
        if (slices.isEmpty() || total <= 0) {
            drawCircle(
                color = colors.calendarMissing.copy(alpha = 0.82f),
                radius = centerRadius,
                style = Stroke(width = strokeWidth)
            )
        } else {
            var start = -90f
            slices.forEachIndexed { index, slice ->
                val sweep = 360f * slice.value / total
                drawArc(
                    color = palette[index % palette.size],
                    startAngle = start,
                    sweepAngle = sweep - 1.2f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth)
                )
                start += sweep
            }
        }
        drawCircle(color = colors.surface, radius = size.minDimension / 2f - strokeWidth)
        drawCenterText(
            paint = textPaint,
            text = if (total > 0) "${total}天" else "--",
            center = Offset(center.x, center.y),
            color = colors.primaryDark,
            size = MonthlyReportDesignTokens.DonutCenterTextFontSize.toPx()
        )
    }
}

@Composable
private fun LegendRow(slice: MonthlyReportPieSlice, total: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MonthlyReportDesignTokens.DonutLegendItemGap)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Text(
            text = slice.label,
            modifier = Modifier.weight(1f),
            color = SleepTheme.colors.textSecondary,
            style = MaterialTheme.typography.bodySmall,
            fontSize = MonthlyReportDesignTokens.DonutLegendFontSize,
            lineHeight = MonthlyReportDesignTokens.DonutLegendLineHeight,
            maxLines = 1,
            softWrap = false
        )
        Text(
            text = "${(slice.value * 100f / total.coerceAtLeast(1)).toInt()}%",
            color = SleepTheme.colors.textPrimary,
            style = MaterialTheme.typography.bodySmall,
            fontSize = MonthlyReportDesignTokens.DonutLegendFontSize,
            lineHeight = MonthlyReportDesignTokens.DonutLegendLineHeight,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.End
        )
    }
}

enum class MonthlyReportPieKind {
    Duration,
    WakeStatus
}

private fun colorForSlice(kind: MonthlyReportPieKind, label: String): Color {
    return when (kind) {
        MonthlyReportPieKind.Duration -> when (label) {
            "<6h" -> Color(0xFFFF8A8A)
            "6-7h" -> Color(0xFF6EA8FE)
            "7-9h" -> Color(0xFF7ED6A5)
            "9-10h" -> Color(0xFFFFC857)
            ">10h" -> Color(0xFFB69CFF)
            else -> Color(0xFFCBD5E1)
        }
        MonthlyReportPieKind.WakeStatus -> when (label) {
            "清醒", "很好" -> Color(0xFF7ED6A5)
            "还行" -> Color(0xFF6EA8FE)
            "一般" -> Color(0xFFFFC857)
            "疲惫", "很累" -> Color(0xFFFF8A8A)
            "困倦" -> Color(0xFFB69CFF)
            else -> Color(0xFFCBD5E1)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCenterText(
    paint: Paint,
    text: String,
    center: Offset,
    color: Color,
    size: Float
) {
    paint.textSize = size
    paint.color = color.toArgb()
    paint.textAlign = Paint.Align.CENTER
    val fontMetrics = paint.fontMetrics
    val baseline = center.y - (fontMetrics.top + fontMetrics.bottom) / 2
    drawContext.canvas.nativeCanvas.drawText(text, center.x, baseline, paint)
}
