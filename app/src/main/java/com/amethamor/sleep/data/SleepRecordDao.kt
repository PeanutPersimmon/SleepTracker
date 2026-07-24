package com.amethamor.sleep.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepRecordDao {
    @Query("SELECT * FROM sleep_records ORDER BY recordDate DESC, createdAt DESC")
    fun observeAllRecords(): Flow<List<SleepRecord>>

    @Query("SELECT * FROM sleep_records ORDER BY recordDate DESC, createdAt DESC LIMIT :limit")
    fun observeRecentRecords(limit: Int): Flow<List<SleepRecord>>

    @Query("SELECT * FROM sleep_records ORDER BY recordDate DESC, createdAt DESC LIMIT 1")
    fun observeLatestRecord(): Flow<SleepRecord?>

    @Query("SELECT * FROM sleep_records WHERE sleepType = 'NIGHT' ORDER BY recordDate DESC, createdAt DESC LIMIT 1")
    fun observeLatestNightRecord(): Flow<SleepRecord?>

    @Query("SELECT * FROM sleep_records ORDER BY recordDate DESC, createdAt DESC LIMIT 1")
    suspend fun getLatestRecord(): SleepRecord?

    @Query(
        "SELECT * FROM sleep_records " +
            "WHERE bedTimeMillis IS NOT NULL AND wakeTimeMillis IS NULL " +
            "AND bedTimeMillis BETWEEN :minimumBedTimeMillis AND :currentTimeMillis " +
            "ORDER BY bedTimeMillis DESC LIMIT 1"
    )
    suspend fun findLatestOpenBedRecord(
        minimumBedTimeMillis: Long,
        currentTimeMillis: Long
    ): SleepRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: SleepRecord): Long

    @Update
    suspend fun update(record: SleepRecord)

    @Delete
    suspend fun delete(record: SleepRecord)

    @Query("SELECT COUNT(*) FROM sleep_records")
    suspend fun getTotalRecordCount(): Int

    @Query("SELECT COUNT(*) FROM sleep_records WHERE sleepType = 'NIGHT'")
    suspend fun getNightRecordCount(): Int

    @Query("SELECT COUNT(*) FROM sleep_records WHERE sleepType = 'NAP'")
    suspend fun getNapRecordCount(): Int
}
