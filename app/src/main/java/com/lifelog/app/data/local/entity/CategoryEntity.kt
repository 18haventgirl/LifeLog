package com.lifelog.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String = "\uD83D\uDCC1",
    val color: String = "#6B7280",
    val sortOrder: Int = 0,
    val isSystem: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
