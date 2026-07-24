package com.amethamor.sleep.ui.report.annual

import kotlin.math.roundToInt

object AnnualReportTextUtils {
    fun formatDuration(minutes: Int?): String {
        if (minutes == null || minutes <= 0) return "暂无数据"
        val hours = minutes / 60
        val rest = minutes % 60
        return if (hours > 0) "${hours}小时${rest}分" else "${rest}分钟"
    }

    fun formatNapDuration(minutes: Int?): String {
        if (minutes == null || minutes <= 0) return "暂无数据"
        return "${minutes}分钟"
    }

    fun formatTimeFromMinutes(minutes: Int?): String {
        if (minutes == null) return "暂无数据"
        val normalized = ((minutes % (24 * 60)) + 24 * 60) % (24 * 60)
        return "%02d:%02d".format(normalized / 60, normalized % 60)
    }

    fun formatPercent(percent: Float): String {
        return "${(percent.coerceIn(0f, 1f) * 100f).roundToInt()}%"
    }

    fun buildPieSlices(labelsAndValues: List<Pair<String, Int>>): List<PieSliceData> {
        val total = labelsAndValues.sumOf { it.second }
        return labelsAndValues
            .filter { it.second > 0 }
            .map { (label, value) ->
                PieSliceData(
                    label = label,
                    value = value,
                    percent = if (total > 0) value.toFloat() / total else 0f
                )
            }
    }

    fun buildSleepWakeSummary(
        averageBedMinutes: Int?,
        averageWakeMinutes: Int?,
        totalRecordDays: Int,
        completeNightDays: Int = totalRecordDays
    ): List<String> {
        if (averageBedMinutes == null && averageWakeMinutes == null) {
            return listOf(
                "今年的入睡与起床时间记录还不够完整。",
                "继续记录后，可以看到更清晰的作息节奏。"
            )
        }

        val isSmallSample = totalRecordDays < 30 || completeNightDays < 30
        val lines = mutableListOf<String>()
        averageBedMinutes?.let {
            lines += when {
                isSmallSample && it >= 23 * 60 + 30 -> "当前记录显示入睡时间较规律。"
                isSmallSample -> "当前记录显示入睡时间较规律。"
                it >= 25 * 60 -> "平均入睡时间明显偏晚，整体有晚睡倾向。"
                it >= 23 * 60 + 30 -> "平均入睡时间略偏晚，作息还有前移空间。"
                else -> "平均入睡时间较规律，夜间节奏保持得不错。"
            }
        }
        averageWakeMinutes?.let {
            lines += when {
                isSmallSample && it >= 9 * 60 + 30 -> "当前记录显示起床时间略偏晚。"
                isSmallSample -> "当前记录显示起床时间较稳定。"
                it >= 9 * 60 + 30 -> "平均起床时间偏晚，适合循序渐进地调整。"
                it < 8 * 60 + 30 -> "平均起床时间较早，早晨节奏比较清爽。"
                else -> "平均起床时间较稳定，日常节奏比较平衡。"
            }
        }
        lines += if (isSmallSample) {
            "建议继续记录，以便形成更稳定的年度判断。"
        } else {
            "建议每次提前 15-20 分钟入睡，循序调整作息。"
        }
        return lines.take(3)
    }

    fun buildAnnualSummary(report: AnnualSummaryInput): List<String> {
        val lines = mutableListOf<String>()
        lines += when {
            report.completeNightDays < 30 -> "今年记录天数较少，年度结论仅供参考。"
            report.completeRate >= 0.8f -> "这一年的睡眠记录较稳定，完整记录占比很高。"
            report.completeRate >= 0.5f -> "这一年保留了不少睡眠记录，可以继续提高完整度。"
            else -> "这一年的记录还比较零散，可以从固定打卡开始。"
        }
        report.mainNightDurationLabel?.let {
            lines += "睡眠时长主要集中在 $it 区间。"
        }
        lines += when {
            report.dreamDaysRate >= 0.35f && report.nightmareDaysRate < 0.12f -> "做梦较常见，但噩梦天数相对较少。"
            report.nightmareDaysRate >= 0.12f -> "噩梦偶有出现，记录下来有助于观察变化。"
            else -> "梦境记录整体平稳，适合继续轻松观察。"
        }
        lines += when {
            report.napDays <= 0 -> "今年午睡记录较少，白天休息节奏比较轻。"
            report.napDays <= 2 -> "午睡记录较少，可继续观察习惯变化。"
            report.mainNapDurationLabel != null -> "午睡习惯较稳定，多集中在 ${report.mainNapDurationLabel}。"
            else -> "今年有午睡记录，短暂休息为白天补了一点能量。"
        }
        return lines.take(4)
    }

    fun shortNapLabel(label: String): String {
        return when (label) {
            "0-30 分钟" -> "0–30m"
            "30-60 分钟" -> "30–60m"
            "60-90 分钟" -> "60–90m"
            "90 分钟以上" -> ">90m"
            else -> label.replace(" ", "")
        }
    }
}

data class AnnualSummaryInput(
    val totalRecordDays: Int,
    val completeNightDays: Int,
    val completeRate: Float,
    val mainNightDurationLabel: String?,
    val dreamDaysRate: Float,
    val nightmareDaysRate: Float,
    val napDays: Int,
    val mainNapDurationLabel: String?
)
