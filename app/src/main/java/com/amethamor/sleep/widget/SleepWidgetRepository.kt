package com.amethamor.sleep.widget

import android.content.Context
import com.amethamor.sleep.data.SleepDatabase
import com.amethamor.sleep.data.SleepRepository

class SleepWidgetRepository(context: Context) {
    private val repository = SleepRepository(
        SleepDatabase.getDatabase(context.applicationContext).sleepRecordDao()
    )

    suspend fun sleepCheckIn(): Boolean = repository.sleepCheckIn()

    suspend fun wakeCheckIn() {
        repository.wakeCheckIn()
    }
}
