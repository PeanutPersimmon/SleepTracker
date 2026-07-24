package com.amethamor.sleep.ui.screens.settings

data class BackupUiState(
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val restartAfterImport: Boolean = false
)
