package com.amethamor.sleep.ui.report.annual

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import com.amethamor.sleep.R

object AnnualReportImageRenderer {
    private object Poster {
        const val Width = 1080
        const val Height = 2400
        const val Padding = 48f
        const val CardPadding = 30f
        const val Radius = 36f
        const val Gap = 30f
        val PieCardWidth = (Width - Padding * 2f - Gap) / 2f

        val Header = RectF(Padding, 40f, Width - Padding, 210f)
        val Overview = RectF(Padding, 200f, Width - Padding, 540f)
        val DurationPie = RectF(Padding, 570f, Padding + PieCardWidth, 890f)
        val WakeStatePie = RectF(Width - Padding - PieCardWidth, 570f, Width - Padding, 890f)
        val Status = RectF(Padding, 940f, Width - Padding, 1270f)
        val Nap = RectF(Padding, 1300f, Width - Padding, 1590f)
        val SleepWakeSummary = RectF(Padding, 1620f, Width - Padding, 1845f)
        val AnnualSummary = RectF(Padding, 1875f, Width - Padding, 2215f)
        val Footer = RectF(Padding, 2215f, Width - Padding, Height - 40f)
    }

    private object TypeScale {
        const val Title = 56f
        const val Subtitle = 28f
        const val SectionTitle = 32f
        const val SectionHint = 22f
        const val HeroNumber = 58f
        const val MetricNumber = 42f
        const val NormalNumber = 34f
        const val SmallNumber = 31f
        const val SmallTitle = 25f
        const val Body = 25f
        const val BodySmall = 23f
        const val Legend = 23f
        const val Caption = 20f
    }

    private object LayoutRules {
        // 分布甜甜圈（睡眠时长分布、醒来状态分布）
        const val DistributionDonutOuterRadius = 78f
        const val DistributionDonutStrokeWidth = 34f
        const val DistributionDonutInnerRadius = 44f
        const val DistributionDonutIconSize = 26f
        const val DistributionDonutLeftInset = 4f
        const val DistributionDonutToLegendGap = 48f
        const val DistributionLegendPercentRightInset = 34f
        const val DistributionLegendTextSize = 20f
        const val DistributionLegendRowHeight = 34f

        // 夜醒/做梦/噩梦小卡片
        const val RingMetricOuterRadius = 40f
        const val RingMetricStrokeWidth = 12f
        const val RingMetricTitleBaselineTopOffset = 40f
        const val RingMetricRingCenterTitleBaselineGap = 64f
        const val RingMetricRingToValueGap = 50f

        // 午睡统计甜甜圈
        const val NapDonutOuterRadius = 76f
        const val NapDonutStrokeWidth = 32f
        const val NapDonutInnerRadius = 44f
        const val NapDonutIconSize = 24f
        const val NapStatsToDonutGap = 48f
        const val NapDonutToLegendGap = 56f

        // 午睡图例
        const val NapLegendLabelToPercentGap = 178f
        const val NapLegendTextSize = 20f
        const val NapLegendRowHeight = 34f

        // 午睡小统计卡片
        const val NapSmallStatTitleBaselineTopOffset = 36f
        const val NapSmallStatTitleToValueBaselineGap = 54f
        const val NapSmallStatValueTextSize = 33f
        const val NapSmallStatTitleTextSize = 25f

        // 装饰图标
        const val DecorationIconSize = 108f
    }

    private data class ChartLegendLayout(
        val chartCenterX: Float,
        val chartCenterY: Float,
        val chartRadius: Float,
        val chartStroke: Float,
        val legendX: Float,
        val legendY: Float,
        val percentRightX: Float,
        val rowHeight: Float
    )

    private val chartColors = listOf(
        AnnualReportColors.Blue,
        AnnualReportColors.Teal,
        AnnualReportColors.Green,
        AnnualReportColors.Yellow,
        AnnualReportColors.Purple
    )

