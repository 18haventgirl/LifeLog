package com.lifelog.app.data.model

import com.lifelog.app.data.local.entity.ActivityEntity
import com.lifelog.app.data.local.entity.CategoryEntity

data class Category(
    val id: Long,
    val name: String,
    val icon: String,
    val color: String,
    val sortOrder: Int,
    val isSystem: Boolean
)

data class CategoryWithActivities(
    val id: Long,
    val name: String,
    val icon: String,
    val color: String,
    val sortOrder: Int,
    val isSystem: Boolean,
    val activities: List<ActivityEntity>
)

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    icon = icon,
    color = color,
    sortOrder = sortOrder,
    isSystem = isSystem
)
