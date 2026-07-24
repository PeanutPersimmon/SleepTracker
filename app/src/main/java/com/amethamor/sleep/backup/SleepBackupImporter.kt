package com.amethamor.sleep.backup

import android.content.Context
import android.net.Uri
import com.amethamor.sleep.data.SleepDatabase
import org.json.JSONObject
import java.io.File

object SleepBackupImporter {

    suspend fun importBackup(
        context: Context,
        backupUri: Uri
    ): SleepBackupResult {
        var tempDir: File? = null
        var rollbackDir: File? = null
        return try {
            tempDir = File(context.cacheDir, "sleep_import_${System.currentTimeMillis()}")
            tempDir.mkdirs()
            
            val unzipSuccess = BackupZipUtils.unzipFile(backupUri, tempDir, context)
            if (!unzipSuccess) {
                return SleepBackupResult.Error("解压备份失败")
            }
            
            val contentDir = findContentDirectory(tempDir)
            if (contentDir == null) {
                return SleepBackupResult.Error("这不是有效的 Sleep 备份文件")
            }
            
            val manifestFile = File(contentDir, SleepBackupConstants.MANIFEST_FILE_NAME)
            if (!manifestFile.exists()) {
                return SleepBackupResult.Error("这不是有效的 Sleep 备份文件")
            }
            
            val manifest = SleepBackupManifest.fromJson(JSONObject(manifestFile.readText()))
            
            if (manifest.app != SleepBackupConstants.APP_NAME) {
                return SleepBackupResult.Error("这不是有效的 Sleep 备份文件")
            }
            
            if (manifest.backupVersion > SleepBackupConstants.BACKUP_VERSION) {
                return SleepBackupResult.Error("备份来自更新版本的 App，当前版本无法导入")
            }
            
            if (manifest.databaseVersion > SleepBackupConstants.DATABASE_VERSION) {
                return SleepBackupResult.Error("备份来自更新版本的 App，当前版本无法导入")
            }
            
            val databaseDir = File(contentDir, SleepBackupConstants.DATABASE_DIR_NAME)
            if (!databaseDir.exists()) {
                return SleepBackupResult.Error("这不是有效的 Sleep 备份文件")
            }
            
            rollbackDir = File(context.cacheDir, "sleep_rollback_${System.currentTimeMillis()}")
            rollbackDir.mkdirs()
            
            val rollbackDbDir = File(rollbackDir, SleepBackupConstants.DATABASE_DIR_NAME)
            val rollbackSettingsDir = File(rollbackDir, SleepBackupConstants.SETTINGS_DIR_NAME)
            
            val rollbackDbSuccess = DatabaseBackupHelper.backupCurrentDatabaseToDirectory(context, rollbackDbDir)
            val rollbackSettingsSuccess = SettingsBackupHelper.backupCurrentSettingsToDirectory(context, rollbackSettingsDir)
            if (!rollbackDbSuccess || !rollbackSettingsSuccess) {
                return SleepBackupResult.Error("导入前备份当前数据失败，已取消导入")
            }
            
            SleepDatabase.closeDatabase()
            
            val importDbSuccess = DatabaseBackupHelper.copyDatabaseFilesFromDirectory(databaseDir, context)
            if (!importDbSuccess) {
                rollback(context, rollbackDir)
                return SleepBackupResult.Error("导入数据库失败，已恢复原数据")
            }
            
            val settingsDir = File(contentDir, SleepBackupConstants.SETTINGS_DIR_NAME)
            if (settingsDir.exists()) {
                val importSettingsSuccess = SettingsBackupHelper.importThemeSettings(context, settingsDir)
                if (!importSettingsSuccess) {
                    rollback(context, rollbackDir)
                    return SleepBackupResult.Error("导入设置失败，已恢复原数据")
                }
            }
            
            SleepBackupResult.Success("导入成功，正在重启 App")
        } catch (e: Exception) {
            e.printStackTrace()
            rollbackDir?.let { rollback(context, it) }
            SleepBackupResult.Error("导入失败，请重试")
        } finally {
            tempDir?.let { BackupZipUtils.deleteDirectory(it) }
            rollbackDir?.let { BackupZipUtils.deleteDirectory(it) }
        }
    }

    private fun rollback(context: Context, rollbackDir: File) {
        try {
            SleepDatabase.closeDatabase()
            
            val rollbackDbDir = File(rollbackDir, SleepBackupConstants.DATABASE_DIR_NAME)
            if (rollbackDbDir.exists()) {
                DatabaseBackupHelper.copyDatabaseFilesFromDirectory(rollbackDbDir, context)
            }
            
            val rollbackSettingsDir = File(rollbackDir, SleepBackupConstants.SETTINGS_DIR_NAME)
            if (rollbackSettingsDir.exists()) {
                SettingsBackupHelper.importThemeSettings(context, rollbackSettingsDir)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun findContentDirectory(rootDir: File): File? {
        val manifestFile = File(rootDir, SleepBackupConstants.MANIFEST_FILE_NAME)
        if (manifestFile.exists()) {
            return rootDir
        }
        
        val subDirs = rootDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
        for (subDir in subDirs) {
            val subManifest = File(subDir, SleepBackupConstants.MANIFEST_FILE_NAME)
            if (subManifest.exists()) {
                return subDir
            }
        }
        
        return null
    }

    suspend fun validateBackup(
        context: Context,
        backupUri: Uri
    ): Pair<Boolean, SleepBackupManifest?> {
        var tempDir: File? = null
        return try {
            tempDir = File(context.cacheDir, "sleep_validate_${System.currentTimeMillis()}")
            tempDir.mkdirs()
            
            val unzipSuccess = BackupZipUtils.unzipFile(backupUri, tempDir, context)
            if (!unzipSuccess) {
                return Pair(false, null)
            }
            
            val contentDir = findContentDirectory(tempDir)
            if (contentDir == null) {
                return Pair(false, null)
            }
            
            val manifestFile = File(contentDir, SleepBackupConstants.MANIFEST_FILE_NAME)
            if (!manifestFile.exists()) {
                return Pair(false, null)
            }
            
            val manifest = SleepBackupManifest.fromJson(JSONObject(manifestFile.readText()))
            
            val isValid = manifest.app == SleepBackupConstants.APP_NAME &&
                    manifest.backupVersion <= SleepBackupConstants.BACKUP_VERSION &&
                    manifest.databaseVersion <= SleepBackupConstants.DATABASE_VERSION
            
            Pair(isValid, manifest)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(false, null)
        } finally {
            tempDir?.let { BackupZipUtils.deleteDirectory(it) }
        }
    }
}