    fun renderAnnualReportBitmap(context: Context, report: AnnualSleepReport): Bitmap {
        val bitmap = Bitmap.createBitmap(Poster.Width, Poster.Height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawBackground(canvas, context)
        drawHeader(canvas, context, report)
        drawOverviewCard(canvas, context, report)
        drawPieCard(canvas, context, Poster.DurationPie, "睡眠时长分布", report.nightDurationDistribution)
        drawPieCard(canvas, context, Poster.WakeStatePie, "醒来状态分布", report.wakeStateDistribution)
        drawDreamWakeNightmareSection(canvas, context, report)
        drawNapSection(canvas, context, report)
        drawSummaryCard(
            canvas = canvas,
            context = context,
            rect = Poster.SleepWakeSummary,
            title = "入睡 / 起床总结",
            iconResId = R.drawable.ic_report_clock,
            decorationResId = R.drawable.ic_report_clock,
            lines = report.sleepWakeSummary,
            maxLines = 3
        )
        drawSummaryCard(
            canvas = canvas,
            context = context,
            rect = Poster.AnnualSummary,
            title = "年度小结",
            iconResId = R.drawable.ic_report_star,
            decorationResId = R.drawable.ic_report_summary,
            lines = report.annualSummary,
            maxLines = 4
        )
        drawFooter(canvas)
        return bitmap
    }

    private fun drawBackground(canvas: Canvas, context: Context) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f,
                0f,
                0f,
                Poster.Height.toFloat(),
                intArrayOf(
                    AnnualReportColors.BackgroundTop,
                    AnnualReportColors.BackgroundMiddle,
                    AnnualReportColors.BackgroundBottom
                ),
                floatArrayOf(0f, 0.54f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, Poster.Width.toFloat(), Poster.Height.toFloat(), paint)
        AnnualReportDrawingUtils.drawVectorIcon(canvas, context, R.drawable.ic_widget_sleep_moon, RectF(914f, 70f, 1010f, 166f), AnnualReportColors.Yellow, 90)
        AnnualReportDrawingUtils.drawVectorIcon(canvas, context, R.drawable.ic_report_cloud, RectF(798f, 118f, 928f, 224f), AnnualReportColors.Primary, 46)
        AnnualReportDrawingUtils.drawVectorIcon(canvas, context, R.drawable.ic_report_star, RectF(172f, 58f, 208f, 94f), AnnualReportColors.Purple, 90)
        AnnualReportDrawingUtils.drawVectorIcon(canvas, context, R.drawable.ic_report_star, RectF(242f, 112f, 272f, 142f), AnnualReportColors.Blue, 72)
        AnnualReportDrawingUtils.drawVectorIcon(canvas, context, R.drawable.ic_report_cloud, RectF(760f, 2248f, 930f, 2358f), AnnualReportColors.Primary, 34)
    }

    private fun drawHeader(canvas: Canvas, context: Context, report: AnnualSleepReport) {
        val rect = Poster.Header
        AnnualReportDrawingUtils.drawTextLine(canvas, "${report.year} 年度睡眠汇报", Poster.Width / 2f, rect.top + 66f, TypeScale.Title, AnnualReportColors.PrimaryDark, true, Paint.Align.CENTER)
        val subtitleY = rect.top + 118f
        drawLine(canvas, 302f, subtitleY - 9f, 392f, subtitleY - 9f, AnnualReportColors.TextTertiary, 2f)
        drawLine(canvas, 688f, subtitleY - 9f, 778f, subtitleY - 9f, AnnualReportColors.TextTertiary, 2f)
        AnnualReportDrawingUtils.drawTextLine(canvas, "你的全年睡眠回顾", Poster.Width / 2f, subtitleY, TypeScale.Subtitle, AnnualReportColors.PrimaryDark, false, Paint.Align.CENTER)
        AnnualReportDrawingUtils.drawVectorIcon(canvas, context, R.drawable.ic_report_star, RectF(rect.left + 36f, rect.top + 90f, rect.left + 66f, rect.top + 120f), AnnualReportColors.Purple, 70)
    }

