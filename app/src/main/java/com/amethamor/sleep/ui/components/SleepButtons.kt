package com.amethamor.sleep.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun SleepPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SleepTheme.colors
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .noRippleClickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = colors.primary,
        shadowElevation = 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.onPrimary
            )
        }
    }
}

@Composable
fun SleepSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SleepTheme.colors
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .noRippleClickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = colors.selectedBackground,
        shadowElevation = 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = colors.selectedText
            )
        }
    }
}

@Composable
fun SleepActionButton(
    text: String,
    isSleep: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SleepTheme.colors
    val backgroundColor = if (isSleep) colors.sleepButton else colors.wakeButton
    val contentColor = if (isSleep) colors.onSleepButton else colors.onWakeButton
    val icon = if (isSleep) Icons.Default.DarkMode else Icons.Default.LightMode
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(116.dp)
            .noRippleClickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        color = backgroundColor,
        shadowElevation = 0.dp
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = contentColor
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = contentColor
            )
        }
    }
}
