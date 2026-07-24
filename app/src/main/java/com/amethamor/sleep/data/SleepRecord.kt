package com.amethamor.sleep.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_records")
data class SleepRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recordDate: String,
    val bedTimeMillis: Long?,
    val wakeTimeMillis: Long?,
    val durationMinutes: Int?,
    val hasDream: Boolean = false,
    val wakeUpCount: Int = 0,
    val hasNightmare: Boolean = false,
    val wakeFeeling: String? = "一般",
    val sleepType: String = SleepType.NIGHT,
    val sleepTypeManuallySet: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
