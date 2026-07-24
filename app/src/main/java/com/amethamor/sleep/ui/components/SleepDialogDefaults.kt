package com.amethamor.sleep.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import com.amethamor.sleep.ui.theme.SleepTheme

object SleepDialogDefaults {
    val Shape = RoundedCornerShape(20.dp)
    val CompactShape = RoundedCornerShape(16.dp)
    val ShadowElevation = 8.dp
    val TonalElevation = 0.dp
    val ContentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)

    val containerColor: Color
        @Composable get() = SleepTheme.colors.surface

    val titleColor: Color
        @Composable get() = SleepTheme.colors.textPrimary

    val bodyColor: Color
        @Composable get() = SleepTheme.colors.textSecondary

    val dismissActionColor: Color
        @Composable get() = SleepTheme.colors.textSecondary

    val confirmActionColor: Color
        @Composable get() = SleepTheme.colors.primary

    val confirmActionDarkColor: Color
        @Composable get() = SleepTheme.colors.primaryDark

    val destructiveActionColor: Color
        @Composable get() = SleepTheme.colors.danger
}
