package com.amethamor.sleep.ui.components

import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun SleepScaffold(
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    val colors = SleepTheme.colors
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background
    ) {
        Scaffold(
            topBar = {
                Box(modifier = Modifier.statusBarsPadding()) {
                    topBar()
                }
            },
            bottomBar = {
                if (bottomBar != {}) {
                    bottomBar()
                }
            },
            containerColor = colors.background,
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    content()
                }
            }
        )
    }
}
