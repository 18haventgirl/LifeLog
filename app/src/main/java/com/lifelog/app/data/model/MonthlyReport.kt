package com.lifelog.app.data.model

data class MonthlyReport(
    val year: Int,
    val month: Int,
    val totalSeconds: Int,
    val activeDays: Int,
    val categorySummary: List<CategoryTimeEntry>,
    val dailyTotals: List<DailyHeatData>,
    val weeklyBreakdown: List<DailyBreakdown>
)

data class DailyHeatData(
    val date: String,
    val totalMinutes: Int,
    val level: Int // 0=无记录, 1=少量, 2=中等, 3=较多, 4=大量
)
