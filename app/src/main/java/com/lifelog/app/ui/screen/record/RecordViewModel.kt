package com.lifelog.app.ui.screen.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelog.app.data.local.entity.RecordEntity
import com.lifelog.app.data.model.CategoryWithActivities
import com.lifelog.app.data.model.ManualRecordData
import com.lifelog.app.data.model.RecordWithDetails
import com.lifelog.app.data.repository.ActivityRepository
import com.lifelog.app.data.repository.CategoryRepository
import com.lifelog.app.data.repository.RecordRepository
import com.lifelog.app.domain.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val categoryRepository: CategoryRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        observeActiveRecord()
        loadCategories()
        loadTodayRecords()
        loadTodayStats()
    }

    private fun observeActiveRecord() {
        viewModelScope.launch {
            recordRepository.observeActiveRecord().collect { record ->
                if (record != null) {
                    val activity = activityRepository.getById(record.activityId)
                    val category = activity?.let { categoryRepository.getById(it.categoryId) }
                    _uiState.update {
                        it.copy(
                            isTimerRunning = true,
                            currentActivityName = activity?.name ?: "未知活动",
                            currentCategoryIcon = category?.icon ?: "⏱️",
                            currentCategoryColor = category?.color,
                            elapsedSeconds = record.durationSeconds
                        )
                    }
                    startLocalTimer(record.durationSeconds)
                } else {
                    _uiState.update {
                        it.copy(
                            isTimerRunning = false,
                            isTimerPaused = false,
                            currentActivityName = null,
                            currentCategoryIcon = null,
                            currentCategoryColor = null,
                            elapsedSeconds = 0
                        )
                    }
                    timerJob?.cancel()
                }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllWithActivities().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    private fun loadTodayRecords() {
        viewModelScope.launch {
            val (dayStart, dayEnd) = DateUtils.todayRange()
            recordRepository.getRecordsByDay(dayStart, dayEnd).collect { records ->
                _uiState.update { it.copy(todayRecords = records) }
            }
        }
    }

    private fun loadTodayStats() {
        viewModelScope.launch {
            val (dayStart, dayEnd) = DateUtils.todayRange()
            val summary = recordRepository.getCategorySummaryByDay(dayStart, dayEnd)
            val totalSeconds = summary.sumOf { it.totalSeconds }
            val count = recordRepository.getTodayRecordCount(dayStart, dayEnd)
            _uiState.update {
                it.copy(
                    todayTotalSeconds = totalSeconds,
                    todayRecordCount = count
                )
            }
        }
    }

    fun selectCategory(categoryId: Long) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun startTimer(activityId: Long) {
        viewModelScope.launch {
            // 先停止当前计时
            recordRepository.getActiveRecord()?.let { active ->
                recordRepository.finishRecord(active.id, System.currentTimeMillis())
            }

            recordRepository.startRecord(
                activityId = activityId,
                startTime = System.currentTimeMillis(),
                recordType = "timer"
            )

            loadTodayRecords()
            loadTodayStats()
        }
    }

    fun pauseTimer() {
        _uiState.update { it.copy(isTimerPaused = true) }
        timerJob?.cancel()
    }

    fun resumeTimer() {
        _uiState.update { it.copy(isTimerPaused = false) }
        startLocalTimer(_uiState.value.elapsedSeconds)
    }

    fun stopTimer() {
        viewModelScope.launch {
            recordRepository.getActiveRecord()?.let { active ->
                recordRepository.finishRecord(active.id, System.currentTimeMillis())
            }
            timerJob?.cancel()
            loadTodayRecords()
            loadTodayStats()
        }
    }

    private fun startLocalTimer(initialSeconds: Int) {
        timerJob?.cancel()
        var seconds = initialSeconds
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!_uiState.value.isTimerPaused) {
                    seconds++
                    _uiState.update { it.copy(elapsedSeconds = seconds) }
                }
            }
        }
    }

    fun addManualRecord(data: ManualRecordData) {
        viewModelScope.launch {
            recordRepository.addManualRecord(data)
            loadTodayRecords()
            loadTodayStats()
        }
    }

    fun addQuickRecord(activityId: Long) {
        viewModelScope.launch {
            recordRepository.getActiveRecord()?.let { active ->
                if (active.recordType == "quick") {
                    recordRepository.finishRecord(active.id, System.currentTimeMillis())
                }
            }
            recordRepository.startRecord(
                activityId = activityId,
                startTime = System.currentTimeMillis(),
                recordType = "quick"
            )
            loadTodayRecords()
        }
    }

    fun deleteRecord(recordId: Long) {
        viewModelScope.launch {
            recordRepository.deleteRecord(recordId)
            loadTodayRecords()
            loadTodayStats()
        }
    }
}

data class RecordUiState(
    val categories: List<CategoryWithActivities> = emptyList(),
    val selectedCategoryId: Long? = null,
    val isTimerRunning: Boolean = false,
    val isTimerPaused: Boolean = false,
    val currentActivityName: String? = null,
    val currentCategoryIcon: String? = null,
    val currentCategoryColor: String? = null,
    val elapsedSeconds: Int = 0,
    val todayRecords: List<RecordWithDetails> = emptyList(),
    val todayTotalSeconds: Int = 0,
    val todayRecordCount: Int = 0
)
