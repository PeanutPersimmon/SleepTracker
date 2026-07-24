package com.amethamor.sleep.backup

import android.content.Context
import com.amethamor.sleep.ui.theme.ThemeSettingsRepository
import org.json.JSONObject
import java.io.File
import java.io.FileWriter

object SettingsBackupHelper {

    fun exportThemeSettings(context: Context, targetDir: File): Boolean {
        return try {
            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }
            
            val themeRepository = ThemeSettingsRepository(context)
            val settingsJson = themeRepository.exportThemeSettingsToJson()
            
            val settingsFile = File(targetDir, SleepBackupConstants.THEME_SETTINGS_FILE_NAME)
            FileWriter(settingsFile).use { writer ->
                writer.write(settingsJson.toString(2))
            }
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun importThemeSettings(context: Context, sourceDir: File): Boolean {
        return try {
            val settingsFile = File(sourceDir, SleepBackupConstants.THEME_SETTINGS_FILE_NAME)
            if (!settingsFile.exists()) {
                return false
            }
            
            val jsonString = settingsFile.readText()
            val settingsJson = JSONObject(jsonString)
            
            val themeRepository = ThemeSettingsRepository(context)
            themeRepository.importThemeSettingsFromJson(settingsJson)
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun backupCurrentSettingsToDirectory(context: Context, backupDir: File): Boolean {
        return exportThemeSettings(context, backupDir)
    }
}
