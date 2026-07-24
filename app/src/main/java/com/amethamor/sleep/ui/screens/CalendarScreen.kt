package com.amethamor.sleep.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amethamor.sleep.ui.SleepViewModel
import com.amethamor.sleep.ui.calendar.CalendarDateUtils
import com.amethamor.sleep.ui.calendar.CalendarMode
import com.amethamor.sleep.ui.calendar.components.CalendarSummaryCard
import com.amethamor.sleep.ui.calendar.components.CalendarToolbar
import com.amethamor.sleep.ui.calendar.components.SleepCalendarGrid
import java.time.YearMonth

@Composable
fun CalendarScreen(viewModel: SleepViewModel) {
    var calendarMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedMode by remember { mutableStateOf(CalendarMode.BedTime) }

    val allRecords by viewModel.allRecords.collectAsState()

    val days = remember(calendarMonth, allRecords) {
        CalendarDateUtils.buildMonthGrid(calendarMonth, allRecords)
    }

    val statistics = remember(calendarMonth, allRecords) {
        CalendarDateUtils.calculateMonthStatistics(calendarMonth, allRecords)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CalendarToolbar(
            monthText = CalendarDateUtils.formatMonth(calendarMonth),
            selectedMode = selectedMode,
            onPreviousMonth = { calendarMonth = calendarMonth.minusMonths(1) },
            onNextMonth = { calendarMonth = calendarMonth.plusMonths(1) },
            onModeSelected = { selectedMode = it }
        )
        CalendarSummaryCard(
            mode = selectedMode,
            statistics = statistics
        )
        SleepCalendarGrid(
            days = days,
            mode = selectedMode
        )
    }
}
