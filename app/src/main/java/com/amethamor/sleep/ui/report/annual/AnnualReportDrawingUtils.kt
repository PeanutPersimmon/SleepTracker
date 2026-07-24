package com.amethamor.sleep.ui.report.annual

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.max
import kotlin.math.min

object AnnualReportDrawingUtils {
    fun drawRoundCard(canvas: Canvas, rect: RectF, radius: Float, paint: Paint) {
        paint.style = Paint.Style.FILL
        paint.color = AnnualReportColors.Card
        canvas.drawRoundRect(rect, radius, radius, paint)
    }

    fun drawCardShadow(canvas: Canvas, rect: RectF, radius: Float) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AnnualReportColors.CardShadow
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(RectF(rect).apply { offset(0f, 9f) }, radius, radius, paint)
        paint.color = Color.argb(13, 183, 200, 221)
        canvas.drawRoundRect(RectF(rect).apply { offset(0f, 18f) }, radius + 2f, radius + 2f, paint)
    }
    
    fun drawSmallCardShadow(canvas: Canvas, rect: RectF, radius: Float) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(12, 183, 200, 221)
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(RectF(rect).apply { offset(0f, 5f) }, radius, radius, paint)
    }

    fun drawTextLine(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        textSize: Float,
        color: Int,
        isBold: Boolean = false,
        align: Paint.Align = Paint.Align.LEFT
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            this.textSize = textSize
            textAlign = align
            typeface = Typeface.create(Typeface.SANS_SERIF, if (isBold) Typeface.BOLD else Typeface.NORMAL)
        }
        canvas.drawText(text, x, y, paint)
    }

    fun drawMultilineWithin(
        canvas: Canvas,
        lines: List<String>,
        x: Float,
        y: Float,
        maxBottom: Float,
        textSize: Float,
        color: Int,
        lineHeight: Float,
        maxLines: Int
    ) {
        lines.take(maxLines).forEachIndexed { index, line ->
            val lineY = y + index * lineHeight
            if (lineY <= maxBottom) {
                drawTextLine(canvas, line, x, lineY, textSize, color)
            }
        }
    }

    fun drawMultiline(
        canvas: Canvas,
        lines: List<String>,
        x: Float,
        y: Float,
        textSize: Float,
        color: Int,
        lineHeight: Float
    ) {
        drawMultilineWithin(
            canvas = canvas,
            lines = lines,
            x = x,
            y = y,
            maxBottom = Float.MAX_VALUE,
            textSize = textSize,
            color = color,
            lineHeight = lineHeight,
            maxLines = lines.size
        )
    }

    fun drawDonutChart(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        outerRadius: Float,
        strokeWidth: Float,
        slices: List<PieSliceData>,
        colors: List<Int>,
        innerRadiusOverride: Float? = null
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.BUTT
            this.strokeWidth = strokeWidth
        }
        val arcRadius = outerRadius - strokeWidth / 2f
        val rect = RectF(centerX - arcRadius, centerY - arcRadius, centerX + arcRadius, centerY + arcRadius)
        if (slices.isEmpty()) {
            paint.color = AnnualReportColors.Gray
            canvas.drawArc(rect, 0f, 360f, false, paint)
            drawTextLine(canvas, "暂无数据", centerX, centerY + 8f, 21f, AnnualReportColors.TextSecondary, align = Paint.Align.CENTER)
            return
        }
        var start = -90f
        slices.forEachIndexed { index, slice ->
            paint.color = colors[index % colors.size]
            val sweep = if (slices.size == 1) 360f else slice.percent * 360f
            canvas.drawArc(rect, start, sweep, false, paint)
            start += sweep
        }
        // 绘制中心白圆
        val innerRadius = innerRadiusOverride ?: max(outerRadius - strokeWidth, 0f)
        paint.style = Paint.Style.FILL
        paint.color = AnnualReportColors.Card
        canvas.drawCircle(centerX, centerY, innerRadius, paint)
    }

    fun drawLegend(
        canvas: Canvas,
        slices: List<PieSliceData>,
        colors: List<Int>,
        startX: Float,
        startY: Float,
        percentRightX: Float = startX + 188f,
        rowHeight: Float = 34f,
        textSize: Float = 22f,
        labelTransform: (String) -> String = { it }
    ) {
        if (slices.isEmpty()) {
            drawTextLine(canvas, "暂无数据", startX, startY, textSize, AnnualReportColors.TextSecondary)
            return
        }
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
        slices.take(5).forEachIndexed { index, slice ->
            val y = startY + index * rowHeight
            dotPaint.color = colors[index % colors.size]
            canvas.drawCircle(startX, y - 8f, 9f, dotPaint)
            drawTextLine(canvas, labelTransform(slice.label), startX + 24f, y, textSize, AnnualReportColors.TextPrimary)
            drawTextLine(
                canvas = canvas,
                text = AnnualReportTextUtils.formatPercent(slice.percent),
                x = percentRightX,
                y = y,
                textSize = textSize,
                color = AnnualReportColors.PrimaryDark,
                isBold = true,
                align = Paint.Align.RIGHT
            )
        }
    }

    fun drawVectorIcon(
        canvas: Canvas,
        context: Context,
        @DrawableRes resId: Int,
        rect: RectF,
        tint: Int? = null,
        alpha: Int = 255
    ) {
        val drawable = ContextCompat.getDrawable(context, resId)?.mutate() ?: return
        tint?.let(drawable::setTint)
        drawable.alpha = alpha

        // 确保绘制区域为正方形，避免拉伸变形
        val width: Float = rect.width()
        val height: Float = rect.height()
        val size: Float = min(width, height)
        val centerX: Float = rect.centerX()
        val centerY: Float = rect.centerY()
        val halfSize: Float = size / 2f

        drawable.setBounds(
            (centerX - halfSize).toInt(),
            (centerY - halfSize).toInt(),
            (centerX + halfSize).toInt(),
            (centerY + halfSize).toInt()
        )
        drawable.draw(canvas)
    }

    fun drawSmallProgressRing(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        percent: Float,
        color: Int,
        radius: Float = 38f,
        strokeWidth: Float = 14f
    ) {
        val background = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            strokeCap = Paint.Cap.ROUND
            this.color = AnnualReportColors.Gray
        }
        val foreground = Paint(background).apply { this.color = color }
        val rect = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        canvas.drawArc(rect, -90f, 360f, false, background)
        if (percent > 0f) {
            canvas.drawArc(rect, -90f, percent.coerceIn(0f, 1f) * 360f, false, foreground)
        }
    }

    fun drawSimpleMoonIcon(canvas: Canvas, centerX: Float, centerY: Float, radius: Float, alpha: Int = 210) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(alpha, 255, 226, 139)
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, radius, paint)
        paint.color = Color.argb(235, 220, 235, 250)
        canvas.drawCircle(centerX + radius * 0.48f, centerY - radius * 0.16f, radius * 0.92f, paint)
    }

    fun drawSimpleStar(canvas: Canvas, centerX: Float, centerY: Float, radius: Float, color: Int = AnnualReportColors.Yellow, alpha: Int = 190) {
        val path = Path().apply {
            moveTo(centerX, centerY - radius)
            lineTo(centerX + radius * 0.28f, centerY - radius * 0.28f)
            lineTo(centerX + radius, centerY)
            lineTo(centerX + radius * 0.28f, centerY + radius * 0.28f)
            lineTo(centerX, centerY + radius)
            lineTo(centerX - radius * 0.28f, centerY + radius * 0.28f)
            lineTo(centerX - radius, centerY)
            lineTo(centerX - radius * 0.28f, centerY - radius * 0.28f)
            close()
        }
        canvas.drawPath(path, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = withAlpha(color, alpha)
        })
    }

    fun drawSimplePillowIcon(canvas: Canvas, rect: RectF, alpha: Int = 160) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(24, 80, 110, 150)
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(RectF(rect).apply { offset(0f, 6f) }, 24f, 24f, paint)
        paint.color = Color.argb(alpha, 182, 207, 252)
        canvas.drawRoundRect(rect, 24f, 24f, paint)
        paint.color = Color.argb(85, 255, 255, 255)
        canvas.drawOval(RectF(rect.left + rect.width() * 0.12f, rect.top + rect.height() * 0.22f, rect.left + rect.width() * 0.44f, rect.bottom - rect.height() * 0.18f), paint)
    }

    fun drawSimpleCloud(canvas: Canvas, x: Float, y: Float, scale: Float, alpha: Int = 72) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(alpha, 196, 214, 244)
            style = Paint.Style.FILL
        }
        canvas.drawCircle(x, y + 24f * scale, 28f * scale, paint)
        canvas.drawCircle(x + 36f * scale, y + 8f * scale, 38f * scale, paint)
        canvas.drawCircle(x + 82f * scale, y + 26f * scale, 30f * scale, paint)
        canvas.drawRoundRect(RectF(x - 18f * scale, y + 26f * scale, x + 118f * scale, y + 58f * scale), 28f * scale, 28f * scale, paint)
    }

    fun drawBedIcon(canvas: Canvas, rect: RectF, color: Int = AnnualReportColors.Primary) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = 5f
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint)
        canvas.drawLine(rect.left, rect.top + rect.height() * 0.2f, rect.left, rect.bottom, paint)
        canvas.drawLine(rect.right, rect.top + rect.height() * 0.55f, rect.right, rect.bottom, paint)
        paint.style = Paint.Style.FILL
        paint.color = withAlpha(color, 145)
        canvas.drawRoundRect(RectF(rect.left + 8f, rect.centerY(), rect.right - 4f, rect.bottom - 4f), 8f, 8f, paint)
        canvas.drawRoundRect(RectF(rect.left + 10f, rect.top + 7f, rect.left + 34f, rect.centerY() + 2f), 8f, 8f, paint)
    }

    fun drawClockIcon(canvas: Canvas, centerX: Float, centerY: Float, radius: Float, color: Int = AnnualReportColors.Blue) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = 5f
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawCircle(centerX, centerY, radius, paint)
        canvas.drawLine(centerX, centerY, centerX, centerY - radius * 0.52f, paint)
        canvas.drawLine(centerX, centerY, centerX + radius * 0.45f, centerY + radius * 0.25f, paint)
    }

    fun withAlpha(color: Int, alpha: Int): Int {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }
}
