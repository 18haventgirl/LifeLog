package com.lifelog.app.ui.screen.goal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lifelog.app.data.local.entity.GoalEntity
import com.lifelog.app.ui.components.AnimatedCard
import com.lifelog.app.ui.components.DeleteConfirmDialog
import com.lifelog.app.ui.components.EmptyState
import com.lifelog.app.ui.components.LifeLogCard

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(
    viewModel: GoalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var deleteTargetId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🎯 我的目标", style = MaterialTheme.typography.headlineMedium)
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.height(40.dp).width(40.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加目标")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.goals.isEmpty()) {
            EmptyState(icon = "🎯", message = "还没有设置目标\n点击右上角 + 添加")
        } else {
            LazyColumn {
                items(uiState.goals) { goal ->
                    AnimatedCard(index = uiState.goals.indexOf(goal)) {
                        LifeLogCard(modifier = Modifier.padding(bottom = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "${if (goal.goalType == "min") "至少" else "最多"} ${goal.targetMinutes}分钟",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        "${if (goal.period == "daily") "每天" else "每周"} · ${if (goal.isActive) "进行中" else "已暂停"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = goal.isActive,
                                    onCheckedChange = { viewModel.toggleGoalActive(goal) }
                                )
                                IconButton(onClick = { deleteTargetId = goal.id }) {
                                    Icon(Icons.Default.Delete, contentDescription = "删除")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 添加目标弹窗
    if (showAddDialog) {
        var targetMinutes by remember { mutableStateOf("60") }
        var goalType by remember { mutableStateOf("min") }
        var period by remember { mutableStateOf("daily") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("添加目标") },
            text = {
                Column {
                    Text("目标类型", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = goalType == "min",
                            onClick = { goalType = "min" },
                            label = { Text("至少") }
                        )
                        FilterChip(
                            selected = goalType == "max",
                            onClick = { goalType = "max" },
                            label = { Text("最多") }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("周期", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = period == "daily",
                            onClick = { period = "daily" },
                            label = { Text("每天") }
                        )
                        FilterChip(
                            selected = period == "weekly",
                            onClick = { period = "weekly" },
                            label = { Text("每周") }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = targetMinutes,
                        onValueChange = { targetMinutes = it.filter { c -> c.isDigit() } },
                        label = { Text("目标时长（分钟）") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val minutes = targetMinutes.toIntOrNull() ?: 0
                        if (minutes > 0) {
                            viewModel.addGoal(
                                GoalEntity(
                                    goalType = goalType,
                                    targetMinutes = minutes,
                                    period = period
                                )
                            )
                            showAddDialog = false
                        }
                    }
                ) { Text("添加") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("取消") }
            }
        )
    }

    // 删除确认
    deleteTargetId?.let { id ->
        DeleteConfirmDialog(
            title = "删除目标",
            message = "确定要删除这个目标吗？",
            onConfirm = {
                viewModel.deleteGoal(id)
                deleteTargetId = null
            },
            onDismiss = { deleteTargetId = null }
        )
    }
}
