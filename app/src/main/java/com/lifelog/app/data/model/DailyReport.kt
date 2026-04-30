package com.lifelog.app.data.model

data class DailyReport(
    val date: String,
    val totalSeconds: Int,
    val recordCount: Int,
    val categories: List<CategoryTimeEntry>,
    val timelineRecords: List<RecordWithDetails>,
    val moodSummary: Map<String, Int>,
    val topCategory: String?,
    val summaryText: String
)

data class CategoryTimeEntry(
    val categoryId: Long,
    val name: String,
    val icon: String,
    val color: String,
    val totalSeconds: Int,
    val percent: Float
)
