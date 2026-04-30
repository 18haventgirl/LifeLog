package com.lifelog.app.ui.screen.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelog.app.data.model.CategoryTimeEntry
import com.lifelog.app.data.model.DailyBreakdown
import com.lifelog.app.data.repository.RecordRepository
import com.lifelog.app.domain.util.DateUtils
import com.lifelog.app.domain.util.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class WeeklyReportViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyReportUiState())
    val uiState: StateFlow<WeeklyReportUiState> = _uiState.asStateFlow()

    fun loadReport(weekDate: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val monday = weekDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val sunday = monday.plusDays(6)
            val (weekStart, weekEnd) = DateUtils.weekRange(weekDate)

            // 每天的分类汇总
            val dailyBreakdowns = mutableListOf<DailyBreakdown>()
            val allCategoryTotals = mutableMapOf<Long, CategoryTimeEntry>()
            var totalSeconds = 0

            for (i in 0..6) {
                val day = monday.plusDays(i.toLong())
                val (dayStart, dayEnd) = DateUtils.dayRange(day)
                val summary = recordRepository.getCategorySummaryByDay(dayStart, dayEnd)
                val dayTotal = summary.sumOf { it.totalSeconds }
                totalSeconds += dayTotal

                val categories = summary.map { s ->
                    val entry = CategoryTimeEntry(
                        categoryId = s.categoryId,
                        name = s.categoryName,
                        icon = s.categoryIcon,
                        color = s.categoryColor,
                        totalSeconds = s.totalSeconds,
                        percent = 0f
                    )
                    // 累加到总分类
                    val existing = allCategoryTotals[s.categoryId]
                    if (existing != null) {
                        allCategoryTotals[s.categoryId] = existing.copy(
                            totalSeconds = existing.totalSeconds + s.totalSeconds
                        )
                    } else {
                        allCategoryTotals[s.categoryId] = entry
                    }
                    entry
                }

                dailyBreakdowns.add(
                    DailyBreakdown(
                        dayLabel = listOf("周一", "周二", "周三", "周四", "周五", "周六", "日")[i],
                        totalSeconds = dayTotal,
                        categories = categories
                    )
                )
            }

            // 计算百分比
            val categorySummary = allCategoryTotals.values.map { entry ->
                entry.copy(
                    percent = if (totalSeconds > 0) entry.totalSeconds * 100f / totalSeconds else 0f
                )
            }.sortedByDescending { it.totalSeconds }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    weekStart = monday.toString(),
                    weekEnd = sunday.toString(),
                    totalSeconds = totalSeconds,
                    dailyBreakdown = dailyBreakdowns,
                    categorySummary = categorySummary
                )
            }
        }
    }
}

data class WeeklyReportUiState(
    val isLoading: Boolean = false,
    val weekStart: String = "",
    val weekEnd: String = "",
    val totalSeconds: Int = 0,
    val dailyBreakdown: List<DailyBreakdown> = emptyList(),
    val categorySummary: List<CategoryTimeEntry> = emptyList()
)
