package com.amethamor.sleep.ui.statistics

import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.util.DateTimeUtils
import java.time.LocalDate

object StatisticsRecordUtils {
    fun latestRecordsByDate(records: List<SleepRecord>): Map<LocalDate, SleepRecord> {
        return records
            .mapNotNull { record ->
                DateTimeUtils.parseRecordDateOrNull(record.recordDate)
                    ?.let { date -> date to record }
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, dailyRecords) -> dailyRecords.maxBy { it.updatedAt } }
    }
}
