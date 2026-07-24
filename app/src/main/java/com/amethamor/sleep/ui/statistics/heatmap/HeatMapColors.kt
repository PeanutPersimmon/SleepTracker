package com.amethamor.sleep.ui.statistics.heatmap

import androidx.compose.ui.graphics.Color
import com.amethamor.sleep.ui.theme.SleepColorScheme

object HeatMapColors {
    private val wakeFeelingAwake = Color(0xFF8BCF9A)
    private val wakeFeelingOkay = Color(0xFF8FB6D9)
    private val wakeFeelingAverage = Color(0xFFE2C76F)
    private val wakeFeelingSleepy = Color(0xFF8A9099)
    private val wakeFeelingExhausted = Color(0xFFE08A8A)

    fun colorFor(
        type: HeatMapType,
        day: HeatMapDay,
        colors: SleepColorScheme
    ): Color {
        val noRecordColor = colors.calendarMissing.copy(alpha = 0.95f)
        if (day.isFuture) return colors.calendarFuture.copy(alpha = 0.78f)
        if (!day.hasRecord) return noRecordColor

        return when (type) {
            HeatMapType.Dream -> if (day.level == 1) {
                colors.dream.copy(alpha = 0.62f)
            } else {
                noRecordColor
            }
            HeatMapType.WakeUp -> wakeUpColor(day.level, colors)
            HeatMapType.Nightmare -> if (day.level == 1) {
                colors.nightmare.copy(alpha = 0.50f)
            } else {
                noRecordColor
            }
            HeatMapType.WakeFeeling -> wakeFeelingColor(day.level, noRecordColor)
        }
    }

    private fun wakeUpColor(level: Int?, colors: SleepColorScheme): Color {
        val noRecordColor = colors.calendarMissing.copy(alpha = 0.95f)
        return when (level) {
            0 -> noRecordColor
            1 -> colors.awake.copy(alpha = 0.38f)
            2 -> colors.awake.copy(alpha = 0.58f)
            3, 4, 5, 6, 7, 8, 9, 10 -> colors.awake.copy(alpha = 0.75f)
            else -> noRecordColor
        }
    }

    private fun wakeFeelingColor(level: Int?, noRecordColor: Color): Color {
        return when (level) {
            1 -> wakeFeelingAwake.copy(alpha = 0.90f)
            2 -> wakeFeelingOkay.copy(alpha = 0.90f)
            3 -> wakeFeelingAverage.copy(alpha = 0.90f)
            4 -> wakeFeelingSleepy.copy(alpha = 0.90f)
            5 -> wakeFeelingExhausted.copy(alpha = 0.90f)
            else -> noRecordColor
        }
    }
}
