package com.lifelog.app.ui.screen.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelog.app.data.model.CategoryTimeEntry
import com.lifelog.app.data.model.DailyBreakdown
import com.lifelog.app.data.model.DailyHeatData
import com.lifelog.app.data.repository.RecordRepository
import com.lifelog.app.domain.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class MonthlyReportViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonthlyReportUiState())
    val uiState: StateFlow<MonthlyReportUiState> = _uiState.asStateFlow()

    fun loadReport(year: Int, month: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val (monthStart, monthEnd) = DateUtils.monthRange(year, month)
            val yearMonth = YearMonth.of(year, month)
            val firstDay = yearMonth.atDay(1)
            val daysInMonth = yearMonth.lengthOfMonth()

            // 每日总时长（热力图用）
            val dailyTotalsRaw = recordRepository.getDailyTotalsForMonth(monthStart, monthEnd)
            val dailyTotalMap = dailyTotalsRaw.associate {
                val day = java.time.Instant.ofEpochMilli(it.startTime)
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                day to it.totalSeconds
            }

            val maxDailyMinutes = dailyTotalMap.values.maxOfOrNull { it / 60 } ?: 1

            // 生成热力图数据（补齐整月）
            val dailyHeatData = mutableListOf<DailyHeatData>()
            for (day in 1..daysInMonth) {
                val date = yearMonth.atDay(day)
                val totalSeconds = dailyTotalMap[date] ?: 0
                val totalMinutes = totalSeconds / 60
                val level = when {
                    totalMinutes == 0 -> 0
                    totalMinutes <= maxDailyMinutes / 4 -> 1
                    totalMinutes <= maxDailyMinutes / 2 -> 2
                    totalMinutes <= maxDailyMinutes * 3 / 4 -> 3
                    else -> 4
                }
                dailyHeatData.add(
                    DailyHeatData(
                        date = date.toString(),
                        totalMinutes = totalMinutes,
                        level = level
                    )
                )
            }

            // 分类汇总
            val categorySummaryRaw = recordRepository.getCategorySummaryByDay(monthStart, monthEnd)
            val totalSeconds = categorySummaryRaw.sumOf { it.totalSeconds }
            val categorySummary = categorySummaryRaw.map { s ->
                CategoryTimeEntry(
                    categoryId = s.categoryId,
                    name = s.categoryName,
                    icon = s.categoryIcon,
                    color = s.categoryColor,
                    totalSeconds = s.totalSeconds,
                    percent = if (totalSeconds > 0) s.totalSeconds * 100f / totalSeconds else 0f
                )
            }

            // 活跃天数
            val activeDays = dailyTotalMap.values.count { it > 0 }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    year = year,
                    month = month,
                    totalSeconds = totalSeconds,
                    activeDays = activeDays,
                    categorySummary = categorySummary,
                    dailyHeatData = dailyHeatData
                )
            }
        }
    }
}

data class MonthlyReportUiState(
    val isLoading: Boolean = false,
    val year: Int = 0,
    val month: Int = 0,
    val totalSeconds: Int = 0,
    val activeDays: Int = 0,
    val categorySummary: List<CategoryTimeEntry> = emptyList(),
    val dailyHeatData: List<DailyHeatData> = emptyList()
)
