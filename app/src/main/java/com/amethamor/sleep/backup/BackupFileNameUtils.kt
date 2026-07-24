package com.amethamor.sleep.backup

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupFileNameUtils {
    fun generateDefaultFileName(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd_HHmm", Locale.getDefault())
        val timestamp = sdf.format(Date())
        return "SleepBackup_$timestamp${SleepBackupConstants.BACKUP_FILE_EXTENSION}"
    }
}
