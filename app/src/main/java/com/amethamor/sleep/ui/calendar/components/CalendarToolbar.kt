package com.amethamor.sleep.ui.calendar.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.calendar.CalendarMode
import com.amethamor.sleep.ui.theme.SleepTheme
import androidx.compose.foundation.interaction.MutableInteractionSource

@Composable
fun CalendarToolbar(
    monthText: String,
    selectedMode: CalendarMode,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onModeSelected: (CalendarMode) -> Unit
) {
    val colors = SleepTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModeIconButton(
                mode = CalendarMode.BedTime,
                selectedMode = selectedMode,
                icon = Icons.Rounded.Bedtime,
                onClick = { onModeSelected(CalendarMode.BedTime) }
            )
            ModeIconButton(
                mode = CalendarMode.WakeTime,
                selectedMode = selectedMode,
                icon = Icons.Rounded.LightMode,
                onClick = { onModeSelected(CalendarMode.WakeTime) }
            )
            ModeIconButton(
                mode = CalendarMode.Duration,
                selectedMode = selectedMode,
                icon = Icons.Rounded.AccessTime,
                onClick = { onModeSelected(CalendarMode.Duration) }
            )
            ModeIconButton(
                mode = CalendarMode.Nap,
                selectedMode = selectedMode,
                icon = Icons.Rounded.Cloud,
                onClick = { onModeSelected(CalendarMode.Nap) }
            )
        }

        Row(
            modifier = Modifier
                .width(152.dp)
                .height(34.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(colors.selectedBackground.copy(alpha = 0.72f)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MonthArrowButton(
                icon = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                onClick = onPreviousMonth
            )
            Box(
                modifier = Modifier.width(96.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = monthText,
                    color = colors.primaryDark,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip
                )
            }
            MonthArrowButton(
                icon = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                onClick = onNextMonth,
                rotated = true
            )
        }
    }
}

@Composable
private fun ModeIconButton(
    mode: CalendarMode,
    selectedMode: CalendarMode,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val colors = SleepTheme.colors
    val selected = mode == selectedMode
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(30.dp)
            .then(
                if (selected) {
                    Modifier
                        .clip(CircleShape)
                        .background(colors.selectedBackground.copy(alpha = 0.86f))
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = mode.label,
            tint = if (selected) colors.primaryDark else colors.textSecondary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun MonthArrowButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    rotated: Boolean = false
) {
    val colors = SleepTheme.colors
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(28.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.primaryDark,
            modifier = if (rotated) Modifier.size(16.dp).rotate(180f) else Modifier.size(16.dp)
        )
    }
}
