package com.amethamor.sleep.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import com.amethamor.sleep.data.SleepDatabase
import com.amethamor.sleep.ui.components.SleepBottomBar
import com.amethamor.sleep.ui.components.SleepScaffold
import com.amethamor.sleep.ui.components.SleepTopBar
import com.amethamor.sleep.ui.components.SleepToast
import com.amethamor.sleep.ui.screens.CalendarScreen
import com.amethamor.sleep.ui.screens.HomeScreen
import com.amethamor.sleep.ui.screens.StatisticsScreen
import com.amethamor.sleep.ui.screens.settings.SettingsScreen
import com.amethamor.sleep.ui.theme.CustomSleepTheme
import com.amethamor.sleep.ui.theme.SleepBaseTheme
import kotlinx.coroutines.delay

enum class SleepScreen(val title: String, val index: Int) {
    Home("首页", 0),
    Statistics("统计", 1),
    Calendar("日历", 2),
    Settings("设置", 3)
}

@Composable
fun SleepApp(
    viewModel: SleepViewModel,
    allThemes: List<SleepBaseTheme>,
    selectedTheme: SleepBaseTheme,
    onThemeSelected: (String) -> Unit,
    onAddCustomTheme: (CustomSleepTheme) -> Unit,
    onDeleteCustomTheme: (String) -> Unit,
    database: SleepDatabase
) {
    var currentScreen by remember { mutableStateOf(SleepScreen.Home) }
    val message by viewModel.message.collectAsState()
    val allRecords by viewModel.allRecords.collectAsState()

    LaunchedEffect(message) {
        if (message != null) {
            delay(600)
            viewModel.clearMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SleepScaffold(
            topBar = { SleepTopBar(title = currentScreen.title) },
            bottomBar = {
                SleepBottomBar(
                    currentScreen = currentScreen,
                    onScreenChange = {
                        currentScreen = it
                    }
                )
            }
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    val duration = 250
                    if (targetState.index > initialState.index) {
                        slideInHorizontally(
                            animationSpec = tween(duration),
                            initialOffsetX = { it }
                        ) togetherWith slideOutHorizontally(
                            animationSpec = tween(duration),
                            targetOffsetX = { -it }
                        )
                    } else {
                        slideInHorizontally(
                            animationSpec = tween(duration),
                            initialOffsetX = { -it }
                        ) togetherWith slideOutHorizontally(
                            animationSpec = tween(duration),
                            targetOffsetX = { it }
                        )
                    }
                },
                label = "page_transition"
            ) { screen ->
                when (screen) {
                    SleepScreen.Home -> HomeScreen(viewModel = viewModel)
                    SleepScreen.Statistics -> StatisticsScreen(viewModel = viewModel)
                    SleepScreen.Calendar -> CalendarScreen(viewModel = viewModel)
                    SleepScreen.Settings -> SettingsScreen(
                        allRecords = allRecords,
                        allThemes = allThemes,
                        selectedTheme = selectedTheme,
                        onThemeSelected = onThemeSelected,
                        onAddCustomTheme = onAddCustomTheme,
                        onDeleteCustomTheme = onDeleteCustomTheme,
                        database = database
                    )
                }
            }
        }

        SleepToast(
            message = message,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 110.dp)
        )
    }
}
