package com.amethamor.sleep.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ThemeRingPreview(
    backgroundTint: Color,
    accent: Color,
    primary: Color,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = size.toPx()
            val strokeWidth = canvasSize * 0.26f

            drawArc(
                color = backgroundTint,
                startAngle = -90f,
                sweepAngle = 120f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )

            drawArc(
                color = accent,
                startAngle = 30f,
                sweepAngle = 120f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )

            drawArc(
                color = primary,
                startAngle = 150f,
                sweepAngle = 120f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )

            val innerCircleRadius = canvasSize * 0.24f
            drawCircle(
                color = primary,
                radius = innerCircleRadius,
                center = Offset(canvasSize / 2f, canvasSize / 2f)
            )
        }
    }
}
