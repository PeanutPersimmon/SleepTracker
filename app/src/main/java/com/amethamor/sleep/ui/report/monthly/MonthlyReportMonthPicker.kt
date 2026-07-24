package com.amethamor.sleep.ui.report.monthly

import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.amethamor.sleep.ui.components.SleepDialogDefaults
import com.amethamor.sleep.ui.theme.SleepTheme
import java.time.YearMonth

@Composable
fun MonthlyReportMonthPicker(
    selectedMonth: YearMonth,
    onMonthChange: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentMonth = remember { YearMonth.now() }
    var showYearMonthPicker by remember { mutableStateOf(false) }
    val canGoNext = selectedMonth < currentMonth

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MonthArrowButton(left = true) { onMonthChange(selectedMonth.minusMonths(1)) }
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable { showYearMonthPicker = true },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "%04d年%02d月".format(selectedMonth.year, selectedMonth.monthValue),
                color = SleepTheme.colors.primaryDark,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        MonthArrowButton(left = false, enabled = canGoNext) {
            val next = selectedMonth.plusMonths(1)
            if (next <= currentMonth) onMonthChange(next)
        }
    }

    if (showYearMonthPicker) {
        YearMonthPickerDialog(
            selectedMonth = selectedMonth,
            currentMonth = currentMonth,
            onDismiss = { showYearMonthPicker = false },
            onMonthSelected = {
                onMonthChange(it)
                showYearMonthPicker = false
            }
        )
    }
}

@Composable
private fun MonthArrowButton(
    left: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, enabled = enabled, modifier = Modifier.size(40.dp)) {
        Icon(
            imageVector = if (left) {
                Icons.AutoMirrored.Rounded.KeyboardArrowLeft
            } else {
                Icons.AutoMirrored.Rounded.KeyboardArrowRight
            },
            contentDescription = null,
            tint = if (enabled) SleepTheme.colors.primaryDark else SleepTheme.colors.textTertiary
        )
    }
}

@Composable
private fun YearMonthPickerDialog(
    selectedMonth: YearMonth,
    currentMonth: YearMonth,
    onDismiss: () -> Unit,
    onMonthSelected: (YearMonth) -> Unit
) {
    var year by remember(selectedMonth) { mutableIntStateOf(selectedMonth.year) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .width(320.dp)
                    .wrapContentHeight(),
                shape = SleepDialogDefaults.Shape,
                color = SleepDialogDefaults.containerColor,
                shadowElevation = SleepDialogDefaults.ShadowElevation,
                tonalElevation = SleepDialogDefaults.TonalElevation
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SleepDialogDefaults.ContentPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MonthArrowButton(left = true) { year -= 1 }
                        Text(
                            "$year 年",
                            color = SleepDialogDefaults.titleColor,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                        MonthArrowButton(left = false, enabled = year < currentMonth.year) { year += 1 }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..12).chunked(3).forEach { rowMonths ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowMonths.forEach { month ->
                                    val candidate = YearMonth.of(year, month)
                                    val isSelected = candidate == selectedMonth
                                    val isEnabled = candidate <= currentMonth

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isSelected) {
                                                    SleepTheme.colors.primary
                                                } else if (!isEnabled) {
                                                    SleepTheme.colors.surfaceVariant.copy(alpha = 0.5f)
                                                } else {
                                                    SleepTheme.colors.selectedBackground
                                                }
                                            )
                                            .clickable(enabled = isEnabled) {
                                                if (isEnabled) {
                                                    onMonthSelected(candidate)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${month}月",
                                            color = when {
                                                isSelected -> SleepTheme.colors.onPrimary
                                                !isEnabled -> SleepTheme.colors.textTertiary
                                                else -> SleepTheme.colors.textPrimary
                                            },
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Text("取消", color = SleepDialogDefaults.dismissActionColor)
                        }
                    }
                }
            }
        }
    }
}
