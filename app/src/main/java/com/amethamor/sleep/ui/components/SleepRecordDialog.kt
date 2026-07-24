package com.amethamor.sleep.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.data.SleepType
import com.amethamor.sleep.ui.theme.*
import com.amethamor.sleep.util.DateTimeUtils
import com.amethamor.sleep.util.SleepCalculateUtils
import java.util.Calendar

private const val ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L
private val DialogFieldLabelFontSize = 14.sp
private val DialogFieldLabelLineHeight = 18.sp

@Composable
fun SleepRecordDialog(
    record: SleepRecord?,
    isManualEntry: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (SleepRecord) -> Unit
) {
    val nowMillis = DateTimeUtils.currentMillis()

    var bedTimeMillis by remember { mutableStateOf(record?.bedTimeMillis) }
    var wakeTimeMillis by remember { mutableStateOf(record?.wakeTimeMillis) }
    var bedDateMillis by remember { mutableStateOf(dateOnlyMillis(record?.bedTimeMillis ?: nowMillis)) }
    var wakeDateMillis by remember { mutableStateOf(dateOnlyMillis(record?.wakeTimeMillis ?: nowMillis)) }
    var hasDream by remember { mutableStateOf(record?.hasDream ?: false) }
    var wakeUpCount by remember { mutableStateOf(record?.wakeUpCount ?: 0) }
    var hasNightmare by remember { mutableStateOf(record?.hasNightmare ?: false) }
    var wakeFeeling by remember { mutableStateOf(record?.wakeFeeling ?: "\u4e00\u822c") }
    var isNapRecord by remember { mutableStateOf(record?.sleepType == SleepType.NAP) }
    var sleepTypeManuallySet by remember { mutableStateOf(record?.sleepTypeManuallySet ?: false) }
    var datePickerTarget by remember { mutableStateOf<Boolean?>(null) }
    var timePickerTarget by remember { mutableStateOf<Boolean?>(null) }
    var saveError by remember { mutableStateOf<String?>(null) }
    val SleepSurface = SleepDialogDefaults.containerColor
    val SleepTextPrimary = SleepDialogDefaults.titleColor

    val wakeFeelingOptions = listOf("\u6e05\u9192", "\u8fd8\u884c", "\u4e00\u822c", "\u56f0\u5026", "\u5f88\u7d2f")

    fun showDatePicker(isBedTime: Boolean) {
        datePickerTarget = isBedTime
    }

    fun showTimePicker(isBedTime: Boolean) {
        timePickerTarget = isBedTime
    }

    fun refreshAutoSleepType() {
        if (sleepTypeManuallySet) return
        val normalizedWakeTimeMillis = normalizeWakeTimeMillis(bedTimeMillis, wakeTimeMillis)
        val durationMinutes = SleepCalculateUtils.calculateDurationMinutes(bedTimeMillis, normalizedWakeTimeMillis)
        isNapRecord = SleepCalculateUtils.inferSleepType(
            bedTimeMillis = bedTimeMillis,
            wakeTimeMillis = normalizedWakeTimeMillis,
            durationMinutes = durationMinutes
        ) == SleepType.NAP
    }

    fun handleSave() {
        if (bedTimeMillis == null && wakeTimeMillis == null) {
            saveError = "请至少填写入睡时间或起床时间"
            return
        }

        val normalizedWakeTimeMillis = normalizeWakeTimeMillis(bedTimeMillis, wakeTimeMillis)
        if (!isTimeRangeValid(bedTimeMillis, normalizedWakeTimeMillis)) {
            saveError = "时间不合理，请确认入睡时间不晚于起床时间"
            return
        }

        val now = DateTimeUtils.currentMillis()
        val createdAt = record?.createdAt ?: now
        val updatedAt = now
        val durationMinutes = SleepCalculateUtils.calculateDurationMinutes(bedTimeMillis, normalizedWakeTimeMillis)
        val recordDate = SleepCalculateUtils.calculateRecordDate(bedTimeMillis, normalizedWakeTimeMillis)
        val manuallySelectedSleepType = if (isNapRecord) SleepType.NAP else SleepType.NIGHT
        val sleepType = SleepCalculateUtils.resolveSleepType(
            bedTimeMillis = bedTimeMillis,
            wakeTimeMillis = normalizedWakeTimeMillis,
            durationMinutes = durationMinutes,
            selectedSleepType = manuallySelectedSleepType,
            sleepTypeManuallySet = sleepTypeManuallySet
        )

        val newRecord = SleepRecord(
            id = record?.id ?: 0,
            recordDate = recordDate,
            bedTimeMillis = bedTimeMillis,
            wakeTimeMillis = normalizedWakeTimeMillis,
            durationMinutes = durationMinutes,
            hasDream = hasDream,
            wakeUpCount = wakeUpCount,
            hasNightmare = hasNightmare,
            wakeFeeling = wakeFeeling,
            sleepType = sleepType,
            sleepTypeManuallySet = sleepTypeManuallySet,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
        onSave(newRecord)
        onDismiss()
    }

    datePickerTarget?.let { isBedTime ->
        SleepDatePickerDialog(
            initialDateMillis = if (isBedTime) bedDateMillis else wakeDateMillis,
            onDismiss = { datePickerTarget = null },
            onConfirm = { selectedDate ->
                if (isBedTime) {
                    bedDateMillis = selectedDate
                    bedTimeMillis = replaceDate(bedTimeMillis, selectedDate)
                } else {
                    wakeDateMillis = selectedDate
                    wakeTimeMillis = replaceDate(wakeTimeMillis, selectedDate)
                }
                refreshAutoSleepType()
                datePickerTarget = null
            }
        )
    }

    timePickerTarget?.let { isBedTime ->
        val calendar = Calendar.getInstance().apply {
            timeInMillis = if (isBedTime) {
                bedTimeMillis ?: nowMillis
            } else {
                wakeTimeMillis ?: nowMillis
            }
        }

        SleepTimePickerDialog(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            onDismiss = { timePickerTarget = null },
            onConfirm = { hour, minute ->
                val selectedDate = if (isBedTime) bedDateMillis else wakeDateMillis
                val selectedMillis = combineDateAndTime(selectedDate, hour, minute)
                if (isBedTime) bedTimeMillis = selectedMillis
                else wakeTimeMillis = selectedMillis
                refreshAutoSleepType()
                timePickerTarget = null
            }
        )
    }

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .width(320.dp)
                    .wrapContentHeight()
                    .heightIn(max = 540.dp),
                shape = SleepDialogDefaults.CompactShape,
                color = SleepSurface,
                shadowElevation = SleepDialogDefaults.ShadowElevation,
                tonalElevation = SleepDialogDefaults.TonalElevation
            ) {
                Column(
                    modifier = Modifier
                    .fillMaxWidth()
                    .padding(SleepDialogDefaults.ContentPadding)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                Text(
                    text = if (isManualEntry) "手动补录" else "编辑睡眠记录",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleepTextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DialogTimeField(
                        label = "入睡日期",
                        value = DateTimeUtils.millisToDateString(bedDateMillis),
                        onClick = { showDatePicker(isBedTime = true) }
                    )

                    DialogTimeField(
                        label = "入睡时间",
                        value = DateTimeUtils.millisToTimeString(bedTimeMillis),
                        onClick = { showTimePicker(isBedTime = true) }
                    )

                    DialogTimeField(
                        label = "起床日期",
                        value = DateTimeUtils.millisToDateString(wakeDateMillis),
                        onClick = { showDatePicker(isBedTime = false) }
                    )

                    DialogTimeField(
                        label = "起床时间",
                        value = DateTimeUtils.millisToTimeString(wakeTimeMillis),
                        onClick = { showTimePicker(isBedTime = false) }
                    )

                    DialogSwitchField(
                        label = "做梦",
                        checked = hasDream,
                        onCheckedChange = { newValue -> hasDream = newValue }
                    )
                    DialogSwitchField(
                        label = "\u5348\u7761\u8bb0\u5f55",
                        checked = isNapRecord,
                        onCheckedChange = { newValue ->
                            isNapRecord = newValue
                            sleepTypeManuallySet = true
                        }
                    )
                    DialogCounterField(
                        label = "\u591c\u9192\u6b21\u6570",
                        value = wakeUpCount,
                        onValueChange = { newValue -> wakeUpCount = newValue }
                    )
                    DialogSwitchField(
                        label = "噩梦",
                        checked = hasNightmare,
                        onCheckedChange = { newValue -> hasNightmare = newValue }
                    )
                    DialogDropdownField(
                        label = "\u9192\u6765\u72b6\u6001",
                        options = wakeFeelingOptions,
                        selectedOption = wakeFeeling,
                        onOptionSelected = { newValue -> wakeFeeling = newValue }
                    )
                    saveError?.let { error ->
                        Text(
                            text = error,
                            color = SleepDanger,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "取消",
                        fontSize = 14.sp,
                        color = SleepDialogDefaults.dismissActionColor,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.noRippleClickable { onDismiss() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "保存",
                        fontSize = 14.sp,
                        color = SleepDialogDefaults.confirmActionColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.noRippleClickable { handleSave() }
                    )
                }
                }
            }
        }
    }
}

