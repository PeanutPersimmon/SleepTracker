package com.amethamor.sleep.ui.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun HeatMapPlaceholderCard(
    title: String,
    year: Int,
    placeholder: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val colors = SleepTheme.colors
    TrendChartCard(
        title = title,
        metaText = "${year}年",
        averageLabel = null,
        averageValue = null,
        onPreviousClick = onPreviousClick,
        onNextClick = onNextClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(156.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.calendarMissing.copy(alpha = 0.76f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$placeholder\n${year}年",
                color = colors.textTertiary,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
