package com.amethamor.sleep.backup

sealed class SleepBackupResult {
    object Idle : SleepBackupResult()
    object Exporting : SleepBackupResult()
    object Importing : SleepBackupResult()
    data class Success(val message: String) : SleepBackupResult()
    data class Error(val message: String) : SleepBackupResult()
}
