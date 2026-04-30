package com.lifelog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifelog.app.data.model.DailyHeatData

@Composable
fun HeatMap(
    dailyData: List<DailyHeatData>,
    modifier: Modifier = Modifier
) {
    val colorLevels = listOf(
        Color(0xFFE2E8F0),
        Color(0xFF93C5FD),
        Color(0xFF60A5FA),
        Color(0xFF3B82F6),
        Color(0xFF1D4ED8),
    )

    // 按周分组，第一周可能不满
    val weeks = mutableListOf<List<DailyHeatData>>()
    val firstWeekPadding = if (dailyData.isNotEmpty()) {
        // 计算第一天是星期几（周一=0）
        val firstDate = java.time.LocalDate.parse(dailyData.first().date)
        val dayOfWeek = firstDate.dayOfWeek.value - 1 // 0=周一
        dayOfWeek
    } else 0

    var currentWeek = MutableList<DailyHeatData?>(firstWeekPadding) { null }
    dailyData.forEach { day ->
        currentWeek.add(day)
        if (currentWeek.size == 7) {
            weeks.add(currentWeek.filterNotNull())
            currentWeek = mutableListOf()
        }
    }
    if (currentWeek.isNotEmpty()) {
        weeks.add(currentWeek.filterNotNull())
    }

    val totalWeeks = weeks.size.coerceAtLeast(1)

    Column(modifier = modifier) {
        // 星期标签
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            listOf("一", "二", "三", "四", "五", "六", "日").forEach { day ->
                Text(
                    day,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 热力图格子 — 等宽排列占满整行
        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.width(20.dp))
                // 填充 7 天
                val weekDays = MutableList<DailyHeatData?>(7) { null }
                week.forEach { day ->
                    val firstDate = java.time.LocalDate.parse(day.date)
                    val dayOfWeek = firstDate.dayOfWeek.value - 1
                    weekDays[dayOfWeek] = day
                }
                weekDays.forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(1.dp)
                            .background(
                                colorLevels[day?.level?.coerceIn(0, colorLevels.lastIndex) ?: 0],
                                RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }

        // 图例
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text("少", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(4.dp))
            colorLevels.forEach { color ->
                Box(
                    modifier = Modifier
                        .width(14.dp)
                        .height(14.dp)
                        .background(color, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text("多", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
