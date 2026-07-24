package com.amethamor.sleep.ui.report.monthly

object MonthlyReportTextGenerator {
    private const val LOW_DATA_THRESHOLD = 3
    private const val DAY_MINUTES = 24 * 60

    fun conclusion(
        summary: MonthlyReportSummaryData,
        hasAnyRecord: Boolean
    ): String {
        if (!hasAnyRecord || summary.nightRecordDays < LOW_DATA_THRESHOLD) {
            return "本月记录较少，暂时无法生成完整趋势。继续记录后可以在这里看到更清晰的睡眠变化。"
        }

        val durationText = when {
            summary.averageNightDurationMinutes == null -> "夜间睡眠时长记录还不够完整"
            summary.averageNightDurationMinutes < 6 * 60 -> "夜间睡眠整体偏短"
            summary.averageNightDurationMinutes in (7 * 60)..(9 * 60) -> "夜间睡眠时长整体较稳定"
            else -> "夜间睡眠时长有一定波动"
        }
        val bedText = summary.averageBedTimeMinutes?.let { averageBedMinutes ->
            if (averageBedMinutes >= DAY_MINUTES) {
                "平均入睡 ${formatClockTime(averageBedMinutes)}，入睡时间略偏晚"
            } else {
                "平均入睡 ${formatClockTime(averageBedMinutes)}，作息节奏保持得比较平稳"
            }
        } ?: "入睡时间记录还不够完整"
        val napText = when {
            summary.napCount == 0 -> "本月几乎没有午睡记录，白天休息主要依赖夜间睡眠"
            (summary.averageNapDurationMinutes ?: 0) > 90 -> {
                "午睡平均 ${formatDuration(summary.averageNapDurationMinutes)}，时长偏长"
            }
            summary.averageNapDurationMinutes != null -> {
                "午睡平均 ${formatDuration(summary.averageNapDurationMinutes)}，整体更像是白天的短时补充"
            }
            else -> "午睡记录整体更像是白天的短时补充"
        }
        return "本月${durationText}，${bedText}。${napText}，可以结合每天的状态继续观察。"
    }

    fun suggestions(summary: MonthlyReportSummaryData): List<String> {
        if (summary.nightRecordDays < LOW_DATA_THRESHOLD) {
            return listOf("先继续积累记录，数据多一些后趋势会更清楚。")
        }

        val result = mutableListOf<String>()
        summary.averageBedTimeMinutes?.let { averageBedMinutes ->
            if (averageBedMinutes >= DAY_MINUTES) {
                val targetMinutes = averageBedMinutes - 20
                result += "本月平均入睡 ${formatClockTime(averageBedMinutes)}，偏晚；建议先把目标设为 ${formatClockTime(targetMinutes)} 前。"
            }
        }
        if ((summary.averageNapDurationMinutes ?: 0) > 90) {
            result += "午睡平均 ${formatDuration(summary.averageNapDurationMinutes)}，偏长；若夜间入睡困难，可尝试控制在 30-60 分钟。"
        }
        if ((summary.averageNightDurationMinutes ?: Int.MAX_VALUE) < 6 * 60) {
            result += "本月平均睡眠 ${formatDuration(summary.averageNightDurationMinutes)}，偏短；可以优先回看连续短睡眠的日期。"
        }
        if (summary.napCount <= 2) {
            result += "本月午睡较少，白天休息主要依赖夜间睡眠。"
        }
        if (summary.totalWakeUpCount >= summary.nightRecordDays.coerceAtLeast(1)) {
            result += "夜醒较多的日期可以结合当天状态回看。"
        }
        return result.take(2).ifEmpty {
            listOf("继续保持记录节奏，优先观察入睡时间和睡眠时长的稳定性。")
        }
    }

    private fun formatClockTime(minutes: Int): String {
        val normalized = ((minutes % DAY_MINUTES) + DAY_MINUTES) % DAY_MINUTES
        return "%02d:%02d".format(normalized / 60, normalized % 60)
    }

    private fun formatDuration(minutes: Int?): String {
        if (minutes == null || minutes <= 0) return "暂无数据"
        val hours = minutes / 60
        val rest = minutes % 60
        return if (hours > 0) "${hours}小时${rest}分" else "${rest}分钟"
    }
}