    private fun drawOverviewCard(canvas: Canvas, context: Context, report: AnnualSleepReport) {
        val rect = Poster.Overview
        card(canvas, rect)
        AnnualReportDrawingUtils.drawVectorIcon(canvas, context, R.drawable.ic_widget_sleep_moon, RectF(rect.left + 40f, rect.top + 40f, rect.left + 92f, rect.top + 92f), AnnualReportColors.Yellow, 115)
        AnnualReportDrawingUtils.drawVectorIcon(canvas, context, R.drawable.ic_report_cloud, RectF(rect.left + 66f, rect.top + 64f, rect.left + 134f, rect.top + 118f), AnnualReportColors.Primary, 45)

        drawTopMetric(canvas, "全年记录", report.totalRecordDays.toString(), "天", rect.left + 388f, rect.top + 88f)
        drawTopMetric(canvas, "完整记录", report.completeNightDays.toString(), "天 · ${AnnualReportTextUtils.formatPercent(report.completeRate)}", rect.left + 728f, rect.top + 88f)
        drawDivider(canvas, rect.left + 558f, rect.top + 42f, rect.top + 140f)
        drawHorizontalDivider(canvas, rect.left + Poster.CardPadding, rect.top + 168f, rect.right - Poster.CardPadding)

        val contentLeft = rect.left + Poster.CardPadding
        val metricTop = rect.top + 256f
        val colWidth = (rect.width() - Poster.CardPadding * 2f) / 3f
        drawBottomMetric(canvas, context, "平均入睡", report.averageBedTimeText, R.drawable.ic_report_bed, contentLeft + colWidth * 0.5f, metricTop)
        drawBottomMetric(canvas, context, "平均起床", report.averageWakeTimeText, R.drawable.ic_report_clock, contentLeft + colWidth * 1.5f, metricTop)
        drawBottomMetric(canvas, context, "平均时长", report.averageNightDurationText, R.drawable.ic_report_bed, contentLeft + colWidth * 2.5f, metricTop, TypeScale.NormalNumber)
        drawDivider(canvas, contentLeft + colWidth, rect.top + 196f, rect.bottom - 34f, AnnualReportDrawingUtils.withAlpha(AnnualReportColors.Divider, 150), 1.5f)
        drawDivider(canvas, contentLeft + colWidth * 2f, rect.top + 196f, rect.bottom - 34f, AnnualReportDrawingUtils.withAlpha(AnnualReportColors.Divider, 150), 1.5f)
    }

    private fun drawPieCard(canvas: Canvas, context: Context, rect: RectF, title: String, slices: List<PieSliceData>) {
        card(canvas, rect)
        AnnualReportDrawingUtils.drawVectorIcon(canvas, context, R.drawable.ic_report_pie, RectF(rect.left + Poster.CardPadding, rect.top + 28f, rect.left + Poster.CardPadding + 30f, rect.top + 58f), AnnualReportColors.PrimaryDark, 180)
        AnnualReportDrawingUtils.drawTextLine(canvas, title, rect.left + Poster.CardPadding + 42f, rect.top + 54f, TypeScale.SectionTitle, AnnualReportColors.PrimaryDark, true)
        val chart = pieLayout(rect, slices.size)
        AnnualReportDrawingUtils.drawDonutChart(canvas, chart.chartCenterX, chart.chartCenterY, chart.chartRadius, chart.chartStroke, slices, chartColors, innerRadiusOverride = LayoutRules.DistributionDonutInnerRadius)
        if (slices.isNotEmpty()) {
            val iconHalfSize = LayoutRules.DistributionDonutIconSize / 2f
            AnnualReportDrawingUtils.drawVectorIcon(
                canvas, context, R.drawable.ic_widget_sleep_moon,
                RectF(
                    chart.chartCenterX - iconHalfSize,
                    chart.chartCenterY - iconHalfSize,
                    chart.chartCenterX + iconHalfSize,
                    chart.chartCenterY + iconHalfSize
                ),
                AnnualReportColors.Primary, 66
            )
        }
        AnnualReportDrawingUtils.drawLegend(
            canvas = canvas,
            slices = slices,
            colors = chartColors,
            startX = chart.legendX,
            startY = chart.legendY,
            percentRightX = chart.percentRightX,
            rowHeight = chart.rowHeight,
            textSize = LayoutRules.DistributionLegendTextSize
        )
    }

