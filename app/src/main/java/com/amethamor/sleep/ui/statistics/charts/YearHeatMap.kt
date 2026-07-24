package com.amethamor.sleep.ui.statistics.charts

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.statistics.heatmap.HeatMapDateUtils
import com.amethamor.sleep.ui.statistics.heatmap.HeatMapDay
import com.amethamor.sleep.ui.statistics.heatmap.HeatMapYearData
import com.amethamor.sleep.ui.theme.SleepTheme
import java.time.LocalDate

internal const val YEAR_HEAT_MAP_CELL_SIZE = 14
internal const val YEAR_HEAT_MAP_CELL_GAP = 3
internal const val YEAR_HEAT_MAP_CELL_CORNER = 4
internal const val YEAR_HEAT_MAP_MONTH_LABEL_HEIGHT = 18
private const val WEEKDAY_LABEL_WIDTH = 14
private const val CURRENT_MONTH_SCROLL_LEAD_WEEKS = 1

@Composable
fun YearHeatMap(
    data: HeatMapYearData,
    colorProvider: (HeatMapDay) -> Color,
    modifier: Modifier = Modifier,
    showValueText: Boolean = false,
    valueTextProvider: (HeatMapDay) -> String? = { it.text },
    initialWeekIndex: Int = 0
) {
    val weekColumns = remember(data.year) {
        HeatMapDateUtils.buildYearWeekColumns(data.year)
    }
    val dayMap = remember(data.days) {
        HeatMapDateUtils.buildDayMap(data.days)
    }
    val today = remember { LocalDate.now() }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    LaunchedEffect(data.year) {
        val targetWeekIndex = if (data.year == today.year) {
            (initialWeekIndex - CURRENT_MONTH_SCROLL_LEAD_WEEKS).coerceAtLeast(0)
        } else {
            0
        }
        val targetPx = with(density) {
            targetWeekIndex * (YEAR_HEAT_MAP_CELL_SIZE.dp + YEAR_HEAT_MAP_CELL_GAP.dp).roundToPx()
        }
        scrollState.scrollTo(targetPx.coerceAtLeast(0))
    }

    Row(modifier = modifier.height(168.dp)) {
        WeekdayLabels()
        Box(
            modifier = Modifier
                .padding(start = 4.dp)
                .horizontalScroll(scrollState)
        ) {
            YearHeatMapCanvas(
                weekColumns = weekColumns,
                dayMap = dayMap,
                today = today,
                colorProvider = colorProvider,
                showValueText = showValueText,
                valueTextProvider = valueTextProvider
            )
        }
    }
}

@Composable
private fun WeekdayLabels() {
    Column(
        modifier = Modifier.padding(top = YEAR_HEAT_MAP_MONTH_LABEL_HEIGHT.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WeekdayLabelRow("\u4e00")
        Gap()
        WeekdayLabelRow("")
        Gap()
        WeekdayLabelRow("\u4e09")
        Gap()
        WeekdayLabelRow("")
        Gap()
        WeekdayLabelRow("\u4e94")
        Gap()
        WeekdayLabelRow("")
        Gap()
        WeekdayLabelRow("\u65e5")
    }
}

@Composable
private fun WeekdayLabelRow(label: String) {
    Box(
        modifier = Modifier
            .width(WEEKDAY_LABEL_WIDTH.dp)
            .height(YEAR_HEAT_MAP_CELL_SIZE.dp),
        contentAlignment = Alignment.Center
    ) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                color = SleepTheme.colors.textSecondary,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun Gap() {
    Spacer(modifier = Modifier.height(YEAR_HEAT_MAP_CELL_GAP.dp))
}