@Composable
private fun DialogTimeField(label: String, value: String, onClick: () -> Unit) {
    val SleepPrimaryLight = SleepTheme.colors.selectedBackground
    val SleepTextPrimary = SleepTheme.colors.textPrimary
    val SleepTextSecondary = SleepTheme.colors.textSecondary
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = DialogFieldLabelFontSize,
            lineHeight = DialogFieldLabelLineHeight,
            fontWeight = FontWeight.Normal,
            color = SleepTextSecondary
        )
        Box(
            modifier = Modifier
                .height(28.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SleepPrimaryLight.copy(alpha = 0.2f))
                .noRippleClickable(onClick = onClick)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = SleepTextPrimary
            )
        }
    }
}

@Composable
private fun DialogSwitchField(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val colors = SleepTheme.colors
    val SleepTextSecondary = colors.textSecondary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .noRippleClickable { onCheckedChange(!checked) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = DialogFieldLabelFontSize,
            lineHeight = DialogFieldLabelLineHeight,
            fontWeight = FontWeight.Normal,
            color = SleepTextSecondary
        )

        Switch(
            checked = checked,
            onCheckedChange = null,
            modifier = Modifier.scale(0.8f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.onPrimary,
                checkedTrackColor = colors.primary,
                uncheckedThumbColor = colors.surface,
                uncheckedTrackColor = colors.surfaceVariant,
                checkedBorderColor = colors.outline,
                uncheckedBorderColor = colors.divider
            )
        )
    }
}

