package com.amethamor.sleep.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.amethamor.sleep.ui.theme.CustomSleepTheme
import com.amethamor.sleep.ui.theme.SleepTheme
import com.amethamor.sleep.ui.theme.colorFromHex
import com.amethamor.sleep.ui.theme.normalizeHexColor

@Composable
fun CustomThemeDialog(
    onDismiss: () -> Unit,
    onCreateTheme: (CustomSleepTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    var nameInput by remember { mutableStateOf("") }
    var primaryInput by remember { mutableStateOf("") }
    var backgroundInput by remember { mutableStateOf("") }
    var accentInput by remember { mutableStateOf("") }
    var hasTriedSubmit by remember { mutableStateOf(false) }

    val colors = SleepTheme.colors
    
    // 方式 A：每次重新计算，不使用 remember
    val normalizedPrimary = normalizeHexColor(primaryInput)
    val normalizedBackground = normalizeHexColor(backgroundInput)
    val normalizedAccent = normalizeHexColor(accentInput)
    
    val canCreate = nameInput.isNotBlank() &&
        normalizedPrimary != null &&
        normalizedBackground != null &&
        normalizedAccent != null
    
    val previewPrimary = normalizedPrimary?.let { colorFromHex(it) }
    val previewBackground = normalizedBackground?.let { colorFromHex(it) }
    val previewAccent = normalizedAccent?.let { colorFromHex(it) }
    val errorMessage = when {
        !hasTriedSubmit -> null
        nameInput.isBlank() -> "请输入主题名称"
        !canCreate -> "请输入正确的 6 位 HEX 颜色，例如 #8FA3C8"
        else -> null
    }

    fun createTheme() {
        hasTriedSubmit = true

        // 在函数内部重新计算，确保使用最新值
        val name = nameInput.trim()
        val currentNormalizedPrimary = normalizeHexColor(primaryInput)
        val currentNormalizedBackground = normalizeHexColor(backgroundInput)
        val currentNormalizedAccent = normalizeHexColor(accentInput)

        val currentPrimaryHex = currentNormalizedPrimary ?: return
        val currentBackgroundHex = currentNormalizedBackground ?: return
        val currentAccentHex = currentNormalizedAccent ?: return
        if (name.isBlank()) return

        val themeId = "custom_${System.currentTimeMillis()}"
        val customTheme = CustomSleepTheme(
            id = themeId,
            name = name,
            primaryHex = currentPrimaryHex,
            backgroundTintHex = currentBackgroundHex,
            accentHex = currentAccentHex
        )

        onCreateTheme(customTheme)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.fillMaxWidth(0.9f),
            shape = SleepDialogDefaults.Shape,
            color = SleepDialogDefaults.containerColor,
            shadowElevation = SleepDialogDefaults.ShadowElevation,
            tonalElevation = SleepDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SleepDialogDefaults.ContentPadding)
            ) {
                Text(
                    text = "创建自定义主题",
                    color = SleepDialogDefaults.titleColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(18.dp))

                DialogInputRow(
                    label = "名称",
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    placeholder = "如 湖蓝",
                    isError = hasTriedSubmit && nameInput.isBlank()
                )

                Spacer(modifier = Modifier.height(16.dp))

                DialogInputRow(
                    label = "主色",
                    value = primaryInput,
                    onValueChange = { primaryInput = it },
                    placeholder = "#5976BA",
                    isError = hasTriedSubmit && normalizedPrimary == null && primaryInput.isNotBlank()
                )

                Spacer(modifier = Modifier.height(16.dp))

                DialogInputRow(
                    label = "背景",
                    value = backgroundInput,
                    onValueChange = { backgroundInput = it },
                    placeholder = "#F5F1E9",
                    isError = hasTriedSubmit && normalizedBackground == null && backgroundInput.isNotBlank()
                )

                Spacer(modifier = Modifier.height(16.dp))

                DialogInputRow(
                    label = "辅助",
                    value = accentInput,
                    onValueChange = { accentInput = it },
                    placeholder = "#A3BBDB",
                    isError = hasTriedSubmit && normalizedAccent == null && accentInput.isNotBlank()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(78.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (previewPrimary != null && previewBackground != null && previewAccent != null) {
                        ThemeRingPreview(
                            backgroundTint = previewBackground,
                            accent = previewAccent,
                            primary = previewPrimary,
                            size = 60.dp
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = colors.danger,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "取消",
                        color = SleepDialogDefaults.dismissActionColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .noRippleClickable(onClick = onDismiss)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(18.dp))
                    Text(
                        text = "创建",
                        color = if (canCreate) SleepDialogDefaults.confirmActionColor else colors.textTertiary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .noRippleClickable(onClick = ::createTheme)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean = false
) {
    val colors = SleepTheme.colors

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = colors.textSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(58.dp)
        )
        DialogInputField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            isError = isError
        )
    }
}

@Composable
private fun DialogInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean = false
) {
    val colors = SleepTheme.colors

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            color = colors.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        cursorBrush = SolidColor(colors.primaryDark),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(
                        color = if (isError) colors.danger.copy(alpha = 0.06f)
                        else colors.surfaceVariant.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = colors.textTertiary,
                        fontSize = 13.sp
                    )
                }
                innerTextField()
            }
        }
    )
}
