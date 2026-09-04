package com.amethamor.sleep.ui.screens.settings

import android.widget.Toast
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amethamor.sleep.backup.BackupFileNameUtils
import com.amethamor.sleep.backup.SleepBackupConstants
import com.amethamor.sleep.backup.SleepBackupManifest
import com.amethamor.sleep.data.SleepDatabase
import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.ui.components.CustomThemeDialog
import com.amethamor.sleep.ui.components.SleepDialogDefaults
import com.amethamor.sleep.ui.components.ThemePreviewRow
import com.amethamor.sleep.ui.report.annual.AnnualReportExportDialog
import com.amethamor.sleep.ui.report.annual.AnnualReportExportManager
import com.amethamor.sleep.ui.report.annual.AnnualReportYearPickerDialog
import com.amethamor.sleep.ui.report.monthly.MonthlyReportExportDialog
import com.amethamor.sleep.ui.screens.settings.components.AboutDialog
import com.amethamor.sleep.ui.screens.settings.components.SettingsItemRow
import com.amethamor.sleep.ui.screens.settings.components.SettingsSectionCard
import com.amethamor.sleep.ui.theme.CustomSleepTheme
import com.amethamor.sleep.ui.theme.SleepBaseTheme
import com.amethamor.sleep.util.AppRestartUtils
import com.amethamor.sleep.util.DateTimeUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.time.Year
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    allRecords: List<SleepRecord>,
    allThemes: List<SleepBaseTheme>,
    selectedTheme: SleepBaseTheme,
    onThemeSelected: (String) -> Unit,
    onAddCustomTheme: (CustomSleepTheme) -> Unit,
    onDeleteCustomTheme: (String) -> Unit,
    database: SleepDatabase
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val backupViewModel: BackupViewModel = viewModel()
    val uiState by backupViewModel.uiState.collectAsState()

    var showAboutDialog by remember { mutableStateOf(false) }
    var showMonthlyReportDialog by remember { mutableStateOf(false) }
    var showAnnualReportYearPickerDialog by remember { mutableStateOf(false) }
    var showCustomThemeDialog by remember { mutableStateOf(false) }
    var isExportingAnnualReport by remember { mutableStateOf(false) }
    val currentYear = Year.now().value
    var selectedAnnualReportYear by remember { mutableStateOf(currentYear) }
    val earliestAnnualReportYear = remember(allRecords, currentYear) {
        allRecords
            .mapNotNull { DateTimeUtils.parseRecordDateOrNull(it.recordDate)?.year }
            .minOrNull()
            ?.coerceAtMost(currentYear)
            ?: currentYear
    }
    var pendingImportUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var pendingImportManifest by remember { mutableStateOf<SleepBackupManifest?>(null) }
    var showFirstConfirmDialog by remember { mutableStateOf(false) }
    var showSecondConfirmDialog by remember { mutableStateOf(false) }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            showToast(it)
            if (uiState.restartAfterImport) {
                Log.d("SleepRestart", "SettingsScreen about to call restartApp")
                Log.d("SleepRestart", "Calling AppRestartUtils.restartApp after successful import")
                delay(700)
                Log.d("SleepRestart", "SettingsScreen invoking restartApp now")
                AppRestartUtils.restartApp(context)
            } else {
                backupViewModel.clearMessages()
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            showToast(it)
            backupViewModel.clearMessages()
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(SleepBackupConstants.MIME_TYPE)
    ) { uri ->
        uri?.let {
            backupViewModel.exportBackup(context, it, database)
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { selectedUri ->
            backupViewModel.validateBackup(context, selectedUri) { isValid, manifest ->
                if (isValid && manifest != null) {
                    pendingImportUri = selectedUri
                    pendingImportManifest = manifest
                    showFirstConfirmDialog = true
                } else {
                    showToast("这不是有效的 Sleep 备份文件")
                }
            }
        }
    }

    fun exportAnnualReport(year: Int) {
        if (isExportingAnnualReport) return
        if (allRecords.none { DateTimeUtils.parseRecordDateOrNull(it.recordDate)?.year == year }) {
            showToast("${year} 年暂无睡眠记录")
            return
        }
        isExportingAnnualReport = true
        scope.launch {
            val result = AnnualReportExportManager.exportAnnualReportImage(context, allRecords, year)
            isExportingAnnualReport = false
            result.onSuccess {
                showToast("已保存到相册")
            }.onFailure {
                showToast("年度报告图片保存失败")
            }
        }
    }

    fun startExport() {
        if (uiState.isExporting || uiState.isImporting) return
        val fileName = BackupFileNameUtils.generateDefaultFileName()
        exportLauncher.launch(fileName)
    }

    fun startImport() {
        if (uiState.isExporting || uiState.isImporting) return
        importLauncher.launch(arrayOf("*/*"))
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }

    if (showAnnualReportYearPickerDialog) {
        AnnualReportYearPickerDialog(
            selectedYear = selectedAnnualReportYear,
            earliestYear = earliestAnnualReportYear,
            currentYear = currentYear,
            onYearChange = { selectedAnnualReportYear = it },
            onDismiss = { showAnnualReportYearPickerDialog = false },
            onExport = {
                showAnnualReportYearPickerDialog = false
                exportAnnualReport(selectedAnnualReportYear)
            }
        )
    }

    if (showMonthlyReportDialog) {
        MonthlyReportExportDialog(
            allRecords = allRecords,
            selectedTheme = selectedTheme,
            onDismiss = { showMonthlyReportDialog = false }
        )
    }

    if (showCustomThemeDialog) {
        CustomThemeDialog(
            onDismiss = { showCustomThemeDialog = false },
            onCreateTheme = { theme ->
                onAddCustomTheme(theme)
                showCustomThemeDialog = false
            }
        )
    }

    if (isExportingAnnualReport) {
        AnnualReportExportDialog()
    }

    if (uiState.isExporting || uiState.isImporting) {
        ImportProgressDialog(
            title = if (uiState.isExporting) "正在导出..." else "正在导入..."
        )
    }

    pendingImportManifest?.takeIf { showFirstConfirmDialog }?.let { manifest ->
        ImportConfirmDialog(
            title = "导入备份",
            message = "将导入以下备份：\n\n" +
                "备份时间：${formatDate(manifest.createdAt)}\n" +
                "睡眠记录：${manifest.recordCount} 条\n" +
                "夜间睡眠：${manifest.nightRecordCount} 条\n" +
                "午睡记录：${manifest.napRecordCount} 条\n" +
                "自定义主题：${manifest.customThemeCount} 个\n\n" +
                "导入后，当前所有睡眠记录、午睡记录、主题色和设置都会被覆盖。",
            confirmText = "继续导入",
            onConfirm = {
                showFirstConfirmDialog = false
                showSecondConfirmDialog = true
            },
            onDismiss = {
                showFirstConfirmDialog = false
                pendingImportUri = null
                pendingImportManifest = null
            }
        )
    }


    pendingImportUri?.takeIf { showSecondConfirmDialog }?.let { importUri ->
        ImportConfirmDialog(
            title = "确认导入？",
            message = "当前数据和自定义主题色都会被覆盖。",
            confirmText = "确认导入",
            onConfirm = {
                showSecondConfirmDialog = false
                pendingImportUri = null
                pendingImportManifest = null
                backupViewModel.importBackup(context, importUri)
            },
            onDismiss = {
                showSecondConfirmDialog = false
                pendingImportUri = null
                pendingImportManifest = null
            }
        )
    }


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 0.dp, bottom = 110.dp)
    ) {
        item {
            SettingsSectionCard(title = "主题色") {
                ThemePreviewRow(
                    allThemes = allThemes,
                    selectedThemeId = selectedTheme.id,
                    onThemeSelected = onThemeSelected,
                    onAddCustomTheme = { showCustomThemeDialog = true },
                    onDeleteCustomTheme = onDeleteCustomTheme
                )
            }
        }

        item {
            SettingsSectionCard(title = "数据管理") {
                SettingsItemRow(
                    title = "导出数据",
                    subtitle = "",
                    onClick = { startExport() },
                    showDivider = false
                )
                SettingsItemRow(
                    title = "导入数据",
                    subtitle = "",
                    trailingText = "谨慎",
                    onClick = { startImport() },
                    showDivider = false
                )
            }
        }

        item {
            SettingsSectionCard(title = "睡眠报告") {
                SettingsItemRow(
                    title = "月度报告",
                    subtitle = "",
                    onClick = { showMonthlyReportDialog = true },
                    showDivider = false
                )
                SettingsItemRow(
                    title = "年度报告",
                    subtitle = "",
                    onClick = {
                        selectedAnnualReportYear = currentYear
                        showAnnualReportYearPickerDialog = true
                    },
                    showDivider = false
                )
            }
        }

        item {
            SettingsSectionCard(title = "") {
                SettingsItemRow(
                    title = "关于",
                    subtitle = "",
                    onClick = { showAboutDialog = true },
                    showDivider = false,
                    compact = true,
                    showArrow = false
                )
            }
        }
    }
}

@Composable
private fun ImportProgressDialog(title: String) {
    Dialog(onDismissRequest = { }) {
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
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = SleepDialogDefaults.titleColor
                )
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun ImportConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = title,
                    color = SleepDialogDefaults.titleColor
                )
                Text(
                    text = message,
                    color = SleepDialogDefaults.bodyColor
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = SleepDialogDefaults.dismissActionColor
                        ),
                        onClick = onDismiss
                    ) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = SleepDialogDefaults.confirmActionColor
                        ),
                        onClick = onConfirm
                    ) {
                        Text(confirmText)
                    }
                }
            }
        }
    }
}

