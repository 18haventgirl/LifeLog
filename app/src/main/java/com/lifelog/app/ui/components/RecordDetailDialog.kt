package com.lifelog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifelog.app.data.model.RecordWithDetails
import com.lifelog.app.domain.util.DateUtils
import com.lifelog.app.domain.util.TimeUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecordDetailDialog(
    record: RecordWithDetails,
    onDismiss: () -> Unit,
    onEdit: ((Long) -> Unit)? = null,
    onDelete: ((Long) -> Unit)? = null
) {
    val categoryColor = try {
        Color(android.graphics.Color.parseColor(record.categoryColor))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(record.activityIcon, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(record.activityName, style = MaterialTheme.typography.titleLarge)
            }
        },
        text = {
            Column {
                // 分类
                DetailRow(
                    label = "分类",
                    value = "${record.categoryName}",
                    valueColor = categoryColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 时间
                DetailRow(
                    label = "开始",
                    value = DateUtils.formatDate(record.startTime, "yyyy-MM-dd HH:mm")
                )
                record.endTime?.let { end ->
                    DetailRow(
                        label = "结束",
                        value = DateUtils.formatDate(end, "yyyy-MM-dd HH:mm")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 时长
                DetailRow(
                    label = "时长",
                    value = TimeUtils.formatDuration(record.durationSeconds),
                    valueColor = categoryColor,
                    isBold = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 记录方式
                val typeLabel = when (record.recordType) {
                    "timer" -> "⏱️ 计时器"
                    "manual" -> "✏️ 手动补录"
                    "quick" -> "⚡ 快速打点"
                    else -> record.recordType
                }
                DetailRow(label = "方式", value = typeLabel)

                // 备注
                if (record.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    DetailRow(label = "备注", value = record.note)
                }

                // 心情
                if (record.mood != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    DetailRow(label = "心情", value = record.mood)
                }

                // 标签
                if (record.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("标签", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        record.tags.forEach { tag ->
                            Text(
                                tag,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row {
                onDelete?.let { delete ->
                    TextButton(onClick = { delete(record.id) }) {
                        Text("删除", color = MaterialTheme.colorScheme.error)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("关闭")
                }
            }
        }
    )
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(48.dp)
        )
        Text(
            value,
            fontSize = 14.sp,
            color = valueColor,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}
