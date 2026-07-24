package com.amethamor.sleep.ui.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.components.SleepCard
import com.amethamor.sleep.ui.statistics.charts.YearHeatMap
import com.amethamor.sleep.ui.statistics.heatmap.HeatMapColors
import com.amethamor.sleep.ui.statistics.heatmap.HeatMapDateUtils
import com.amethamor.sleep.ui.statistics.heatmap.HeatMapType
import com.amethamor.sleep.ui.statistics.heatmap.HeatMapYearData
import com.amethamor.sleep.ui.theme.SleepTheme
import java.time.LocalDate

@Composable
fun YearHeatMapCard(
    title: String,
    type: HeatMapType,
    data: HeatMapYearData,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val colors = SleepTheme.colors
    SleepCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    color = colors.primaryDark,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    ArrowButton("上一年", true, onPreviousClick)
                    ArrowButton("下一年", false, onNextClick)
                }
            }

            Text(
                text = HeatMapDateUtils.formatYear(data.year),
                color = colors.textSecondary,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp
            )

            val initialWeekIndex = if (data.year == LocalDate.now().year) {
                HeatMapDateUtils.findInitialWeekIndexForMonth(data.year, LocalDate.now().monthValue)
            } else {
                0
            }

            YearHeatMap(
                data = data,
                colorProvider = { day -> HeatMapColors.colorFor(type, day, colors) },
                showValueText = type == HeatMapType.WakeUp,
                initialWeekIndex = initialWeekIndex
            )
        }
    }
}

@Composable
private fun ArrowButton(
    contentDescription: String,
    left: Boolean,
    onClick: () -> Unit
) {
    val colors = SleepTheme.colors
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
    ) {
        Icon(
            imageVector = if (left) {
                Icons.AutoMirrored.Rounded.KeyboardArrowLeft
            } else {
                Icons.AutoMirrored.Rounded.KeyboardArrowRight
            },
            contentDescription = contentDescription,
            tint = colors.primaryDark,
            modifier = Modifier.size(20.dp)
        )
    }
}
