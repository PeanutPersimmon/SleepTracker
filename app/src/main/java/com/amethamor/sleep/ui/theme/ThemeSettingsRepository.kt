package com.amethamor.sleep.ui.theme

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class ThemeSettingsRepository(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(
        "sleep_theme_settings",
        Context.MODE_PRIVATE
    )

    fun loadSelectedThemeId(): String {
        return prefs.getString(KEY_SELECTED_THEME_ID, DefaultSleepBaseTheme.id)
            ?: DefaultSleepBaseTheme.id
    }

    fun saveSelectedThemeId(themeId: String) {
        prefs.edit().putString(KEY_SELECTED_THEME_ID, themeId).apply()
    }

    fun loadCustomThemes(): List<CustomSleepTheme> {
        val jsonString = prefs.getString(KEY_CUSTOM_THEMES_JSON, "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)
        val themes = mutableListOf<CustomSleepTheme>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            themes.add(
                CustomSleepTheme(
                    id = jsonObject.getString("id"),
                    name = jsonObject.getString("name"),
                    primaryHex = jsonObject.getString("primaryHex"),
                    backgroundTintHex = jsonObject.getString("backgroundTintHex"),
                    accentHex = jsonObject.getString("accentHex")
                )
            )
        }
        return themes
    }

    fun saveCustomThemes(themes: List<CustomSleepTheme>) {
        val jsonArray = JSONArray()
        for (theme in themes) {
            val jsonObject = JSONObject()
            jsonObject.put("id", theme.id)
            jsonObject.put("name", theme.name)
            jsonObject.put("primaryHex", theme.primaryHex)
            jsonObject.put("backgroundTintHex", theme.backgroundTintHex)
            jsonObject.put("accentHex", theme.accentHex)
            jsonArray.put(jsonObject)
        }
        prefs.edit().putString(KEY_CUSTOM_THEMES_JSON, jsonArray.toString()).apply()
    }

    fun addCustomTheme(theme: CustomSleepTheme) {
        val currentThemes = loadCustomThemes().toMutableList()
        currentThemes.add(theme)
        saveCustomThemes(currentThemes)
        saveSelectedThemeId(theme.id)
    }

    fun deleteCustomTheme(themeId: String) {
        val currentThemes = loadCustomThemes().toMutableList()
        currentThemes.removeAll { it.id == themeId }
        saveCustomThemes(currentThemes)
        
        if (loadSelectedThemeId() == themeId) {
            saveSelectedThemeId(DefaultSleepBaseTheme.id)
        }
    }

    fun getAllThemes(): List<SleepBaseTheme> {
        val presetThemes = SleepThemeOptions
        val customThemes = loadCustomThemes().map { it.toBaseTheme() }
        return presetThemes + customThemes
    }

    fun getSelectedTheme(): SleepBaseTheme {
        val selectedId = loadSelectedThemeId()
        val allThemes = getAllThemes()
        val found = allThemes.firstOrNull { it.id == selectedId }
        if (found != null) {
            return found
        }
        saveSelectedThemeId(DefaultSleepBaseTheme.id)
        return DefaultSleepBaseTheme
    }

    fun exportThemeSettingsToJson(): JSONObject {
        val json = JSONObject()
        json.put("selectedThemeId", loadSelectedThemeId())
        
        val customThemesArray = JSONArray()
        val customThemes = loadCustomThemes()
        for (theme in customThemes) {
            val themeJson = JSONObject()
            themeJson.put("id", theme.id)
            themeJson.put("name", theme.name)
            themeJson.put("primaryHex", theme.primaryHex)
            themeJson.put("backgroundTintHex", theme.backgroundTintHex)
            themeJson.put("accentHex", theme.accentHex)
            customThemesArray.put(themeJson)
        }
        json.put("customThemes", customThemesArray)
        
        return json
    }

    fun importThemeSettingsFromJson(json: JSONObject) {
        val selectedThemeId = json.optString("selectedThemeId", DefaultSleepBaseTheme.id)
        val customThemesArray = json.optJSONArray("customThemes")
        
        val customThemes = mutableListOf<CustomSleepTheme>()
        if (customThemesArray != null) {
            for (i in 0 until customThemesArray.length()) {
                val themeJson = customThemesArray.getJSONObject(i)
                customThemes.add(
                    CustomSleepTheme(
                        id = themeJson.getString("id"),
                        name = themeJson.getString("name"),
                        primaryHex = themeJson.getString("primaryHex"),
                        backgroundTintHex = themeJson.getString("backgroundTintHex"),
                        accentHex = themeJson.getString("accentHex")
                    )
                )
            }
        }
        
        val customThemesJson = JSONArray()
        for (theme in customThemes) {
            val themeJson = JSONObject()
            themeJson.put("id", theme.id)
            themeJson.put("name", theme.name)
            themeJson.put("primaryHex", theme.primaryHex)
            themeJson.put("backgroundTintHex", theme.backgroundTintHex)
            themeJson.put("accentHex", theme.accentHex)
            customThemesJson.put(themeJson)
        }

        prefs.edit()
            .putString(KEY_CUSTOM_THEMES_JSON, customThemesJson.toString())
            .putString(KEY_SELECTED_THEME_ID, selectedThemeId)
            .commit()
    }

    companion object {
        private const val KEY_SELECTED_THEME_ID = "selected_theme_id"
        private const val KEY_CUSTOM_THEMES_JSON = "custom_sleep_themes_json"
    }
}
