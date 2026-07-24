package com.amethamor.sleep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.data.SleepType
import com.amethamor.sleep.ui.SleepViewModel
import com.amethamor.sleep.ui.components.SleepActionButton
import com.amethamor.sleep.ui.components.SleepCard
import com.amethamor.sleep.ui.components.SleepDialogDefaults
import com.amethamor.sleep.ui.components.SleepRecordDialog
import com.amethamor.sleep.ui.components.SleepSecondaryButton
import com.amethamor.sleep.ui.components.noRippleClickable
import com.amethamor.sleep.ui.theme.SleepTheme
import com.amethamor.sleep.ui.theme.SleepTextSecondary
import com.amethamor.sleep.ui.theme.SleepWarning
import com.amethamor.sleep.util.DateTimeUtils
import kotlin.math.roundToInt

@Composable
fun HomeScreen(viewModel: SleepViewModel) {
    val latestRecord by viewModel.latestNightRecord.collectAsState()
    val recentRecords by viewModel.recentRecords.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var dialogRecord by remember { mutableStateOf<SleepRecord?>(null) }
    var isManualEntry by remember { mutableStateOf(false) }

    if (showDialog) {
        SleepRecordDialog(
            record = dialogRecord,
            isManualEntry = isManualEntry,
            onDismiss = { showDialog = false },
            onSave = { record ->
                if (isManualEntry || dialogRecord == null) {
                    viewModel.insertSleepRecord(record)
                } else {
                    viewModel.updateSleepRecord(record)
                }
                showDialog = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 0.dp, bottom = 110.dp)
    ) {
        item {
            TopActionButtons(
                onSleepClick = {
                    viewModel.sleepCheckIn()
                },
                onWakeClick = {
                    viewModel.wakeCheckIn()
                }
            )
        }

        item {
            val record = latestRecord
            CompactSleepOverviewCard(
                latestRecord = record,
                onClick = {
                    if (record != null && (record.bedTimeMillis == null || record.wakeTimeMillis == null)) {
                        isManualEntry = false
                        dialogRecord = record
                        showDialog = true
                    }
                }
            )
        }

        item {
            SleepStatusCard(latestRecord = latestRecord)
        }

        item {
            SleepSecondaryButton(
                text = "手动补录",
                onClick = {
                    isManualEntry = true
                    dialogRecord = null
                    showDialog = true
                }
            )
        }

        item {
            RecentRecordsCard(
                recentRecords = recentRecords,
                onRecordClick = { record ->
                    isManualEntry = false
                    dialogRecord = record
                    showDialog = true
                },
                onRecordDelete = { record ->
                    viewModel.deleteSleepRecord(record)
                }
            )
        }
    }
}

@Composable
fun TopActionButtons(
    onSleepClick: () -> Unit,
    onWakeClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SleepActionButton(
            text = "睡觉打卡",
            isSleep = true,
            onClick = onSleepClick,
            modifier = Modifier.weight(1f)
        )
        SleepActionButton(
            text = "起床打卡",
            isSleep = false,
            onClick = onWakeClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CompactSleepOverviewCard(
    latestRecord: SleepRecord?,
    onClick: () -> Unit
) {
    val timeRange = if (latestRecord == null) {
        "未记录"
    } else {
        val bedTime = DateTimeUtils.millisToTimeString(latestRecord.bedTimeMillis)
        val wakeTime = DateTimeUtils.millisToTimeString(latestRecord.wakeTimeMillis)
        "$bedTime - $wakeTime"
    }

    val duration = DateTimeUtils.formatDuration(latestRecord?.durationMinutes)
    val colors = SleepTheme.colors
    SleepCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "最近睡眠",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = timeRange,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = duration,
                style = MaterialTheme.typography.titleLarge,
                color = colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.End,
                modifier = Modifier.wrapContentWidth(Alignment.End)
            )
        }
    }
}

@Composable
fun SleepStatusCard(latestRecord: SleepRecord?) {
    val hasDream = latestRecord?.hasDream ?: false
    val wakeUpCount = latestRecord?.wakeUpCount ?: 0
    val hasNightmare = latestRecord?.hasNightmare ?: false
    val colors = SleepTheme.colors
    val wakeFeeling = latestRecord?.wakeFeeling ?: "一般"

    SleepCard {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MiniStatusChip(label = "做梦", value = if (hasDream) "是" else "否", color = colors.dream)
                MiniStatusChip(label = "夜醒", value = "${wakeUpCount}次", color = colors.awake)
                MiniStatusChip(label = "噩梦", value = if (hasNightmare) "是" else "否", color = colors.nightmare)
                MiniStatusChip(label = "感觉", value = wakeFeeling, color = colors.primary)
            }
        }
    }
}

