package com.lifelog.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifelog.app.data.local.entity.ActivityEntity
import com.lifelog.app.data.model.CategoryWithActivities

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ActivityPicker(
    categories: List<CategoryWithActivities>,
    selectedCategoryId: Long?,
    onCategorySelect: (Long) -> Unit,
    onActivitySelect: (Long) -> Unit,
    onAddCategory: (() -> Unit)? = null
) {
    var activeCategoryId by remember { mutableStateOf(selectedCategoryId ?: categories.firstOrNull()?.id) }

    LaunchedEffect(categories) {
        if (activeCategoryId == null && categories.isNotEmpty()) {
            activeCategoryId = categories.first().id
        }
    }

    Column {
        // 大类横向滚动
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = category.id == activeCategoryId,
                    onClick = {
                        activeCategoryId = category.id
                        onCategorySelect(category.id)
                    },
                    label = { Text("${category.icon} ${category.name}") },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = try {
                            Color(android.graphics.Color.parseColor(category.color)).copy(alpha = 0.15f)
                        } catch (e: Exception) {
                            MaterialTheme.colorScheme.primaryContainer
                        },
                        selectedLabelColor = try {
                            Color(android.graphics.Color.parseColor(category.color))
                        } catch (e: Exception) {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                )
            }
            // "+" 按钮 — 用 FilterChip 保持对齐
            if (onAddCategory != null) {
                item {
                    FilterChip(
                        selected = false,
                        onClick = onAddCategory,
                        label = {
                            Text("+ 自定义", fontSize = 13.sp)
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 小类网格
        val selectedCategory = categories.find { it.id == activeCategoryId }
        selectedCategory?.let { cat ->
            val activities = cat.activities
            val columns = 4
            val rows = (activities.size + columns - 1) / columns

            for (row in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0 until columns) {
                        val index = row * columns + col
                        if (index < activities.size) {
                            val activity = activities[index]
                            ActivityButton(
                                activity = activity,
                                categoryColor = try {
                                    Color(android.graphics.Color.parseColor(cat.color))
                                } catch (e: Exception) {
                                    MaterialTheme.colorScheme.primary
                                },
                                onClick = { onActivitySelect(activity.id) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (activities.isEmpty()) {
                Text(
                    "该分类暂无活动，请先添加",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
fun ActivityButton(
    activity: ActivityEntity,
    categoryColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(activity.icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                activity.name,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