    private fun pieLayout(rect: RectF, sliceCount: Int): ChartLegendLayout {
        val contentTop = rect.top + 92f
        val contentHeight = 190f
        val contentCenterY = contentTop + contentHeight / 2f
        val legendBlockHeight = sliceCount * LayoutRules.DistributionLegendRowHeight
        val legendStartY = contentCenterY - legendBlockHeight / 2f + 8f
        
        val chartCenterX = rect.left + Poster.CardPadding + LayoutRules.DistributionDonutOuterRadius + LayoutRules.DistributionDonutLeftInset
        val legendStartX = chartCenterX + LayoutRules.DistributionDonutOuterRadius + LayoutRules.DistributionDonutToLegendGap
        
        return ChartLegendLayout(
            chartCenterX = chartCenterX,
            chartCenterY = contentCenterY,
            chartRadius = LayoutRules.DistributionDonutOuterRadius,
            chartStroke = LayoutRules.DistributionDonutStrokeWidth,
            legendX = legendStartX,
            legendY = legendStartY,
            percentRightX = rect.right - LayoutRules.DistributionLegendPercentRightInset,
            rowHeight = LayoutRules.DistributionLegendRowHeight
        )
    }

    private fun drawDreamWakeNightmareSection(canvas: Canvas, context: Context, report: AnnualSleepReport) {
        val rect = Poster.Status
        card(canvas, rect)
        drawSectionHeader(canvas, context, rect, "夜醒 / 做梦 / 噩梦", "基于完整夜间记录", R.drawable.ic_widget_sleep_moon)
        val gap = 24f
        val innerLeft = rect.left + Poster.CardPadding
        val top = rect.top + 86f
        val bottom = rect.bottom - Poster.CardPadding
        val itemWidth = (rect.width() - Poster.CardPadding * 2f - gap * 2f) / 3f
        drawRingMetric(canvas, context, "夜醒天数", report.wakeUpDays, report.wakeUpDaysRate, AnnualReportColors.Blue, R.drawable.ic_report_bed, RectF(innerLeft, top, innerLeft + itemWidth, bottom))
        drawRingMetric(canvas, context, "做梦天数", report.dreamDays, report.dreamDaysRate, AnnualReportColors.Teal, R.drawable.ic_report_cloud, RectF(innerLeft + itemWidth + gap, top, innerLeft + itemWidth * 2f + gap, bottom))
        drawRingMetric(canvas, context, "噩梦天数", report.nightmareDays, report.nightmareDaysRate, AnnualReportColors.Purple, R.drawable.ic_report_nightmare, RectF(innerLeft + itemWidth * 2f + gap * 2f, top, rect.right - Poster.CardPadding, bottom))
    }

