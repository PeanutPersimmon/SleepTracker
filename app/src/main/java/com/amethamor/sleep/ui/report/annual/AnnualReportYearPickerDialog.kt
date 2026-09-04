package com.amethamor.sleep.ui.report.annual

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.amethamor.sleep.ui.components.SleepDialogDefaults
import com.amethamor.sleep.ui.theme.SleepTheme

@Composable
fun AnnualReportYearPickerDialog(
    selectedYear: Int,
    earliestYear: Int,
    currentYear: Int,
    onYearChange: (Int) -> Unit,
    onDismiss: () -> Unit,
    onExport: () -> Unit
) {
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
                    Text(
                        text = "年度报告",
                        modifier = Modifier.fillMaxWidth(),
                        color = SleepDialogDefaults.titleColor,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        YearArrowButton(
                            previous = true,
                            enabled = selectedYear > earliestYear,
                            onClick = { onYearChange(selectedYear - 1) }
                        )
                        Text(
                            text = selectedYear.toString(),
                            color = SleepDialogDefaults.titleColor,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        YearArrowButton(
                            previous = false,
                            enabled = selectedYear < currentYear,
                            onClick = { onYearChange(selectedYear + 1) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = onDismiss,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("取消", color = SleepDialogDefaults.dismissActionColor)
                        }
                        Button(
                            onClick = onExport,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SleepTheme.colors.primary,
                                contentColor = SleepTheme.colors.onPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            Text("导出")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun YearArrowButton(
    previous: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            imageVector = if (previous) {
                Icons.AutoMirrored.Rounded.KeyboardArrowLeft
            } else {
                Icons.AutoMirrored.Rounded.KeyboardArrowRight
            },
            contentDescription = if (previous) "上一年" else "下一年",
            tint = if (enabled) SleepTheme.colors.primaryDark else SleepTheme.colors.textTertiary
        )
    }
}
