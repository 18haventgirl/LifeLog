package com.lifelog.app.data.model

import com.lifelog.app.data.local.entity.GoalEntity

data class Goal(
    val id: Long,
    val activityId: Long?,
    val categoryId: Long?,
    val goalType: String,
    val targetMinutes: Int,
    val period: String,
    val isActive: Boolean
)

fun GoalEntity.toDomain() = Goal(
    id = id,
    activityId = activityId,
    categoryId = categoryId,
    goalType = goalType,
    targetMinutes = targetMinutes,
    period = period,
    isActive = isActive
)