    private fun drawNapSection(canvas: Canvas, context: Context, report: AnnualSleepReport) {
        val rect = Poster.Nap
        card(canvas, rect)
        drawSectionHeader(canvas, context, rect, "午睡统计", "基于午睡记录", R.drawable.ic_report_pillow)
        val left = rect.left + Poster.CardPadding
        val top = rect.top + 88f
        val statWidth = 224f
        val statGap = 22f
        val statsRight = left + statWidth * 2f + statGap
        smallStatCard(canvas, RectF(left, top, left + statWidth, top + 112f), "午睡天数", "${report.napDays} 天 · ${AnnualReportTextUtils.formatPercent(report.napDaysRate)}", AnnualReportColors.Blue)
        smallStatCard(canvas, RectF(left + statWidth + statGap, top, left + statWidth * 2f + statGap, top + 112f), "平均午睡", report.averageNapDurationText, AnnualReportColors.Teal)

        val chartCenterX = statsRight + LayoutRules.NapStatsToDonutGap + LayoutRules.NapDonutOuterRadius
        val chartCenterY = rect.top + 160f
        AnnualReportDrawingUtils.drawDonutChart(canvas, chartCenterX, chartCenterY, LayoutRules.NapDonutOuterRadius, LayoutRules.NapDonutStrokeWidth, report.napDurationDistribution, chartColors, innerRadiusOverride = LayoutRules.NapDonutInnerRadius)
        if (report.napDurationDistribution.isNotEmpty()) {
            val iconHalfSize = LayoutRules.NapDonutIconSize / 2f
            AnnualReportDrawingUtils.drawVectorIcon(
                canvas, context, R.drawable.ic_report_pillow,
                RectF(
                    chartCenterX - iconHalfSize,
                    chartCenterY - iconHalfSize,
                    chartCenterX + iconHalfSize,
                    chartCenterY + iconHalfSize
                ),
                AnnualReportColors.Primary, 70
            )
        }
        val napLegendLabelStartX = chartCenterX + LayoutRules.NapDonutOuterRadius + LayoutRules.NapDonutToLegendGap
        val napLegendPercentRightX = napLegendLabelStartX + LayoutRules.NapLegendLabelToPercentGap
        AnnualReportDrawingUtils.drawLegend(
            canvas = canvas,
            slices = report.napDurationDistribution,
            colors = chartColors,
            startX = napLegendLabelStartX,
            startY = rect.top + 96f,
            percentRightX = napLegendPercentRightX,
            rowHeight = LayoutRules.NapLegendRowHeight,
            textSize = LayoutRules.NapLegendTextSize,
            labelTransform = AnnualReportTextUtils::shortNapLabel
        )
        AnnualReportDrawingUtils.drawTextLine(canvas, napSummaryLine(report), left, rect.bottom - 30f, TypeScale.BodySmall, AnnualReportColors.PrimaryDark)
    }

    private fun napSummaryLine(report: AnnualSleepReport): String {
        if (report.napDays <= 2) return "午睡记录较少，当前结果仅供参考。"
        val mainLabel = report.napDurationDistribution.maxByOrNull { it.value }?.label
        return if (mainLabel != null) {
            "午睡多集中在 ${AnnualReportTextUtils.shortNapLabel(mainLabel)}，频率适中。"
        } else {
            "午睡记录较少，当前结果仅供参考。"
        }
    }

    private fun drawSummaryCard(
        canvas: Canvas,
        context: Context,
        rect: RectF,
        title: String,
        iconResId: Int,
        decorationResId: Int,
        lines: List<String>,
        maxLines: Int
    ) {
        card(canvas, rect)
        drawSectionHeader(canvas, context, rect, title, null, iconResId)
        // 绘制正圆图标，保持 1:1 比例
        val iconHalfSize = LayoutRules.DecorationIconSize / 2f
        val iconCenterX = rect.right - Poster.CardPadding - iconHalfSize
        val iconCenterY = rect.top + (rect.height() / 2f)
        AnnualReportDrawingUtils.drawVectorIcon(
            canvas,
            context,
            decorationResId,
            RectF(
                iconCenterX - iconHalfSize,
                iconCenterY - iconHalfSize,
                iconCenterX + iconHalfSize,
                iconCenterY + iconHalfSize
            ),
            AnnualReportColors.Primary,
            64
        )
        drawSummaryTextBlock(
            canvas = canvas,
            lines = lines.take(maxLines).map { "• $it" },
            x = rect.left + Poster.CardPadding,
            contentTop = rect.top + 92f,
            contentBottom = rect.bottom - 42f,
            maxLines = maxLines
        )
    }

