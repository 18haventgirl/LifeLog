package com.lifelog.app.ui.screen.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelog.app.data.model.CategoryTimeEntry
import com.lifelog.app.data.model.RecordWithDetails
import com.lifelog.app.data.repository.RecordRepository
import com.lifelog.app.domain.util.DateUtils
import com.lifelog.app.domain.util.SummaryGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DailyReportViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyReportUiState())
    val uiState: StateFlow<DailyReportUiState> = _uiState.asStateFlow()

    private var recordsJob: Job? = null

    fun loadReport(date: LocalDate) {
        // 取消上一次的监听
        recordsJob?.cancel()

        recordsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val (dayStart, dayEnd) = DateUtils.dayRange(date)

            // 实时监听记录变化
            recordRepository.getRecordsByDay(dayStart, dayEnd).collect { records ->
                // 重新计算分类汇总
                val categorySummary = recordRepository.getCategorySummaryByDay(dayStart, dayEnd)
                val totalSeconds = categorySummary.sumOf { it.totalSeconds }

                val categories = categorySummary.map { summary ->
                    CategoryTimeEntry(
                        categoryId = summary.categoryId,
                        name = summary.categoryName,
                        icon = summary.categoryIcon,
                        color = summary.categoryColor,
                        totalSeconds = summary.totalSeconds,
                        percent = if (totalSeconds > 0) {
                            (summary.totalSeconds * 100f / totalSeconds)
                        } else 0f
                    )
                }

                val moodSummary = recordRepository.getMoodSummary(dayStart, dayEnd)
                    .associate { it.mood to it.count }

                val recordCount = records.size
                val topCategory = categories.maxByOrNull { it.totalSeconds }?.name
                val summaryText = SummaryGenerator.generateDailySummary(
                    totalSeconds = totalSeconds,
                    topCategoryName = topCategory,
                    recordCount = recordCount,
                    moodSummary = moodSummary
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        totalSeconds = totalSeconds,
                        recordCount = recordCount,
                        categories = categories,
                        timelineRecords = records,
                        moodSummary = moodSummary,
                        topCategory = topCategory,
                        summaryText = summaryText
                    )
                }
            }
        }
    }
}

data class DailyReportUiState(
    val isLoading: Boolean = false,
    val totalSeconds: Int = 0,
    val recordCount: Int = 0,
    val categories: List<CategoryTimeEntry> = emptyList(),
    val timelineRecords: List<RecordWithDetails> = emptyList(),
    val moodSummary: Map<String, Int> = emptyMap(),
    val topCategory: String? = null,
    val summaryText: String = ""
)
