package com.lifelog.app.data.model

import com.lifelog.app.data.local.entity.ActivityEntity

data class Activity(
    val id: Long,
    val name: String,
    val categoryId: Long,
    val icon: String,
    val sortOrder: Int,
    val isActive: Boolean
)

fun ActivityEntity.toDomain() = Activity(
    id = id,
    name = name,
    categoryId = categoryId,
    icon = icon,
    sortOrder = sortOrder,
    isActive = isActive
)
