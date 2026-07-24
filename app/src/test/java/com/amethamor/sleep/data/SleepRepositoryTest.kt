package com.amethamor.sleep.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SleepRepositoryTest {
    @Test
    fun concurrentSleepCheckIn_createsOnlyOneOpenRecord() = runBlocking {
        val dao = FakeSleepRecordDao(mutableListOf(), findDelayMillis = 25)
        val repository = SleepRepository(dao)

        val results = coroutineScope {
            listOf(
                async(Dispatchers.Default) { repository.sleepCheckIn() },
                async(Dispatchers.Default) { repository.sleepCheckIn() }
            ).awaitAll()
        }

        assertEquals(1, results.count { it })
        assertEquals(1, results.count { !it })
        assertEquals(
            1,
            dao.insertedRecords.count { it.bedTimeMillis != null && it.wakeTimeMillis == null }
        )
    }

    @Test
    fun wakeCheckIn_doesNotCompleteOpenRecordOlderThan24Hours() = runBlocking {
        val staleRecord = record(
            bedTimeMillis = System.currentTimeMillis() - 25 * HOUR_MILLIS,
            wakeTimeMillis = null
        )
        val dao = FakeSleepRecordDao(mutableListOf(staleRecord))
        val repository = SleepRepository(dao)

        repository.wakeCheckIn()

        assertTrue(dao.updatedRecords.isEmpty())
        assertEquals(1, dao.insertedRecords.size)
        assertNull(dao.insertedRecords.single().bedTimeMillis)
        assertNotNull(dao.insertedRecords.single().wakeTimeMillis)
        assertEquals(24 * HOUR_MILLIS, dao.lastCurrentTimeMillis - dao.lastMinimumBedTimeMillis)
    }

    @Test
    fun wakeCheckIn_completesOpenRecordWithin24Hours() = runBlocking {
        val openRecord = record(
            bedTimeMillis = System.currentTimeMillis() - HOUR_MILLIS,
            wakeTimeMillis = null
        )
        val dao = FakeSleepRecordDao(mutableListOf(openRecord))
        val repository = SleepRepository(dao)

        repository.wakeCheckIn()

        assertTrue(dao.insertedRecords.isEmpty())
        assertEquals(openRecord.id, dao.updatedRecords.single().id)
        assertNotNull(dao.updatedRecords.single().wakeTimeMillis)
    }

    private class FakeSleepRecordDao(
        private val records: MutableList<SleepRecord>,
        private val findDelayMillis: Long = 0
    ) : SleepRecordDao {
        private val allRecordsFlow = MutableStateFlow(records.toList())
        val insertedRecords = mutableListOf<SleepRecord>()
        val updatedRecords = mutableListOf<SleepRecord>()
        var lastMinimumBedTimeMillis: Long = 0
        var lastCurrentTimeMillis: Long = 0

        override fun observeAllRecords(): Flow<List<SleepRecord>> = allRecordsFlow

        override fun observeRecentRecords(limit: Int): Flow<List<SleepRecord>> =
            MutableStateFlow(records.take(limit))

        override fun observeLatestRecord(): Flow<SleepRecord?> = MutableStateFlow(records.firstOrNull())

        override fun observeLatestNightRecord(): Flow<SleepRecord?> =
            MutableStateFlow(records.firstOrNull { it.sleepType == SleepType.NIGHT })

        override suspend fun getLatestRecord(): SleepRecord? = records.firstOrNull()

        override suspend fun findLatestOpenBedRecord(
            minimumBedTimeMillis: Long,
            currentTimeMillis: Long
        ): SleepRecord? {
            if (findDelayMillis > 0) delay(findDelayMillis)
            lastMinimumBedTimeMillis = minimumBedTimeMillis
            lastCurrentTimeMillis = currentTimeMillis
            return records
                .filter { record ->
                    record.bedTimeMillis != null &&
                        record.wakeTimeMillis == null &&
                        record.bedTimeMillis in minimumBedTimeMillis..currentTimeMillis
                }
                .maxByOrNull { it.bedTimeMillis ?: Long.MIN_VALUE }
        }

        override suspend fun insert(record: SleepRecord): Long {
            insertedRecords += record
            records += record
            allRecordsFlow.value = records.toList()
            return record.id
        }

        override suspend fun update(record: SleepRecord) {
            updatedRecords += record
            records.replaceAll { existing -> if (existing.id == record.id) record else existing }
            allRecordsFlow.value = records.toList()
        }

        override suspend fun delete(record: SleepRecord) {
            records.remove(record)
            allRecordsFlow.value = records.toList()
        }

        override suspend fun getTotalRecordCount(): Int = records.size

        override suspend fun getNightRecordCount(): Int = records.count { it.sleepType == SleepType.NIGHT }

        override suspend fun getNapRecordCount(): Int = records.count { it.sleepType == SleepType.NAP }
    }

    private companion object {
        const val HOUR_MILLIS = 60 * 60 * 1000L

        fun record(
            bedTimeMillis: Long?,
            wakeTimeMillis: Long?,
            id: Long = 1
        ) = SleepRecord(
            id = id,
            recordDate = "2026-07-23",
            bedTimeMillis = bedTimeMillis,
            wakeTimeMillis = wakeTimeMillis,
            durationMinutes = null,
            createdAt = 1,
            updatedAt = 1
        )
    }
}
