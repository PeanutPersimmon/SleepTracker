package com.amethamor.sleep.data

import com.amethamor.sleep.util.DateTimeUtils
import com.amethamor.sleep.util.SleepCalculateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SleepRepository(private val dao: SleepRecordDao) {
    private val sleepCheckMutex = Mutex()

    fun observeLatestRecord(): Flow<SleepRecord?> = dao.observeLatestRecord()

    fun observeLatestNightRecord(): Flow<SleepRecord?> = dao.observeLatestNightRecord()

    fun observeRecentRecords(limit: Int): Flow<List<SleepRecord>> = dao.observeRecentRecords(limit)

    fun observeAllRecords(): Flow<List<SleepRecord>> = dao.observeAllRecords()

    suspend fun sleepCheckIn(): Boolean = sleepCheckMutex.withLock {
        val now = DateTimeUtils.currentMillis()
        val openRecord = dao.findLatestOpenBedRecord(now - MAX_OPEN_SLEEP_DURATION_MILLIS, now)
        if (openRecord != null) {
            return@withLock false
        }

        val record = SleepRecord(
            recordDate = SleepCalculateUtils.calculateRecordDate(now, null),
            bedTimeMillis = now,
            wakeTimeMillis = null,
            durationMinutes = null,
            hasDream = false,
            wakeUpCount = 0,
            hasNightmare = false,
            wakeFeeling = "一般",
            createdAt = now,
            updatedAt = now
        )
        dao.insert(record)
        true
    }

    suspend fun wakeCheckIn() {
        val now = DateTimeUtils.currentMillis()
        val openRecord = dao.findLatestOpenBedRecord(now - MAX_OPEN_SLEEP_DURATION_MILLIS, now)

        if (openRecord != null) {
            val durationMinutes = SleepCalculateUtils.calculateDurationMinutes(openRecord.bedTimeMillis, now)
            val sleepType = SleepCalculateUtils.resolveSleepType(
                bedTimeMillis = openRecord.bedTimeMillis,
                wakeTimeMillis = now,
                durationMinutes = durationMinutes,
                selectedSleepType = openRecord.sleepType,
                sleepTypeManuallySet = openRecord.sleepTypeManuallySet
            )
            val updatedRecord = openRecord.copy(
                wakeTimeMillis = now,
                durationMinutes = durationMinutes,
                recordDate = SleepCalculateUtils.calculateRecordDate(openRecord.bedTimeMillis, now),
                sleepType = sleepType,
                updatedAt = now
            )
            dao.update(updatedRecord)
        } else {
            val record = SleepRecord(
                recordDate = SleepCalculateUtils.calculateRecordDate(null, now),
                bedTimeMillis = null,
                wakeTimeMillis = now,
                durationMinutes = null,
                hasDream = false,
                wakeUpCount = 0,
                hasNightmare = false,
                wakeFeeling = "一般",
                createdAt = now,
                updatedAt = now
            )
            dao.insert(record)
        }
    }

    suspend fun deleteRecord(record: SleepRecord) {
        dao.delete(record)
    }

    suspend fun updateRecord(record: SleepRecord) {
        dao.update(record)
    }

    suspend fun insertRecord(record: SleepRecord): Long {
        return dao.insert(record)
    }

    private companion object {
        const val MAX_OPEN_SLEEP_DURATION_MILLIS = 24 * 60 * 60 * 1000L
    }
}