    private fun drawFooter(canvas: Canvas) {
        val rect = Poster.Footer
        AnnualReportDrawingUtils.drawTextLine(canvas, "Sleep Check · 年度睡眠报告", Poster.Width / 2f, rect.top + 44f, TypeScale.Caption, AnnualReportColors.TextTertiary, false, Paint.Align.CENTER)
    }

    private fun drawSectionHeader(canvas: Canvas, context: Context, rect: RectF, title: String, hint: String?, iconResId: Int) {
        val iconRect = RectF(rect.left + Poster.CardPadding, rect.top + 28f, rect.left + Poster.CardPadding + 30f, rect.top + 58f)
        AnnualReportDrawingUtils.drawVectorIcon(canvas, context, iconResId, iconRect, AnnualReportColors.PrimaryDark, 180)
        AnnualReportDrawingUtils.drawTextLine(canvas, title, iconRect.right + 12f, rect.top + 54f, TypeScale.SectionTitle, AnnualReportColors.PrimaryDark, true)
        hint?.let {
            AnnualReportDrawingUtils.drawTextLine(canvas, it, rect.right - Poster.CardPadding, rect.top + 54f, TypeScale.SectionHint, AnnualReportColors.TextSecondary, false, Paint.Align.RIGHT)
        }
    }

    private fun drawTopMetric(canvas: Canvas, label: String, value: String, unit: String, x: Float, y: Float) {
        AnnualReportDrawingUtils.drawTextLine(canvas, label, x, y - 26f, TypeScale.SmallTitle, AnnualReportColors.TextPrimary, true, Paint.Align.CENTER)
        AnnualReportDrawingUtils.drawTextLine(canvas, value, x - 18f, y + 38f, TypeScale.HeroNumber, AnnualReportColors.PrimaryDark, true, Paint.Align.CENTER)
        AnnualReportDrawingUtils.drawTextLine(canvas, unit, x + 66f, y + 25f, TypeScale.BodySmall, AnnualReportColors.TextPrimary)
    }

    private fun drawBottomMetric(canvas: Canvas, context: Context, label: String, value: String, iconResId: Int, x: Float, y: Float, valueSize: Float = TypeScale.MetricNumber) {
        AnnualReportDrawingUtils.drawVectorIcon(canvas, context, iconResId, RectF(x - 20f, y - 78f, x + 20f, y - 38f), AnnualReportColors.Primary, 125)
        AnnualReportDrawingUtils.drawTextLine(canvas, label, x, y - 16f, TypeScale.BodySmall, AnnualReportColors.TextSecondary, true, Paint.Align.CENTER)
        AnnualReportDrawingUtils.drawTextLine(canvas, value, x, y + 34f, valueSize, AnnualReportColors.PrimaryDark, true, Paint.Align.CENTER)
    }

    private fun drawRingMetric(canvas: Canvas, context: Context, label: String, days: Int, rate: Float, color: Int, iconResId: Int, rect: RectF) {
        metricSurface(canvas, rect, color)
        val titleBaseline = rect.top + LayoutRules.RingMetricTitleBaselineTopOffset
        AnnualReportDrawingUtils.drawTextLine(canvas, label, rect.centerX(), titleBaseline, TypeScale.SmallTitle, color, true, Paint.Align.CENTER)
        val ringCenterY = titleBaseline + LayoutRules.RingMetricRingCenterTitleBaselineGap
        AnnualReportDrawingUtils.drawSmallProgressRing(
            canvas, rect.centerX(), ringCenterY, rate, color,
            radius = LayoutRules.RingMetricOuterRadius,
            strokeWidth = LayoutRules.RingMetricStrokeWidth
        )
        AnnualReportDrawingUtils.drawVectorIcon(canvas, context, iconResId, RectF(rect.centerX() - 17f, ringCenterY - 17f, rect.centerX() + 17f, ringCenterY + 17f), color, 92)
        val valueBaseline = ringCenterY + LayoutRules.RingMetricOuterRadius + LayoutRules.RingMetricRingToValueGap
        AnnualReportDrawingUtils.drawTextLine(canvas, "$days 天 · ${AnnualReportTextUtils.formatPercent(rate)}", rect.centerX(), valueBaseline, TypeScale.SmallNumber, color, true, Paint.Align.CENTER)
    }

