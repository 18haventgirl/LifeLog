package com.lifelog.app.ui.screen.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelog.app.data.local.entity.GoalEntity
import com.lifelog.app.data.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalUiState())
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()

    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            goalRepository.getAll().collect { goals ->
                _uiState.update { it.copy(goals = goals) }
            }
        }
    }

    fun addGoal(goal: GoalEntity) {
        viewModelScope.launch {
            goalRepository.insert(goal)
        }
    }

    fun deleteGoal(id: Long) {
        viewModelScope.launch {
            goalRepository.deleteById(id)
        }
    }

    fun toggleGoalActive(goal: GoalEntity) {
        viewModelScope.launch {
            goalRepository.update(goal.copy(isActive = !goal.isActive))
        }
    }
}

data class GoalUiState(
    val goals: List<GoalEntity> = emptyList()
)
