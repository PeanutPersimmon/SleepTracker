package com.amethamor.sleep.ui.statistics.charts

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.statistics.heatmap.HeatMapDay
import com.amethamor.sleep.ui.statistics.heatmap.HeatMapWeekColumn
import com.amethamor.sleep.ui.theme.SleepTheme
import java.time.LocalDate

@Composable
internal fun YearHeatMapCanvas(
    weekColumns: List<HeatMapWeekColumn>,
    dayMap: Map<LocalDate, HeatMapDay>,
    today: LocalDate,
    colorProvider: (HeatMapDay) -> Color,
    showValueText: Boolean,
    valueTextProvider: (HeatMapDay) -> String?
) {
    val colors = SleepTheme.colors
    val monthTextPaint = remember { Paint(Paint.ANTI_ALIAS_FLAG) }
    val valueTextPaint = remember { Paint(Paint.ANTI_ALIAS_FLAG) }
    val todayBorderColor = remember(colors.calendarSelected) { colors.calendarSelected.copy(alpha = 0.45f) }
    val valueTextColor = remember(colors.textSecondary) { colors.textSecondary.copy(alpha = 0.85f) }
    val contentWidth = contentWidthFor(weekColumns.size)
    val contentHeight = YEAR_HEAT_MAP_MONTH_LABEL_HEIGHT +
        YEAR_HEAT_MAP_CELL_SIZE * 7 +
        YEAR_HEAT_MAP_CELL_GAP * 6

    Canvas(
        modifier = Modifier
            .width(contentWidth.dp)
            .height(contentHeight.dp)
    ) {
        val cellSizePx = YEAR_HEAT_MAP_CELL_SIZE.dp.toPx()
        val cellGapPx = YEAR_HEAT_MAP_CELL_GAP.dp.toPx()
        val cellStepPx = cellSizePx + cellGapPx
        val cellCornerPx = YEAR_HEAT_MAP_CELL_CORNER.dp.toPx()
        val monthLabelHeightPx = YEAR_HEAT_MAP_MONTH_LABEL_HEIGHT.dp.toPx()
        val todayStrokeWidthPx = 1.dp.toPx()
        val monthBaseline = monthLabelHeightPx - 4.dp.toPx()
        val nativeCanvas = drawContext.canvas.nativeCanvas

        monthTextPaint.textSize = 12.sp.toPx()
        monthTextPaint.color = colors.textSecondary.toArgb()
        monthTextPaint.textAlign = Paint.Align.LEFT

        valueTextPaint.textSize = 9.sp.toPx()
        valueTextPaint.color = valueTextColor.toArgb()
        valueTextPaint.textAlign = Paint.Align.CENTER

        weekColumns.forEach { weekColumn ->
            val x = weekColumn.weekIndex * cellStepPx
            weekColumn.monthLabel?.let { label ->
                nativeCanvas.drawText(label, x, monthBaseline, monthTextPaint)
            }

            weekColumn.dates.forEachIndexed { dayIndex, date ->
                val day = date?.let { dayMap[it] } ?: return@forEachIndexed
                val y = monthLabelHeightPx + dayIndex * cellStepPx
                drawRoundRect(
                    color = colorProvider(day),
                    topLeft = Offset(x, y),
                    size = Size(cellSizePx, cellSizePx),
                    cornerRadius = CornerRadius(cellCornerPx, cellCornerPx)
                )

                if (day.date == today) {
                    drawRoundRect(
                        color = todayBorderColor,
                        topLeft = Offset(x, y),
                        size = Size(cellSizePx, cellSizePx),
                        cornerRadius = CornerRadius(cellCornerPx, cellCornerPx),
                        style = Stroke(width = todayStrokeWidthPx)
                    )
                }

                if (showValueText) {
                    valueTextProvider(day)?.let { text ->
                        val baseline = y + cellSizePx / 2f -
                            (valueTextPaint.ascent() + valueTextPaint.descent()) / 2f
                        nativeCanvas.drawText(text, x + cellSizePx / 2f, baseline, valueTextPaint)
                    }
                }
            }
        }
    }
}

private fun contentWidthFor(weekCount: Int): Int {
    if (weekCount <= 0) return 0
    return weekCount * YEAR_HEAT_MAP_CELL_SIZE + (weekCount - 1) * YEAR_HEAT_MAP_CELL_GAP
}
