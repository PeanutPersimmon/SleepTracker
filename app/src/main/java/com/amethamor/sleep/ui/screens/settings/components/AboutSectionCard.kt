package com.amethamor.sleep.ui.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.components.SleepCard
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun AboutIntroCard() {
    val colors = SleepTheme.colors
    SleepCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "睡眠打卡",
                color = colors.textPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "一个用于记录入睡、起床、睡眠时长与睡眠状态的极简记录 App。",
                color = colors.textSecondary,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
            Text(
                text = "只记录，不施压。",
                color = colors.primaryDark,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AboutSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = SleepTheme.colors
    SleepCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = title,
                color = colors.textPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
        }
    }
}

@Composable
fun InfoLine(label: String, value: String) {
    val colors = SleepTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = colors.textSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            color = colors.textPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun BulletLine(text: String) {
    Text(
        text = "• $text",
        color = SleepTheme.colors.textSecondary,
        style = MaterialTheme.typography.bodyMedium,
        lineHeight = 22.sp
    )
}
