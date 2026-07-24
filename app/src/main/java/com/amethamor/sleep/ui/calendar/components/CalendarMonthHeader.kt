package com.amethamor.sleep.ui.calendar.components

import androidx.compose.foundation.layout.Arrangement
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
fun CalendarMonthHeader(
    monthText: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val colors = SleepTheme.colors
    SleepCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MonthArrowButton(
                contentDescription = "\u4e0a\u4e00\u4e2a\u6708",
                isPrevious = true,
                onClick = onPreviousClick
            )
            Text(
                text = monthText,
                color = colors.primaryDark,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            MonthArrowButton(
                contentDescription = "\u4e0b\u4e00\u4e2a\u6708",
                isPrevious = false,
                onClick = onNextClick
            )
        }
    }
}

@Composable
private fun MonthArrowButton(
    contentDescription: String,
    isPrevious: Boolean,
    onClick: () -> Unit
) {
    val colors = SleepTheme.colors
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
    ) {
        Icon(
            imageVector = if (isPrevious) {
                Icons.AutoMirrored.Rounded.KeyboardArrowLeft
            } else {
                Icons.AutoMirrored.Rounded.KeyboardArrowRight
            },
            contentDescription = contentDescription,
            tint = colors.primaryDark,
            modifier = Modifier.size(26.dp)
        )
    }
}
