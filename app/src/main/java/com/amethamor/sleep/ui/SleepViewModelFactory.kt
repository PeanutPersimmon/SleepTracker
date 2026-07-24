package com.amethamor.sleep.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amethamor.sleep.data.SleepRepository

class SleepViewModelFactory(private val repository: SleepRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepViewModel::class.java)) {
            return SleepViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
