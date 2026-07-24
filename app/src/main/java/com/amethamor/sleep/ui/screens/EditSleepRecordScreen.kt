package com.amethamor.sleep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.amethamor.sleep.data.SleepRecord
import com.amethamor.sleep.data.SleepType
import com.amethamor.sleep.ui.SleepViewModel
import com.amethamor.sleep.ui.components.SleepDialogDefaults
import com.amethamor.sleep.ui.components.SleepCard
import com.amethamor.sleep.ui.components.SleepRecordDropdownField
import com.amethamor.sleep.ui.components.SleepRecordNumberField
import com.amethamor.sleep.ui.components.SleepRecordSwitchField
import com.amethamor.sleep.ui.components.SleepRecordTimeField
import com.amethamor.sleep.ui.components.noRippleClickable
import com.amethamor.sleep.ui.theme.SleepTheme
import com.amethamor.sleep.util.DateTimeUtils
import com.amethamor.sleep.util.SleepCalculateUtils
import java.util.Calendar

@Composable
fun EditSleepRecordScreen(
    record: SleepRecord?,
    isManualEntry: Boolean = false,
    viewModel: SleepViewModel,
    onBack: () -> Unit
) {
    val SleepPrimary = SleepTheme.colors.primary
    val SleepPrimaryDark = SleepTheme.colors.primaryDark
    val SleepPrimaryLight = SleepTheme.colors.selectedBackground
    val SleepSurface = SleepDialogDefaults.containerColor
    val nowMillis = DateTimeUtils.currentMillis()
    
    var bedTimeMillis by remember {
        mutableStateOf(record?.bedTimeMillis)
    }
    var wakeTimeMillis by remember {
        mutableStateOf(record?.wakeTimeMillis)
    }
    var bedDateMillis by remember {
        mutableStateOf(dateOnlyMillis(record?.bedTimeMillis ?: nowMillis))
    }
    var wakeDateMillis by remember {
        mutableStateOf(dateOnlyMillis(record?.wakeTimeMillis ?: nowMillis))
    }
    var hasDream by remember {
        mutableStateOf(record?.hasDream ?: false)
    }
    var wakeUpCount by remember {
        mutableStateOf(record?.wakeUpCount ?: 0)
    }
    var hasNightmare by remember {
        mutableStateOf(record?.hasNightmare ?: false)
    }
    var wakeFeeling by remember {
        mutableStateOf(
            when (record?.wakeFeeling) {
                "\u5f88\u597d" -> "\u6e05\u9192"
                "\u597d" -> "\u8fd8\u884c"
                "\u5dee" -> "\u56f0\u5026"
                "\u5f88\u5dee" -> "\u5f88\u7d2f"
                else -> record?.wakeFeeling ?: "\u4e00\u822c"
            }
        )
    }
    var isNapRecord by remember { mutableStateOf(record?.sleepType == SleepType.NAP) }
    var sleepTypeManuallySet by remember { mutableStateOf(record?.sleepTypeManuallySet ?: false) }
    var datePickerTarget by remember { mutableStateOf<Boolean?>(null) }
    var timePickerTarget by remember { mutableStateOf<Boolean?>(null) }

    val wakeFeelingOptions = listOf("\u6e05\u9192", "\u8fd8\u884c", "\u4e00\u822c", "\u56f0\u5026", "\u5f88\u7d2f")

    fun showDatePicker(isBedTime: Boolean) {
        datePickerTarget = isBedTime
    }

    fun showTimePicker(isBedTime: Boolean) {
        timePickerTarget = isBedTime
    }

    fun refreshAutoSleepType() {
        if (sleepTypeManuallySet) return
        val durationMinutes = SleepCalculateUtils.calculateDurationMinutes(bedTimeMillis, wakeTimeMillis)
        isNapRecord = SleepCalculateUtils.inferSleepType(
            bedTimeMillis = bedTimeMillis,
            wakeTimeMillis = wakeTimeMillis,
            durationMinutes = durationMinutes
        ) == SleepType.NAP
    }

    fun handleSave() {
        val now = DateTimeUtils.currentMillis()
        val updatedAt = now
        val createdAt = record?.createdAt ?: now
        val durationMinutes = SleepCalculateUtils.calculateDurationMinutes(bedTimeMillis, wakeTimeMillis)
        val recordDate = SleepCalculateUtils.calculateRecordDate(bedTimeMillis, wakeTimeMillis)
        val sleepType = if (isNapRecord) SleepType.NAP else SleepType.NIGHT
        
        val newRecord = SleepRecord(
            id = record?.id ?: 0,
            recordDate = recordDate,
            bedTimeMillis = bedTimeMillis,
            wakeTimeMillis = wakeTimeMillis,
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
        
        if (isManualEntry || record == null) {
            viewModel.insertSleepRecord(newRecord)
        } else {
            viewModel.updateSleepRecord(newRecord)
        }
        
        onBack()
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
        val initialCalendar = Calendar.getInstance().apply {
            timeInMillis = if (isBedTime) {
                bedTimeMillis ?: nowMillis
            } else {
                wakeTimeMillis ?: nowMillis
            }
        }

        SleepTimePickerDialog(
            initialHour = initialCalendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = initialCalendar.get(Calendar.MINUTE),
            onDismiss = { timePickerTarget = null },
            onConfirm = { selectedHour, selectedMinute ->
                val selectedDate = if (isBedTime) bedDateMillis else wakeDateMillis
                val selectedMillis = combineDateAndTime(selectedDate, selectedHour, selectedMinute)
                if (isBedTime) {
                    bedTimeMillis = selectedMillis
                } else {
                    wakeTimeMillis = selectedMillis
                }
                refreshAutoSleepType()
                timePickerTarget = null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isManualEntry) "手动补录" else "编辑睡眠记录",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

        SleepCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SleepRecordTimeField(
                    label = "入睡日期",
                    value = DateTimeUtils.millisToDateString(bedDateMillis),
                    onClick = { showDatePicker(isBedTime = true) }
                )

                SleepRecordTimeField(
                    label = "入睡时间",
                    value = DateTimeUtils.millisToTimeString(bedTimeMillis),
                    onClick = { showTimePicker(isBedTime = true) }
                )

                SleepRecordTimeField(
                    label = "起床日期",
                    value = DateTimeUtils.millisToDateString(wakeDateMillis),
                    onClick = { showDatePicker(isBedTime = false) }
                )

                SleepRecordTimeField(
                    label = "起床时间",
                    value = DateTimeUtils.millisToTimeString(wakeTimeMillis),
                    onClick = { showTimePicker(isBedTime = false) }
                )

                SleepRecordSwitchField(
                    label = "\u5348\u7761\u8bb0\u5f55",
                    checked = isNapRecord,
                    onCheckedChange = {
                        isNapRecord = it
                        sleepTypeManuallySet = true
                    }
                )

                SleepRecordSwitchField(
                    label = "\u505a\u68a6",
                    checked = hasDream,
                    onCheckedChange = { hasDream = it }
                )

                SleepRecordNumberField(
                    label = "夜醒次数",
                    value = wakeUpCount,
                    onValueChange = { wakeUpCount = it }
                )

                SleepRecordSwitchField(
                    label = "噩梦",
                    checked = hasNightmare,
                    onCheckedChange = { hasNightmare = it }
                )

                SleepRecordDropdownField(
                    label = "醒来状态",
                    options = wakeFeelingOptions,
                    selectedOption = wakeFeeling,
                    onOptionSelected = { wakeFeeling = it }
                )

            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SleepPrimaryLight,
                    contentColor = SleepPrimaryDark
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "取消",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Button(
                onClick = { handleSave() },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SleepPrimary,
                    contentColor = SleepSurface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "保存",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
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
                    TextButton(onClick = { displayedMonthMillis = addMonths(displayedMonthMillis, 1) }) {
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
                                            .noRippleClickable { selectedDateMillis = dateMillis },
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
                    TextButton(onClick = { onConfirm(selectedDateMillis) }, modifier = Modifier.height(36.dp)) {
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
@OptIn(ExperimentalFoundationApi::class)
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