    private fun smallStatCard(canvas: Canvas, rect: RectF, label: String, value: String, accentColor: Int) {
        metricSurface(canvas, rect, accentColor)
        val titleBaseline = rect.top + LayoutRules.NapSmallStatTitleBaselineTopOffset
        AnnualReportDrawingUtils.drawTextLine(canvas, label, rect.centerX(), titleBaseline, LayoutRules.NapSmallStatTitleTextSize, accentColor, true, Paint.Align.CENTER)
        val valueBaseline = titleBaseline + LayoutRules.NapSmallStatTitleToValueBaselineGap
        val valueSize = if (value.length > 8) 28f else LayoutRules.NapSmallStatValueTextSize
        AnnualReportDrawingUtils.drawTextLine(canvas, value, rect.centerX(), valueBaseline, valueSize, AnnualReportColors.PrimaryDark, true, Paint.Align.CENTER)
    }

    private fun drawSummaryTextBlock(
        canvas: Canvas,
        lines: List<String>,
        x: Float,
        contentTop: Float,
        contentBottom: Float,
        maxLines: Int
    ) {
        val visibleLines = lines.take(maxLines)
        if (visibleLines.isEmpty()) return
        val lineHeight = 38f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AnnualReportColors.PrimaryDark
            textSize = TypeScale.Body
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }
        val metrics = paint.fontMetrics
        val textHeight = metrics.descent - metrics.ascent
        val textBlockHeight = textHeight + (visibleLines.size - 1) * lineHeight
        val contentHeight = contentBottom - contentTop
        val firstBaseline = contentTop + (contentHeight - textBlockHeight).coerceAtLeast(0f) / 2f - metrics.ascent
        visibleLines.forEachIndexed { index, line ->
            canvas.drawText(line, x, firstBaseline + index * lineHeight, paint)
        }
    }

    private fun metricSurface(canvas: Canvas, rect: RectF, accentColor: Int) {
        val radius = 24f
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AnnualReportDrawingUtils.withAlpha(accentColor, 10)
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(RectF(rect).apply { offset(0f, 5f) }, radius, radius, shadowPaint)

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AnnualReportDrawingUtils.withAlpha(accentColor, 12)
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(rect, radius, radius, fillPaint)

        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AnnualReportDrawingUtils.withAlpha(accentColor, 36)
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }
        val insetRect = RectF(rect).apply { inset(0.75f, 0.75f) }
        canvas.drawRoundRect(insetRect, radius, radius, borderPaint)
    }

    private fun card(canvas: Canvas, rect: RectF, radius: Float = Poster.Radius, smallShadow: Boolean = false) {
        if (smallShadow) {
            AnnualReportDrawingUtils.drawSmallCardShadow(canvas, rect, radius)
        } else {
            AnnualReportDrawingUtils.drawCardShadow(canvas, rect, radius)
        }
        AnnualReportDrawingUtils.drawRoundCard(canvas, rect, radius, Paint(Paint.ANTI_ALIAS_FLAG))
    }

    private fun drawDivider(
        canvas: Canvas,
        x: Float,
        top: Float,
        bottom: Float,
        color: Int = AnnualReportColors.Divider,
        strokeWidth: Float = 2f
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            this.strokeWidth = strokeWidth
        }
        canvas.drawLine(x, top, x, bottom, paint)
    }

    private fun drawHorizontalDivider(canvas: Canvas, left: Float, y: Float, right: Float) {
        drawLine(canvas, left, y, right, y, AnnualReportColors.Divider, 2f)
    }

    private fun drawLine(canvas: Canvas, startX: Float, startY: Float, endX: Float, endY: Float, color: Int, strokeWidth: Float) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            this.strokeWidth = strokeWidth
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawLine(startX, startY, endX, endY, paint)
    }
}
