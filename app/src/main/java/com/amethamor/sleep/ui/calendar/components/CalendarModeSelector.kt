package com.amethamor.sleep.ui.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.calendar.CalendarMode
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun CalendarModeSelector(
    selectedMode: CalendarMode,
    onModeSelected: (CalendarMode) -> Unit
) {
    val colors = SleepTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surface)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CalendarMode.entries.forEach { mode ->
            val selected = mode == selectedMode
            Text(
                text = mode.label,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (selected) colors.selectedBackground else colors.surface)
                    .clickable { onModeSelected(mode) }
                    .padding(vertical = 10.dp),
                color = if (selected) colors.selectedText else colors.textSecondary,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}
