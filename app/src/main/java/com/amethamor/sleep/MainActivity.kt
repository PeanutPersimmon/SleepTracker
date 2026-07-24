package com.amethamor.sleep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.amethamor.sleep.data.SleepDatabase
import com.amethamor.sleep.data.SleepRepository
import com.amethamor.sleep.ui.SleepApp
import com.amethamor.sleep.ui.SleepViewModel
import com.amethamor.sleep.ui.SleepViewModelFactory
import com.amethamor.sleep.ui.theme.CustomSleepTheme
import com.amethamor.sleep.ui.theme.SleepBaseTheme
import com.amethamor.sleep.ui.theme.SleepCheckAppTheme
import com.amethamor.sleep.ui.theme.ThemeSettingsRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        val database = SleepDatabase.getDatabase(applicationContext)
        val repository = SleepRepository(database.sleepRecordDao())
        val viewModelFactory = SleepViewModelFactory(repository)
        val themeSettingsRepository = ThemeSettingsRepository(applicationContext)
        
        setContent {
            val viewModel: SleepViewModel = viewModel(factory = viewModelFactory)
            var selectedTheme by remember {
                mutableStateOf(themeSettingsRepository.getSelectedTheme())
            }
            var allThemes by remember {
                mutableStateOf(themeSettingsRepository.getAllThemes())
            }

            SleepCheckAppTheme(baseTheme = selectedTheme) {
                SleepApp(
                    viewModel = viewModel,
                    allThemes = allThemes,
                    selectedTheme = selectedTheme,
                    onThemeSelected = { themeId ->
                        val nextTheme = themeSettingsRepository.getAllThemes().firstOrNull { it.id == themeId } 
                            ?: themeSettingsRepository.getSelectedTheme()
                        selectedTheme = nextTheme
                        themeSettingsRepository.saveSelectedThemeId(nextTheme.id)
                    },
                    onAddCustomTheme = { theme ->
                        themeSettingsRepository.addCustomTheme(theme)
                        allThemes = themeSettingsRepository.getAllThemes()
                        selectedTheme = themeSettingsRepository.getSelectedTheme()
                    },
                    onDeleteCustomTheme = { themeId ->
                        themeSettingsRepository.deleteCustomTheme(themeId)
                        allThemes = themeSettingsRepository.getAllThemes()
                        selectedTheme = themeSettingsRepository.getSelectedTheme()
                    },
                    database = database
                )
            }
        }
    }
}
