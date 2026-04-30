package com.lifelog.app.ui.screen.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelog.app.data.local.entity.ActivityEntity
import com.lifelog.app.data.local.entity.CategoryEntity
import com.lifelog.app.data.model.CategoryWithActivities
import com.lifelog.app.data.repository.ActivityRepository
import com.lifelog.app.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllWithActivities().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    fun addCategory(name: String, icon: String, color: String) {
        viewModelScope.launch {
            categoryRepository.insert(
                CategoryEntity(
                    name = name,
                    icon = icon,
                    color = color,
                    sortOrder = 999,
                    isSystem = false
                )
            )
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            categoryRepository.getById(categoryId)?.let { category ->
                if (!category.isSystem) {
                    categoryRepository.delete(category)
                }
            }
        }
    }

    fun addActivity(categoryId: Long, name: String, icon: String) {
        viewModelScope.launch {
            activityRepository.insert(
                ActivityEntity(
                    name = name,
                    categoryId = categoryId,
                    icon = icon,
                    sortOrder = 999
                )
            )
        }
    }

    fun updateActivity(activity: ActivityEntity) {
        viewModelScope.launch {
            activityRepository.update(activity)
        }
    }

    fun deleteActivity(activityId: Long) {
        viewModelScope.launch {
            activityRepository.getById(activityId)?.let { activity ->
                activityRepository.delete(activity)
            }
        }
    }
}

data class CategoryUiState(
    val categories: List<CategoryWithActivities> = emptyList()
)
