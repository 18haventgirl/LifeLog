package com.lifelog.app.ui.screen.report

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lifelog.app.domain.util.DateUtils
import com.lifelog.app.domain.util.TimeUtils
import com.lifelog.app.ui.components.DateSwitcher
import com.lifelog.app.ui.components.EmptyState
import com.lifelog.app.ui.components.GanttChart
import com.lifelog.app.ui.components.LifeLogCard
import com.lifelog.app.ui.components.PieChart
import com.lifelog.app.ui.components.PieSlice
import com.lifelog.app.ui.components.StatCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import androidx.navigation.NavController

@Composable
fun DailyReportScreen(
    navController: NavController,
    viewModel: DailyReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(selectedDate) {
        viewModel.loadReport(selectedDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 日期切换器
        DateSwitcher(
            date = selectedDate,
            format = { it.format(DateTimeFormatter.ofPattern("yyyy年M月d日 EEEE", Locale.CHINA)) },
            onPrevious = { selectedDate = selectedDate.minusDays(1) },
            onNext = { selectedDate = selectedDate.plusDays(1) },
            onToday = { selectedDate = LocalDate.now() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.totalSeconds == 0 && !uiState.isLoading) {
            EmptyState(
                icon = "📅",
                message = "这天没有记录数据"
            )
        } else {
            // 数据卡片
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = "⏱️",
                    title = "总时长",
                    value = TimeUtils.formatDuration(uiState.totalSeconds),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "📋",
                    title = "记录",
                    value = "${uiState.recordCount}次",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "🏆",
                    title = "最多",
                    value = uiState.topCategory ?: "-",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 饼图
            if (uiState.categories.isNotEmpty()) {
                LifeLogCard {
                    Text("时间分布", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    PieChart(
                        data = uiState.categories.map {
                            PieSlice(
                                label = "${it.icon} ${it.name}",
                                value = it.totalSeconds.toFloat(),
                                color = try {
                                    Color(android.graphics.Color.parseColor(it.color))
                                } catch (e: Exception) {
                                    MaterialTheme.colorScheme.primary
                                },
                                percent = it.percent
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )
                    // 图例
                    Spacer(modifier = Modifier.height(12.dp))
                    uiState.categories.forEach { cat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        try {
                                            Color(android.graphics.Color.parseColor(cat.color))
                                        } catch (e: Exception) {
                                            MaterialTheme.colorScheme.primary
                                        },
                                        RoundedCornerShape(3.dp)
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "${cat.icon} ${cat.name}",
                                modifier = Modifier.weight(1f),
                                fontSize = 13.sp
                            )
                            Text(
                                TimeUtils.formatDuration(cat.totalSeconds),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "${String.format("%.1f", cat.percent)}%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 甘特图
            if (uiState.timelineRecords.isNotEmpty()) {
                LifeLogCard {
                    Text("时间线", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    GanttChart(
                        records = uiState.timelineRecords,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 心情统计
            if (uiState.moodSummary.isNotEmpty()) {
                LifeLogCard {
                    Text("今日心情", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        uiState.moodSummary.forEach { (mood, count) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(mood, fontSize = 28.sp)
                                Text(
                                    "${count}次",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 总结语
            if (uiState.summaryText.isNotEmpty()) {
                LifeLogCard(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ) {
                    Text("💡 今日总结", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        uiState.summaryText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // 月报入口
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    val now = java.time.LocalDate.now()
                    navController.navigate("monthly_report/${now.year}/${now.monthValue}")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("查看本月报告")
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
