package com.amethamor.sleep.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.data.SleepRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SleepViewModel(private val repository: SleepRepository) : ViewModel() {
    val latestRecord: StateFlow<SleepRecord?> = repository.observeLatestRecord()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val recentRecords: StateFlow<List<SleepRecord>> = repository.observeRecentRecords(5)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val latestNightRecord: StateFlow<SleepRecord?> = repository.observeLatestNightRecord()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val allRecords: StateFlow<List<SleepRecord>> = repository.observeAllRecords()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun sleepCheckIn() {
        viewModelScope.launch {
            val success = repository.sleepCheckIn()
            if (!success) {
                _message.value = "已有未完成睡眠，请起床打卡"
            }
        }
    }

    fun wakeCheckIn() {
        viewModelScope.launch {
            repository.wakeCheckIn()
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    fun updateSleepRecord(record: SleepRecord) {
        viewModelScope.launch {
            repository.updateRecord(record)
        }
    }

    fun insertSleepRecord(record: SleepRecord) {
        viewModelScope.launch {
            repository.insertRecord(record)
        }
    }

    fun deleteSleepRecord(record: SleepRecord) {
        viewModelScope.launch {
            repository.deleteRecord(record)
        }
    }
}
