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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lifelog.app.domain.util.TimeUtils
import com.lifelog.app.ui.components.EmptyState
import com.lifelog.app.ui.components.HeatMap
import com.lifelog.app.ui.components.LifeLogCard
import com.lifelog.app.ui.components.StatCard

@Composable
fun MonthlyReportScreen(
    year: Int,
    month: Int,
    viewModel: MonthlyReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedYear by remember { mutableIntStateOf(year) }
    var selectedMonth by remember { mutableIntStateOf(month) }

    LaunchedEffect(selectedYear, selectedMonth) {
        viewModel.loadReport(selectedYear, selectedMonth)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 月切换器
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (selectedMonth == 1) { selectedYear--; selectedMonth = 12 }
                else selectedMonth--
            }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "上一月")
            }
            Text(
                "${selectedYear}年${selectedMonth}月",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            IconButton(onClick = {
                if (selectedMonth == 12) { selectedYear++; selectedMonth = 1 }
                else selectedMonth++
            }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "下一月")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.totalSeconds == 0 && !uiState.isLoading) {
            EmptyState(icon = "📊", message = "本月没有记录数据")
        } else {
            // 统计卡片
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = "⏱️",
                    title = "本月总时长",
                    value = TimeUtils.formatDuration(uiState.totalSeconds),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "📅",
                    title = "活跃天数",
                    value = "${uiState.activeDays}天",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "📈",
                    title = "日均",
                    value = if (uiState.activeDays > 0)
                        TimeUtils.formatDuration(uiState.totalSeconds / uiState.activeDays)
                    else "0min",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 热力图
            if (uiState.dailyHeatData.isNotEmpty()) {
                LifeLogCard {
                    Text("活跃热力图", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    HeatMap(
                        dailyData = uiState.dailyHeatData,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 分类排行
            if (uiState.categorySummary.isNotEmpty()) {
                LifeLogCard {
                    Text("分类排行", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    uiState.categorySummary.forEach { cat ->
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
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
