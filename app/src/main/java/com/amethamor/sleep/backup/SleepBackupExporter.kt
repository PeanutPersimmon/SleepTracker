package com.amethamor.sleep.backup

import android.content.Context
import android.net.Uri
import com.amethamor.sleep.data.SleepDatabase
import com.amethamor.sleep.ui.theme.ThemeSettingsRepository
import java.io.File
import java.io.FileWriter
import java.util.Date

object SleepBackupExporter {

    suspend fun exportBackup(
        context: Context,
        destinationUri: Uri,
        database: SleepDatabase
    ): SleepBackupResult {
        var tempDir: File? = null
        return try {
            val dao = database.sleepRecordDao()
            
            val totalCount = dao.getTotalRecordCount()
            val nightCount = dao.getNightRecordCount()
            val napCount = dao.getNapRecordCount()
            
            val themeRepository = ThemeSettingsRepository(context)
            val customThemes = themeRepository.loadCustomThemes()
            val customThemeCount = customThemes.size
            
            tempDir = File(context.cacheDir, "sleep_backup_${System.currentTimeMillis()}")
            tempDir.mkdirs()
            
            val databaseDir = File(tempDir, SleepBackupConstants.DATABASE_DIR_NAME)
            val settingsDir = File(tempDir, SleepBackupConstants.SETTINGS_DIR_NAME)
            
            val dbSuccess = DatabaseBackupHelper.copyDatabaseFilesToDirectory(context, databaseDir)
            if (!dbSuccess) {
                return SleepBackupResult.Error("导出数据库失败")
            }
            
            val settingsSuccess = SettingsBackupHelper.exportThemeSettings(context, settingsDir)
            if (!settingsSuccess) {
                return SleepBackupResult.Error("导出设置失败")
            }
            
            val manifest = SleepBackupManifest(
                app = SleepBackupConstants.APP_NAME,
                backupVersion = SleepBackupConstants.BACKUP_VERSION,
                createdAt = Date().time,
                databaseVersion = SleepBackupConstants.DATABASE_VERSION,
                recordCount = totalCount,
                nightRecordCount = nightCount,
                napRecordCount = napCount,
                containsSettings = true,
                containsCustomThemes = customThemeCount > 0,
                customThemeCount = customThemeCount
            )
            
            val manifestFile = File(tempDir, SleepBackupConstants.MANIFEST_FILE_NAME)
            FileWriter(manifestFile).use { writer ->
                writer.write(manifest.toJson().toString(2))
            }
            
            val zipSuccess = BackupZipUtils.zipDirectory(tempDir, destinationUri, context)
            if (!zipSuccess) {
                return SleepBackupResult.Error("压缩备份失败")
            }
            
            SleepBackupResult.Success("导出成功")
        } catch (e: Exception) {
            e.printStackTrace()
            SleepBackupResult.Error("导出失败，请重试")
        } finally {
            tempDir?.let {
                BackupZipUtils.deleteDirectory(it)
            }
        }
    }
}