@Composable
private fun DialogCounterField(label: String, value: Int, onValueChange: (Int) -> Unit) {
    val SleepDivider = SleepTheme.colors.outline
    val SleepPrimary = SleepTheme.colors.primary
    val SleepSurface = SleepDialogDefaults.containerColor
    val SleepTextSecondary = SleepTheme.colors.textSecondary
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = DialogFieldLabelFontSize,
            lineHeight = DialogFieldLabelLineHeight,
            fontWeight = FontWeight.Normal,
            color = SleepTextSecondary
        )

        Surface(
            modifier = Modifier,
            shape = RoundedCornerShape(12.dp),
            color = SleepSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, SleepDivider),
            shadowElevation = 0.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "-",
                    fontSize = 14.sp,
                    color = SleepPrimary,
                    modifier = Modifier
                        .width(36.dp)
                        .height(34.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .noRippleClickable { onValueChange((value - 1).coerceAtLeast(0)) }
                        .wrapContentHeight(Alignment.CenterVertically)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )

                Text(
                    text = value.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .width(44.dp)
                        .height(34.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )

                Text(
                    text = "+",
                    fontSize = 14.sp,
                    color = SleepPrimary,
                    modifier = Modifier
                        .width(36.dp)
                        .height(34.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .noRippleClickable { onValueChange(value + 1) }
                        .wrapContentHeight(Alignment.CenterVertically)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun DialogDropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val SleepPrimary = SleepTheme.colors.primary
    val SleepPrimaryLight = SleepTheme.colors.selectedBackground
    val SleepSurface = SleepDialogDefaults.containerColor
    val SleepTextSecondary = SleepTheme.colors.textSecondary

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = DialogFieldLabelFontSize,
            lineHeight = DialogFieldLabelLineHeight,
            fontWeight = FontWeight.Normal,
            color = SleepTextSecondary
        )

        Box {
            Box(
                modifier = Modifier
                    .height(28.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SleepPrimaryLight.copy(alpha = 0.2f))
                    .noRippleClickable { expanded = true }
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(selectedOption, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            if (expanded) {
                Popup(
                    alignment = Alignment.TopEnd,
                    onDismissRequest = { expanded = false }
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 3.dp,
                        color = SleepSurface
                    ) {
                        Column(
                            modifier = Modifier
                                .width(80.dp)
                                .heightIn(max = 280.dp)
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 4.dp)
                        ) {
                            options.forEach { option ->
                                val isSelected = option == selectedOption
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(34.dp)
                                        .noRippleClickable {
                                            onOptionSelected(option)
                                            expanded = false
                                        }
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = option,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                        color = if (isSelected) SleepPrimary else SleepTextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun normalizeWakeTimeMillis(bedTimeMillis: Long?, wakeTimeMillis: Long?): Long? {
    if (bedTimeMillis == null || wakeTimeMillis == null) return wakeTimeMillis
    return if (wakeTimeMillis < bedTimeMillis) wakeTimeMillis + ONE_DAY_MILLIS else wakeTimeMillis
}

private fun isTimeRangeValid(bedTimeMillis: Long?, wakeTimeMillis: Long?): Boolean {
    if (bedTimeMillis == null || wakeTimeMillis == null) return true
    val durationMinutes = SleepCalculateUtils.calculateDurationMinutes(bedTimeMillis, wakeTimeMillis)
    return wakeTimeMillis >= bedTimeMillis && durationMinutes != null && durationMinutes in 0..(24 * 60)
}

@Composable
private fun SleepDatePickerDialog(
    initialDateMillis: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val SleepPrimary = SleepTheme.colors.primary
    val SleepPrimaryLight = SleepTheme.colors.selectedBackground
    val SleepSurface = SleepDialogDefaults.containerColor
    val SleepTextPrimary = SleepTheme.colors.textPrimary
    val SleepTextSecondary = SleepTheme.colors.textSecondary
    var displayedMonthMillis by remember { mutableStateOf(monthStartMillis(initialDateMillis)) }
    var selectedDateMillis by remember { mutableStateOf(dateOnlyMillis(initialDateMillis)) }
    val monthCalendar = Calendar.getInstance().apply { timeInMillis = displayedMonthMillis }
    val todayMillis = dateOnlyMillis(DateTimeUtils.currentMillis())
    val year = monthCalendar.get(Calendar.YEAR)
    val month = monthCalendar.get(Calendar.MONTH)
    val firstDayOffset = monthCalendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
    val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val canMoveToNextMonth = displayedMonthMillis < monthStartMillis(todayMillis)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.width(320.dp),
            color = SleepSurface,
            shape = SleepDialogDefaults.Shape,
            shadowElevation = SleepDialogDefaults.ShadowElevation,
            tonalElevation = SleepDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SleepDialogDefaults.ContentPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { displayedMonthMillis = addMonths(displayedMonthMillis, -1) }) {
                        Text("<", color = SleepPrimary, fontSize = 20.sp)
                    }
                    Text(
                        text = "${year}-${(month + 1).toString().padStart(2, '0')}",
                        color = SleepTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    TextButton(
                        onClick = { displayedMonthMillis = addMonths(displayedMonthMillis, 1) },
                        enabled = canMoveToNextMonth
                    ) {
                        Text(">", color = SleepPrimary, fontSize = 20.sp)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("S", "M", "T", "W", "T", "F", "S").forEach { weekday ->
                        Box(
                            modifier = Modifier.size(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = weekday,
                                color = SleepTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(6) { week ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            repeat(7) { dayOfWeek ->
                                val dayNumber = week * 7 + dayOfWeek - firstDayOffset + 1
                                if (dayNumber in 1..daysInMonth) {
                                    val dateMillis = dateOnlyMillis(year, month, dayNumber)
                                    val isSelected = dateMillis == selectedDateMillis
                                    val isToday = dateMillis == todayMillis
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .noRippleClickable {
                                                if (dateMillis <= todayMillis) selectedDateMillis = dateMillis
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clip(RoundedCornerShape(15.dp))
                                                .background(
                                                    when {
                                                        isSelected -> SleepPrimary
                                                        isToday -> SleepPrimaryLight
                                                        else -> SleepSurface
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = dayNumber.toString(),
                                                color = if (isSelected) SleepSurface else SleepTextPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(36.dp))
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.height(36.dp)) {
                        Text("取消", color = SleepDialogDefaults.dismissActionColor)
                    }
                    TextButton(
                        onClick = { onConfirm(selectedDateMillis) },
                        enabled = selectedDateMillis <= todayMillis,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("确认", color = SleepDialogDefaults.confirmActionColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SleepTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val SleepPrimaryDark = SleepTheme.colors.primaryDark
    val SleepSurface = SleepDialogDefaults.containerColor
    var selectedHour by remember { mutableStateOf(initialHour.coerceIn(0, 23)) }
    var selectedMinute by remember { mutableStateOf(initialMinute.coerceIn(0, 59)) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.width(320.dp),
            color = SleepSurface,
            shape = SleepDialogDefaults.Shape,
            shadowElevation = SleepDialogDefaults.ShadowElevation,
            tonalElevation = SleepDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(SleepDialogDefaults.ContentPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "%02d:%02d".format(selectedHour, selectedMinute),
                    color = SleepPrimaryDark,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TimeWheelPicker(
                        value = selectedHour,
                        range = 24,
                        onValueChange = { selectedHour = it }
                    )
                    TimeWheelPicker(
                        value = selectedMinute,
                        range = 60,
                        onValueChange = { selectedMinute = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.height(36.dp)) {
                        Text("取消", color = SleepDialogDefaults.dismissActionColor)
                    }
                    TextButton(onClick = { onConfirm(selectedHour, selectedMinute) }, modifier = Modifier.height(36.dp)) {
                        Text("确认", color = SleepDialogDefaults.confirmActionColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
private fun TimeWheelPicker(
    value: Int,
    range: Int,
    onValueChange: (Int) -> Unit
) {
    val SleepPrimary = SleepTheme.colors.primary
    val SleepTextSecondary = SleepTheme.colors.textSecondary
    val values = remember(range) { (0 until range).toList() }
    val visibleItemCount = 3
    val loopCount = 10000
    val virtualCount = values.size * loopCount
    val initialValue = remember { value }
    val selectedVirtualIndex = remember(values, initialValue) {
        loopCount / 2 * values.size + values.indexOf(initialValue)
    }
    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    var currentCenterIndex by remember { mutableStateOf(selectedVirtualIndex) }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(selectedVirtualIndex) {
        listState.scrollToItem(selectedVirtualIndex - 1)
        currentCenterIndex = selectedVirtualIndex
        initialized = true
    }

    LaunchedEffect(listState, range) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { firstVisibleIndex ->
                if (initialized) {
                    currentCenterIndex = firstVisibleIndex + visibleItemCount / 2
                }
            }
    }

    LaunchedEffect(listState, range) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrollInProgress ->
                if (!isScrollInProgress && initialized) {
                    val centerIndex = listState.firstVisibleItemIndex + visibleItemCount / 2
                    currentCenterIndex = centerIndex
                    onValueChange(values[floorMod(centerIndex, values.size)])
                }
            }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier
                .height(108.dp)
                .width(88.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(virtualCount) { index ->
                val itemValue = values[floorMod(index, values.size)]
                val isSelected = index == currentCenterIndex
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = itemValue.toString().padStart(2, '0'),
                        color = if (isSelected) SleepPrimary else SleepTextSecondary,
                        fontSize = if (isSelected) 20.sp else 18.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

private fun floorMod(value: Int, modulus: Int): Int {
    return ((value % modulus) + modulus) % modulus
}

private fun dateOnlyMillis(millis: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun monthStartMillis(millis: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun addMonths(millis: Long, months: Int): Long {
    return Calendar.getInstance().apply {
        timeInMillis = millis
        add(Calendar.MONTH, months)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun dateOnlyMillis(year: Int, month: Int, dayOfMonth: Int): Long {
    return Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, dayOfMonth)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun combineDateAndTime(dateMillis: Long, hour: Int, minute: Int): Long {
    return Calendar.getInstance().apply {
        timeInMillis = dateMillis
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun replaceDate(timeMillis: Long?, dateMillis: Long): Long? {
    if (timeMillis == null) return null
    val timeCalendar = Calendar.getInstance().apply { timeInMillis = timeMillis }
    return combineDateAndTime(
        dateMillis = dateMillis,
        hour = timeCalendar.get(Calendar.HOUR_OF_DAY),
        minute = timeCalendar.get(Calendar.MINUTE)
    )
}
