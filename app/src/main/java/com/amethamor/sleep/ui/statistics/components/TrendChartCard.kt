package com.amethamor.sleep.ui.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun TrendChartCard(
    title: String,
    metaText: String,
    averageLabel: String?,
    averageValue: String?,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    chart: @Composable ColumnScope.() -> Unit
) {
    val colors = SleepTheme.colors
    SleepCard {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ChartHeader(
                title = title,
                onPreviousClick = onPreviousClick,
                onNextClick = onNextClick
            )
            Text(
                text = metaText,
                color = colors.textSecondary,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                maxLines = 1
            )
            chart()
            if (averageLabel != null && averageValue != null) {
                AverageText(label = averageLabel, value = averageValue)
            }
        }
    }
}

@Composable
private fun ChartHeader(
    title: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val colors = SleepTheme.colors
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
            ArrowButton(
                contentDescription = "上一个时间范围",
                left = true,
                onClick = onPreviousClick
            )
            ArrowButton(
                contentDescription = "下一个时间范围",
                left = false,
                onClick = onNextClick
            )
        }
    }
}

@Composable
private fun AverageText(
    label: String,
    value: String
) {
    val colors = SleepTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label  ",
            color = colors.textSecondary,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 11.sp
        )
        Text(
            text = value,
            color = colors.textPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
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
