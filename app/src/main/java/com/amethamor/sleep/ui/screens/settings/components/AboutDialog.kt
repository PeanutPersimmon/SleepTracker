package com.amethamor.sleep.ui.screens.settings.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.amethamor.sleep.ui.components.SleepDialogDefaults

@Composable
fun AboutDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .width(320.dp)
                    .wrapContentHeight(),
                shape = SleepDialogDefaults.CompactShape,
                color = SleepDialogDefaults.containerColor,
                shadowElevation = SleepDialogDefaults.ShadowElevation,
                tonalElevation = SleepDialogDefaults.TonalElevation
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SleepDialogDefaults.ContentPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "关于",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = SleepDialogDefaults.titleColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "一个用于记录入睡、起床、睡眠时长与睡眠状态的极简记录 App。",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 13.sp,
                        color = SleepDialogDefaults.bodyColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "当前版本：1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 13.sp,
                        color = SleepDialogDefaults.bodyColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://github.com/PeanutPersimmon")
                                )
                            )
                        }
                    ) {
                        Text(
                            text = "Developer:\nPeanutPersimmon",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 13.sp,
                            color = SleepDialogDefaults.confirmActionColor,
                            textAlign = TextAlign.Center
                        )
                    }

                    TextButton(
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://github.com/PeanutPersimmon/SleepTracker")
                                )
                            )
                        }
                    ) {
                        Text(
                            text = "Repository:\nSleepTracker",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 13.sp,
                            color = SleepDialogDefaults.confirmActionColor,
                            textAlign = TextAlign.Center
                        )
                    }

                    Text(
                        text = "License:\nGPL-3.0-only\n\nDevelopment assistance:\nOpenAI Codex",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 13.sp,
                        color = SleepDialogDefaults.bodyColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "睡眠记录保存在本地设备中，当前版本不包含云同步。",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 13.sp,
                        color = SleepDialogDefaults.bodyColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "关闭",
                            color = SleepDialogDefaults.confirmActionColor
                        )
                    }
                }
            }
        }
    }
}