@Composable
fun MiniStatusChip(label: String, value: String, color: Color) {
    val colors = SleepTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
fun RecentRecordsCard(
    recentRecords: List<SleepRecord>,
    onRecordClick: (SleepRecord) -> Unit,
    onRecordDelete: (SleepRecord) -> Unit
) {
    var pendingDeleteRecord by remember { mutableStateOf<SleepRecord?>(null) }
    val colors = SleepTheme.colors

    pendingDeleteRecord?.let { record ->
        DeleteRecordConfirmDialog(
            onDismissRequest = { pendingDeleteRecord = null },
            onConfirm = {
                pendingDeleteRecord = null
                onRecordDelete(record)
            }
        )
    }

    SleepCard {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "最近记录",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            if (recentRecords.isEmpty()) {
                Text(
                    text = "暂无记录",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SleepTextSecondary,
                    fontSize = 14.sp
                )
            } else {
                recentRecords.forEach { record ->
                    key(record.id) {
                        val bedTime = DateTimeUtils.millisToTimeString(record.bedTimeMillis)
                        val wakeTime = DateTimeUtils.millisToTimeString(record.wakeTimeMillis)
                        val duration = DateTimeUtils.formatDuration(record.durationMinutes)
                        val status = if (record.bedTimeMillis != null && record.wakeTimeMillis != null) "completed" else "incomplete"

                        SwipeableRecordItem(
                            date = record.recordDate,
                            time = "$bedTime - $wakeTime",
                            duration = duration,
                            status = status,
                            isNap = record.sleepType == SleepType.NAP,
                            onClick = { onRecordClick(record) },
                            onDelete = { pendingDeleteRecord = record }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteRecordConfirmDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier.width(280.dp),
            shape = SleepDialogDefaults.Shape,
            color = SleepDialogDefaults.containerColor,
            shadowElevation = SleepDialogDefaults.ShadowElevation,
            tonalElevation = SleepDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(SleepDialogDefaults.ContentPadding),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "删除记录",
                    color = SleepDialogDefaults.titleColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "确定删除这条睡眠记录吗？",
                    color = SleepDialogDefaults.bodyColor,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "取消",
                        color = SleepDialogDefaults.dismissActionColor,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .noRippleClickable(onClick = onDismissRequest)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "删除",
                        color = SleepDialogDefaults.destructiveActionColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .noRippleClickable(onClick = onConfirm)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

private enum class RecordSwipeState {
    Closed,
    Open
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun SwipeableRecordItem(
    date: String,
    time: String,
    duration: String,
    status: String,
    isNap: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = SleepTheme.colors
    val density = androidx.compose.ui.platform.LocalDensity.current
    val recordItemHeight = 68.dp
    val deleteButtonWidth = 64.dp
    val deleteButtonWidthPx = with(density) { deleteButtonWidth.toPx() }
    val itemShape = RoundedCornerShape(10.dp)
    val itemBackground = lerp(colors.surface, colors.surfaceVariant, 0.62f)
    val swipeState = remember {
        AnchoredDraggableState(
            initialValue = RecordSwipeState.Closed,
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { with(density) { 80.dp.toPx() } },
            animationSpec = androidx.compose.animation.core.spring()
        )
    }

    LaunchedEffect(deleteButtonWidthPx) {
        swipeState.updateAnchors(
            DraggableAnchors {
                RecordSwipeState.Closed at 0f
                RecordSwipeState.Open at -deleteButtonWidthPx
            }
        )
    }
    val swipeOffset = swipeState.offset.takeUnless { it.isNaN() } ?: 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = recordItemHeight)
            .clip(itemShape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(recordItemHeight),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(deleteButtonWidth)
                    .height(recordItemHeight)
                    .clip(itemShape)
                    .background(colors.danger)
                    .noRippleClickable(onClick = onDelete),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "删除",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset { androidx.compose.ui.unit.IntOffset(swipeOffset.roundToInt(), 0) }
                .anchoredDraggable(
                    state = swipeState,
                    orientation = Orientation.Horizontal
                )
                .clip(itemShape)
                .background(itemBackground)
                .noRippleClickable(onClick = onClick)
                .heightIn(min = recordItemHeight)
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    if (isNap) {
                        Text(
                            text = "\u5348\u7761",
                            color = colors.primaryDark,
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.selectedBackground.copy(alpha = 0.75f))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                }
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = duration,
                style = MaterialTheme.typography.bodyMedium,
                color = if (status == "completed") colors.primary else SleepWarning,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.End,
                modifier = Modifier.wrapContentWidth(Alignment.End)
            )
        }
    }
}
