package com.amethamor.sleep.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun SleepCard(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: Dp = 0.dp,
    shadowElevation: Dp = 1.dp,
    content: @Composable () -> Unit
) {
    val colors = SleepTheme.colors
    val cardShape = shape
    Card(
        modifier = modifier.shadow(
            elevation = shadowElevation,
            shape = cardShape,
            clip = false
        ),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        border = BorderStroke(
            width = 0.5.dp,
            color = colors.outline.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation,
            pressedElevation = elevation
        ),
        shape = cardShape
    ) {
        Box(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}
