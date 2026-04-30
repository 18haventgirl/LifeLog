package com.lifelog.app.ui.screen.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.lifelog.app.data.local.entity.ActivityEntity
import com.lifelog.app.ui.components.AnimatedCard
import com.lifelog.app.ui.components.DeleteConfirmDialog
import com.lifelog.app.ui.components.LifeLogCard

private val PRESET_COLORS = listOf(
    "#3B82F6", "#8B5CF6", "#10B981", "#F59E0B", "#EF4444",
    "#6366F1", "#EC4899", "#14B8A6", "#F97316", "#06B6D4",
    "#A855F7", "#F43F5E", "#9CA3AF", "#64748B"
)

@OptIn(ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showAddActivityDialog by remember { mutableStateOf(false) }
    var showEditActivityDialog by remember { mutableStateOf<ActivityEntity?>(null) }
    var addActivityCategoryId by remember { mutableStateOf<Long?>(null) }
    var deleteCategoryId by remember { mutableStateOf<Long?>(null) }
    var deleteActivityId by remember { mutableStateOf<Long?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📂 分类管理", style = MaterialTheme.typography.headlineMedium)
                FloatingActionButton(
                    onClick = { showAddCategoryDialog = true },
                    modifier = Modifier.height(40.dp).width(40.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(Icons.Default.Add, contentDescription = "添加分类")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(uiState.categories) { category ->
            val categoryColor = try {
                Color(android.graphics.Color.parseColor(category.color))
            } catch (e: Exception) {
                MaterialTheme.colorScheme.primary
            }

            AnimatedCard(index = uiState.categories.indexOf(category)) {
                LifeLogCard(modifier = Modifier.padding(bottom = 8.dp)) {
                    // 分类头部
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(category.icon, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(category.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${category.activities.size}个活动",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // 添加活动按钮
                        IconButton(
                            onClick = {
                                addActivityCategoryId = category.id
                                showAddActivityDialog = true
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "添加活动", modifier = Modifier.size(20.dp))
                        }
                        // 删除分类（仅自定义）
                        if (!category.isSystem) {
                            IconButton(onClick = { deleteCategoryId = category.id }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "删除分类",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }

                    // 活动列表
                    if (category.activities.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        category.activities.forEach { activity ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${activity.icon} ${activity.name}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                // 编辑
                                IconButton(
                                    onClick = { showEditActivityDialog = activity },
                                    modifier = Modifier.height(24.dp).width(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "编辑",
                                        modifier = Modifier.height(14.dp),
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                // 删除
                                IconButton(
                                    onClick = { deleteActivityId = activity.id },
                                    modifier = Modifier.height(24.dp).width(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "删除",
                                        modifier = Modifier.height(14.dp),
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // ===== 添加分类弹窗 =====
    if (showAddCategoryDialog) {
        var name by remember { mutableStateOf("") }
        var icon by remember { mutableStateOf("📁") }
        var selectedColor by remember { mutableStateOf(PRESET_COLORS[0]) }

        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("添加分类") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("分类名称") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = icon,
                        onValueChange = { icon = it },
                        label = { Text("图标（emoji）") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("颜色", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PRESET_COLORS.forEach { colorHex ->
                            val color = try {
                                Color(android.graphics.Color.parseColor(colorHex))
                            } catch (e: Exception) { MaterialTheme.colorScheme.primary }
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .then(
                                        if (selectedColor == colorHex)
                                            Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                        else Modifier
                                    )
                                    .clickable { selectedColor = colorHex }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addCategory(name, icon, selectedColor)
                            showAddCategoryDialog = false
                        }
                    },
                    enabled = name.isNotBlank()
                ) { Text("添加") }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) { Text("取消") }
            }
        )
    }

    // ===== 添加活动弹窗 =====
    if (showAddActivityDialog) {
        var activityName by remember { mutableStateOf("") }
        var activityIcon by remember { mutableStateOf("📌") }

        AlertDialog(
            onDismissRequest = { showAddActivityDialog = false },
            title = { Text("添加活动") },
            text = {
                Column {
                    OutlinedTextField(
                        value = activityName,
                        onValueChange = { activityName = it },
                        label = { Text("活动名称") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = activityIcon,
                        onValueChange = { activityIcon = it },
                        label = { Text("图标（emoji）") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        addActivityCategoryId?.let { catId ->
                            viewModel.addActivity(catId, activityName, activityIcon)
                        }
                        showAddActivityDialog = false
                    },
                    enabled = activityName.isNotBlank()
                ) { Text("添加") }
            },
            dismissButton = {
                TextButton(onClick = { showAddActivityDialog = false }) { Text("取消") }
            }
        )
    }

    // ===== 编辑活动弹窗 =====
    showEditActivityDialog?.let { activity ->
        var editName by remember { mutableStateOf(activity.name) }
        var editIcon by remember { mutableStateOf(activity.icon) }

        AlertDialog(
            onDismissRequest = { showEditActivityDialog = null },
            title = { Text("编辑活动") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("活动名称") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editIcon,
                        onValueChange = { editIcon = it },
                        label = { Text("图标（emoji）") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateActivity(activity.copy(name = editName, icon = editIcon))
                        showEditActivityDialog = null
                    },
                    enabled = editName.isNotBlank()
                ) { Text("保存") }
            },
            dismissButton = {
                TextButton(onClick = { showEditActivityDialog = null }) { Text("取消") }
            }
        )
    }

    // 删除分类确认
    deleteCategoryId?.let { id ->
        DeleteConfirmDialog(
            title = "删除分类",
            message = "删除分类会同时删除其下所有活动和相关记录，确定要删除吗？",
            onConfirm = {
                viewModel.deleteCategory(id)
                deleteCategoryId = null
            },
            onDismiss = { deleteCategoryId = null }
        )
    }

    // 删除活动确认
    deleteActivityId?.let { id ->
        DeleteConfirmDialog(
            title = "删除活动",
            message = "删除活动会同时删除相关记录，确定要删除吗？",
            onConfirm = {
                viewModel.deleteActivity(id)
                deleteActivityId = null
            },
            onDismiss = { deleteActivityId = null }
        )
    }
}
