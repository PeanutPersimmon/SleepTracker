package com.amethamor.sleep.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amethamor.sleep.ui.theme.SleepBaseTheme
import com.amethamor.sleep.ui.theme.SleepColorScheme
import com.amethamor.sleep.ui.theme.SleepTheme
import com.amethamor.sleep.ui.theme.deriveSleepColorScheme

@Composable
fun ThemePreviewItem(
    name: String,
    backgroundTint: Color,
    accent: Color,
    primary: Color,
    primaryDark: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    showDelete: Boolean = false,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = SleepTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.noRippleClickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    if (isSelected) Color(0xFFEDEFF2) else Color.Transparent,
                    shape = RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            ThemeRingPreview(
                backgroundTint = backgroundTint,
                accent = accent,
                primary = primary,
                size = 52.dp
            )
            if (showDelete && onDelete != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 10.dp, y = (-10).dp)
                        .size(20.dp)
                        .noRippleClickable(onClick = { onDelete() }),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "x",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textTertiary
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = name,
            color = if (isSelected) primaryDark else colors.textTertiary,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun ThemePreviewItemFromTheme(
    theme: SleepBaseTheme,
    colorScheme: SleepColorScheme,
    isSelected: Boolean,
    onClick: () -> Unit,
    showDelete: Boolean = false,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ThemePreviewItem(
        name = theme.name,
        backgroundTint = theme.backgroundTint,
        accent = theme.accent,
        primary = theme.primary,
        primaryDark = colorScheme.primaryDark,
        isSelected = isSelected,
        onClick = onClick,
        showDelete = showDelete,
        onDelete = onDelete,
        modifier = modifier
    )
}

@Composable
fun AddCustomThemeItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SleepTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.noRippleClickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(
                    width = 1.dp,
                    color = colors.outline,
                    shape = RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add custom theme",
                tint = colors.textTertiary,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Custom",
            color = colors.textTertiary,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun ThemePreviewRow(
    allThemes: List<SleepBaseTheme>,
    selectedThemeId: String,
    onThemeSelected: (String) -> Unit,
    onAddCustomTheme: () -> Unit,
    onDeleteCustomTheme: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.padding(top = 10.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(start = 8.dp, end = 20.dp)
    ) {
        items(allThemes) { theme ->
            ThemePreviewItemFromTheme(
                theme = theme,
                colorScheme = deriveSleepColorScheme(theme),
                isSelected = selectedThemeId == theme.id,
                onClick = { onThemeSelected(theme.id) },
                showDelete = theme.id.startsWith("custom_"),
                onDelete = { onDeleteCustomTheme(theme.id) }
            )
        }
        item {
            AddCustomThemeItem(onClick = onAddCustomTheme)
        }
    }
}
