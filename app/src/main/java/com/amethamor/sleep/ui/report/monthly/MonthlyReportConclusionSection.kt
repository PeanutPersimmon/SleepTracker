package com.amethamor.sleep.ui.report.monthly

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.components.SleepCard
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun MonthlyReportConclusionSection(data: MonthlyReportData) {
    SleepCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            MonthlyReportSectionTitle(text = "本月总结与小建议")
            Text(
                text = data.conclusion,
                color = SleepTheme.colors.textPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
            data.suggestions.forEach { suggestion ->
                Text(
                    text = "- $suggestion",
                    color = SleepTheme.colors.textSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
