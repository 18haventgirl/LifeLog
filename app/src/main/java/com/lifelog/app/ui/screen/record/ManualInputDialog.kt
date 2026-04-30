package com.lifelog.app.ui.screen.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lifelog.app.data.model.CategoryWithActivities
import com.lifelog.app.data.model.ManualRecordData
import com.lifelog.app.ui.components.ActivityPicker
import com.lifelog.app.ui.components.MoodSelector
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualInputDialog(
    categories: List<CategoryWithActivities>,
    onDismiss: () -> Unit,
    onSave: (ManualRecordData) -> Unit
) {
    var selectedActivityId by remember { mutableStateOf<Long?>(null) }
    var note by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf<String?>(null) }
    var tagsText by remember { mutableStateOf("") }

    // 时间状态
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var startTime by remember { mutableStateOf(LocalTime.now().minusHours(1)) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var endTime by remember { mutableStateOf(LocalTime.now()) }

    // 弹窗控制
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("✏️ 手动补录") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // 活动选择
                Text("选择活动", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                ActivityPicker(
                    categories = categories,
                    selectedCategoryId = null,
                    onCategorySelect = {},
                    onActivitySelect = { selectedActivityId = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 开始时间
                Text("开始时间", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showStartDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("${startDate.monthValue}/${startDate.dayOfMonth}")
                    }
                    OutlinedButton(
                        onClick = { showStartTimePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("%02d:%02d".format(startTime.hour, startTime.minute))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 结束时间
                Text("结束时间", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showEndDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("${endDate.monthValue}/${endDate.dayOfMonth}")
                    }
                    OutlinedButton(
                        onClick = { showEndTimePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("%02d:%02d".format(endTime.hour, endTime.minute))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 备注
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("备注（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 心情
                Text("心情", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                MoodSelector(
                    selectedMood = selectedMood,
                    onMoodSelect = { selectedMood = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 标签
                OutlinedTextField(
                    value = tagsText,
                    onValueChange = { tagsText = it },
                    label = { Text("标签（逗号分隔）") },
                    placeholder = { Text("如：高效,专注") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedActivityId?.let { activityId ->
                        val start = LocalDateTime.of(startDate, startTime)
                            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        val end = LocalDateTime.of(endDate, endTime)
                            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        if (end > start) {
                            onSave(
                                ManualRecordData(
                                    activityId = activityId,
                                    startTime = start,
                                    endTime = end,
                                    note = note,
                                    mood = selectedMood,
                                    tags = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                )
                            )
                            onDismiss()
                        }
                    }
                }
            ) { Text("保存") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )

    // DatePicker 弹窗
    if (showStartDatePicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        startDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showStartDatePicker = false
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("取消") }
            }
        ) { DatePicker(state = state) }
    }

    if (showEndDatePicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        endDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showEndDatePicker = false
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("取消") }
            }
        ) { DatePicker(state = state) }
    }

    // TimePicker 弹窗
    if (showStartTimePicker) {
        val state = rememberTimePickerState(
            initialHour = startTime.hour,
            initialMinute = startTime.minute
        )
        AlertDialog(
            onDismissRequest = { showStartTimePicker = false },
            title = { Text("选择开始时间") },
            text = { TimePicker(state = state) },
            confirmButton = {
                TextButton(onClick = {
                    startTime = LocalTime.of(state.hour, state.minute)
                    showStartTimePicker = false
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false }) { Text("取消") }
            }
        )
    }

    if (showEndTimePicker) {
        val state = rememberTimePickerState(
            initialHour = endTime.hour,
            initialMinute = endTime.minute
        )
        AlertDialog(
            onDismissRequest = { showEndTimePicker = false },
            title = { Text("选择结束时间") },
            text = { TimePicker(state = state) },
            confirmButton = {
                TextButton(onClick = {
                    endTime = LocalTime.of(state.hour, state.minute)
                    showEndTimePicker = false
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showEndTimePicker = false }) { Text("取消") }
            }
        )
    }
}

@Composable
fun QuickInputDialog(
    categories: List<CategoryWithActivities>,
    onDismiss: () -> Unit,
    onSave: (Long) -> Unit
) {
    var selectedActivityId by remember { mutableStateOf<Long?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("⚡ 快速打点") },
        text = {
            Column {
                Text(
                    "选择你正在做的事情，系统自动记录时间点",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                ActivityPicker(
                    categories = categories,
                    selectedCategoryId = null,
                    onCategorySelect = {},
                    onActivitySelect = { selectedActivityId = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedActivityId?.let {
                        onSave(it)
                        onDismiss()
                    }
                }
            ) { Text("✅ 记录此刻") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
