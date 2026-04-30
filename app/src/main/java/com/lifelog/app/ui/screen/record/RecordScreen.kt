package com.lifelog.app.ui.screen.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.lifelog.app.domain.util.DateUtils
import com.lifelog.app.domain.util.TimeUtils
import com.lifelog.app.ui.components.ActivityPicker
import com.lifelog.app.ui.components.AnimatedCard
import com.lifelog.app.ui.components.CurrentActivityCard
import com.lifelog.app.ui.components.DeleteConfirmDialog
import com.lifelog.app.ui.components.EmptyState
import com.lifelog.app.ui.components.RecordDetailDialog
import com.lifelog.app.ui.components.StatCard

@Composable
fun RecordScreen(
    viewModel: RecordViewModel = hiltViewModel(),
    navController: NavHostController? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showManualDialog by remember { mutableStateOf(false) }
    var showQuickDialog by remember { mutableStateOf(false) }
    var deleteTargetId by remember { mutableStateOf<Long?>(null) }
    var detailRecord by remember { mutableStateOf<com.lifelog.app.data.model.RecordWithDetails?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // ===== 当前活动卡片 =====
        AnimatedCard(index = 0) {
            CurrentActivityCard(
                isActive = uiState.isTimerRunning,
                isPaused = uiState.isTimerPaused,
                categoryIcon = uiState.currentCategoryIcon ?: "⏱️",
                activityName = uiState.currentActivityName ?: "未开始记录",
                categoryColor = uiState.currentCategoryColor?.let {
                    try { Color(android.graphics.Color.parseColor(it)) } catch (e: Exception) { null }
                } ?: MaterialTheme.colorScheme.primary,
                elapsedSeconds = uiState.elapsedSeconds,
                onPause = { viewModel.pauseTimer() },
                onResume = { viewModel.resumeTimer() },
                onStop = { viewModel.stopTimer() }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ===== 活动选择器 =====
        Text(
            "选择活动",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        AnimatedCard(index = 1) {
            ActivityPicker(
                categories = uiState.categories,
                selectedCategoryId = uiState.selectedCategoryId,
                onCategorySelect = { viewModel.selectCategory(it) },
                onActivitySelect = { activityId -> viewModel.startTimer(activityId) },
                onAddCategory = { navController?.navigate(com.lifelog.app.ui.navigation.Screen.Category.route) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ===== 今日统计概览 =====
        AnimatedCard(index = 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = "⏱️",
                    title = "今日记录",
                    value = TimeUtils.formatDuration(uiState.todayTotalSeconds),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "📋",
                    title = "记录次数",
                    value = "${uiState.todayRecordCount}",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ===== 今日记录标题 + 操作按钮 =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "📋 今日记录",
                style = MaterialTheme.typography.titleMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(onClick = { showManualDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("补录", fontSize = 13.sp)
                }
                FilledTonalButton(onClick = { showQuickDialog = true }) {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("打点", fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ===== 今日记录列表 =====
        if (uiState.todayRecords.isEmpty()) {
            EmptyState(
                icon = "📝",
                message = "今天还没有记录\n点击上方活动开始记录吧"
            )
        } else {
            uiState.todayRecords.forEachIndexed { index, record ->
                AnimatedCard(index = index + 3) {
                    RecordItem(
                        record = record,
                        onClick = { detailRecord = it },
                        onDelete = { deleteTargetId = it }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // 弹窗
    if (showManualDialog) {
        ManualInputDialog(
            categories = uiState.categories,
            onDismiss = { showManualDialog = false },
            onSave = { viewModel.addManualRecord(it) }
        )
    }
    if (showQuickDialog) {
        QuickInputDialog(
            categories = uiState.categories,
            onDismiss = { showQuickDialog = false },
            onSave = { viewModel.addQuickRecord(it) }
        )
    }

    // 删除确认
    deleteTargetId?.let { id ->
        DeleteConfirmDialog(
            message = "确定要删除这条记录吗？此操作不可撤销。",
            onConfirm = {
                viewModel.deleteRecord(id)
                deleteTargetId = null
            },
            onDismiss = { deleteTargetId = null }
        )
    }

    // 记录详情
    detailRecord?.let { record ->
        RecordDetailDialog(
            record = record,
            onDismiss = { detailRecord = null },
            onDelete = { id ->
                viewModel.deleteRecord(id)
                detailRecord = null
            }
        )
    }
}

@Composable
private fun RecordItem(
    record: com.lifelog.app.data.model.RecordWithDetails,
    onClick: (com.lifelog.app.data.model.RecordWithDetails) -> Unit,
    onDelete: (Long) -> Unit
) {
    val categoryColor = try {
        Color(android.graphics.Color.parseColor(record.categoryColor))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick(record) }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(record.activityIcon, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(record.activityName, style = MaterialTheme.typography.bodyLarge)
            Text(
                DateUtils.formatDate(record.startTime, "HH:mm") +
                    record.endTime?.let { " - ${DateUtils.formatDate(it, "HH:mm")}" } ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            TimeUtils.formatDuration(record.durationSeconds),
            style = MaterialTheme.typography.titleMedium,
            color = categoryColor
        )
        IconButton(onClick = { onDelete(record.id) }) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "删除",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
