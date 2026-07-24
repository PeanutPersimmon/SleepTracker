package com.amethamor.sleep.ui.statistics.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun StatisticSectionTitle(text: String) {
    Text(
        text = text,
        color = SleepTheme.colors.textPrimary,
        style = MaterialTheme.typography.titleMedium,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
    )
}
