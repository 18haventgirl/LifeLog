package com.lifelog.app.data.model

data class WeeklyReport(
    val weekStart: String,
    val weekEnd: String,
    val totalSeconds: Int,
    val dailyBreakdown: List<DailyBreakdown>,
    val categorySummary: List<CategoryTimeEntry>,
    val comparisonWithLastWeek: Float?
)

data class DailyBreakdown(
    val dayLabel: String,
    val totalSeconds: Int,
    val categories: List<CategoryTimeEntry>
)
