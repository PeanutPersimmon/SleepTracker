package com.amethamor.sleep.ui.screens.settings

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amethamor.sleep.backup.SleepBackupExporter
import com.amethamor.sleep.backup.SleepBackupImporter
import com.amethamor.sleep.backup.SleepBackupManifest
import com.amethamor.sleep.data.SleepDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BackupViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    fun exportBackup(
        context: Context,
        uri: Uri,
        database: SleepDatabase
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, isImporting = false)
            val result = SleepBackupExporter.exportBackup(context, uri, database)
            when (result) {
                is com.amethamor.sleep.backup.SleepBackupResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        successMessage = result.message,
                        restartAfterImport = false
                    )
                }
                is com.amethamor.sleep.backup.SleepBackupResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        errorMessage = result.message,
                        restartAfterImport = false
                    )
                }
                else -> {
                    _uiState.value = _uiState.value.copy(isExporting = false)
                }
            }
        }
    }

    fun validateBackup(
        context: Context,
        uri: Uri,
        onValidated: (Boolean, SleepBackupManifest?) -> Unit
    ) {
        viewModelScope.launch {
            val (isValid, manifest) = SleepBackupImporter.validateBackup(context, uri)
            onValidated(isValid, manifest)
        }
    }

    fun importBackup(
        context: Context,
        uri: Uri
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isImporting = true, isExporting = false)
            val result = SleepBackupImporter.importBackup(context, uri)
            when (result) {
                is com.amethamor.sleep.backup.SleepBackupResult.Success -> {
                    Log.d(TAG, "Import succeeded; restartAfterImport=true")
                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                        successMessage = result.message,
                        restartAfterImport = true
                    )
                }
                is com.amethamor.sleep.backup.SleepBackupResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                        errorMessage = result.message,
                        restartAfterImport = false
                    )
                }
                else -> {
                    _uiState.value = _uiState.value.copy(isImporting = false)
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            errorMessage = null,
            restartAfterImport = false
        )
    }

    companion object {
        private const val TAG = "SleepRestart"
    }
}
