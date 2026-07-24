package com.amethamor.sleep.ui.report.annual

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.amethamor.sleep.ui.components.SleepDialogDefaults

@Composable
fun AnnualReportExportDialog() {
    Dialog(onDismissRequest = {}) {
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
                    text = "正在生成年度报告...",
                    style = MaterialTheme.typography.titleMedium,
                    color = SleepDialogDefaults.titleColor
                )
                CircularProgressIndicator()
                Text(
                    text = "正在绘制 1080x2400 睡眠海报",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SleepDialogDefaults.bodyColor
                )
            }
        }
    }
}
