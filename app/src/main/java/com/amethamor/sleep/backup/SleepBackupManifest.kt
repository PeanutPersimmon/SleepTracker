package com.amethamor.sleep.backup

import org.json.JSONObject

data class SleepBackupManifest(
    val app: String,
    val backupVersion: Int,
    val createdAt: Long,
    val databaseVersion: Int,
    val recordCount: Int,
    val nightRecordCount: Int,
    val napRecordCount: Int,
    val containsSettings: Boolean,
    val containsCustomThemes: Boolean,
    val customThemeCount: Int
) {
    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("app", app)
        json.put("backupVersion", backupVersion)
        json.put("createdAt", createdAt)
        json.put("databaseVersion", databaseVersion)
        json.put("recordCount", recordCount)
        json.put("nightRecordCount", nightRecordCount)
        json.put("napRecordCount", napRecordCount)
        json.put("containsSettings", containsSettings)
        json.put("containsCustomThemes", containsCustomThemes)
        json.put("customThemeCount", customThemeCount)
        return json
    }

    companion object {
        fun fromJson(json: JSONObject): SleepBackupManifest {
            return SleepBackupManifest(
                app = json.optString("app", ""),
                backupVersion = json.optInt("backupVersion", 0),
                createdAt = json.optLong("createdAt", 0L),
                databaseVersion = json.optInt("databaseVersion", 0),
                recordCount = json.optInt("recordCount", 0),
                nightRecordCount = json.optInt("nightRecordCount", 0),
                napRecordCount = json.optInt("napRecordCount", 0),
                containsSettings = json.optBoolean("containsSettings", false),
                containsCustomThemes = json.optBoolean("containsCustomThemes", false),
                customThemeCount = json.optInt("customThemeCount", 0)
            )
        }
    }
}
