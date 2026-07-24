package com.amethamor.sleep.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amethamor.sleep.ui.screens.settings.components.AboutIntroCard
import com.amethamor.sleep.ui.screens.settings.components.AboutSectionCard
import com.amethamor.sleep.ui.screens.settings.components.BulletLine
import com.amethamor.sleep.ui.screens.settings.components.InfoLine

@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 0.dp, bottom = 110.dp)
    ) {
        item {
            TextButton(onClick = onBack) {
                Text(text = "返回")
            }
        }

        item {
            AboutIntroCard()
        }

        item {
            AboutSectionCard(title = "版本信息") {
                InfoLine(label = "当前版本", value = "1.0.0")
                InfoLine(label = "数据范围", value = "本地睡眠记录")
                InfoLine(label = "当前状态", value = "开发中")
            }
        }

        item {
            AboutSectionCard(title = "设计原则") {
                BulletLine("只记录，不施压")
                BulletLine("不做目标督促")
                BulletLine("关注长期趋势")
            }
        }

        item {
            AboutSectionCard(title = "数据说明") {
                BulletLine("睡眠记录保存在本地设备中。")
                BulletLine("后续会支持数据导入导出。")
                BulletLine("当前版本不包含云同步。")
            }
        }
    }
}
