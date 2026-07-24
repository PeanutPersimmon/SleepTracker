package com.amethamor.sleep.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.SleepScreen
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun SleepBottomBar(
    currentScreen: SleepScreen,
    onScreenChange: (SleepScreen) -> Unit
) {
    val colors = SleepTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomScreens.forEach { screen ->
                BottomBarItem(
                    screen = screen,
                    selected = currentScreen == screen,
                    onClick = { onScreenChange(screen) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    screen: SleepScreen,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SleepTheme.colors
    val iconColor = if (selected) colors.onPrimary else colors.textSecondary
    val textColor = if (selected) colors.onPrimary else colors.textSecondary

    Box(
        modifier = modifier
            .height(52.dp)
            .noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(if (selected) colors.primary else colors.surface)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = screen.icon(),
                contentDescription = screen.title,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = screen.title,
                color = textColor,
                fontSize = 12.sp,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

private val bottomScreens = listOf(
    SleepScreen.Home,
    SleepScreen.Statistics,
    SleepScreen.Calendar,
    SleepScreen.Settings
)

private fun SleepScreen.icon(): ImageVector {
    return when (this) {
        SleepScreen.Home -> Icons.Outlined.Home
        SleepScreen.Statistics -> Icons.AutoMirrored.Outlined.ShowChart
        SleepScreen.Calendar -> Icons.Outlined.DateRange
        SleepScreen.Settings -> Icons.Outlined.Settings
    }
}
