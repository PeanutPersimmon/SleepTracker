package com.amethamor.sleep.ui.report.monthly

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.ui.components.SleepDialogDefaults
import com.amethamor.sleep.ui.theme.SleepBaseTheme
import com.amethamor.sleep.ui.theme.SleepTheme
import kotlinx.coroutines.launch
import java.time.YearMonth

@Composable
fun MonthlyReportExportDialog(
    allRecords: List<SleepRecord>,
    selectedTheme: SleepBaseTheme,
    onDismiss: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
    var isExporting by remember { mutableStateOf(false) }
    val reportData = remember(allRecords, selectedMonth) {
        MonthlyReportCalculator.calculate(allRecords, selectedMonth)
    }

    Dialog(
        onDismissRequest = { if (!isExporting) onDismiss() },
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
                        text = "月度报告",
                        color = SleepDialogDefaults.titleColor,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "选择月份并导出一张完整的睡眠月报长图。",
                        color = SleepDialogDefaults.bodyColor,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp
                    )
                    MonthlyReportMonthPicker(
                        selectedMonth = selectedMonth,
                        onMonthChange = { selectedMonth = it }
                    )
                    Text(
                        text = if (reportData.hasAnyRecord) {
                            "将导出 ${reportData.summary.nightRecordDays} 天夜间记录、${reportData.summary.napCount} 次午睡记录。"
                        } else {
                            "该月份暂无记录，将导出空状态报告。"
                        },
                        color = SleepTheme.colors.textTertiary,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            enabled = !isExporting,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Text("取消", color = SleepDialogDefaults.dismissActionColor)
                        }
                        Button(
                            onClick = {
                                if (isExporting) return@Button
                                isExporting = true
                                scope.launch {
                                    val result = exportMonthlyReportAsPng(context, reportData, selectedTheme)
                                    isExporting = false
                                    if (result.isSuccess) {
                                        Toast.makeText(context, "已保存月度报告图片", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    } else {
                                        Toast.makeText(context, "导出失败，请稍后重试", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            enabled = !isExporting,
                            modifier = Modifier.weight(1.2f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SleepTheme.colors.primary,
                                contentColor = SleepTheme.colors.onPrimary
                            ),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Text(if (isExporting) "导出中..." else "导出图片")
                        }
                    }
                }
            }
        }
    }
}
