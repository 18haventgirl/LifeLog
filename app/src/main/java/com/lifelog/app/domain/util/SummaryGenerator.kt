package com.lifelog.app.domain.util

object SummaryGenerator {

    fun generateDailySummary(
        totalSeconds: Int,
        topCategoryName: String?,
        recordCount: Int,
        moodSummary: Map<String, Int> = emptyMap()
    ): String {
        if (totalSeconds == 0) return "今天还没有记录，试着开始记录你的时间吧！💪"

        val totalHours = totalSeconds / 3600f

        val sb = StringBuilder()

        when {
            totalHours >= 12 -> sb.append("今天记录了${String.format("%.1f", totalHours)}小时，真是太充实了！🎉")
            totalHours >= 6 -> sb.append("今天记录了${String.format("%.1f", totalHours)}小时，很不错的一天！👍")
            totalHours >= 2 -> sb.append("今天记录了${String.format("%.1f", totalHours)}小时，继续保持！✨")
            else -> sb.append("今天记录了${String.format("%.1f", totalHours)}小时，慢慢来，记录就是进步！📝")
        }

        if (topCategoryName != null) {
            sb.append("\n\n今天时间花在「$topCategoryName」最多。")
        }

        if (moodSummary.isNotEmpty()) {
            val topMood = moodSummary.maxByOrNull { it.value }
            if (topMood != null) {
                sb.append("\n今天的心情以${topMood.key}为主。")
            }
        }

        return sb.toString()
    }

    fun generateWeeklySummary(
        totalSeconds: Int,
        dailyAverages: Int,
        topCategoryName: String?
    ): String {
        if (totalSeconds == 0) return "本周还没有记录，新的一周从记录开始！"

        val totalHours = totalSeconds / 3600f
        val avgHours = dailyAverages / 3600f

        return buildString {
            append("本周共记录${String.format("%.1f", totalHours)}小时，")
            append("日均${String.format("%.1f", avgHours)}小时。")
            if (topCategoryName != null) {
                append("\n本周时间主要花在「$topCategoryName」上。")
            }
        }
    }
}
