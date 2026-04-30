package com.lifelog.app.domain.util

object TimeUtils {

    fun formatTime(seconds: Int): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return "%02d:%02d:%02d".format(h, m, s)
    }

    fun formatDuration(seconds: Int): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        return when {
            h > 0 -> "${h}h ${m}min"
            m > 0 -> "${m}min"
            else -> "${seconds}s"
        }
    }

    fun formatDurationShort(seconds: Int): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        return when {
            h > 0 -> "${h}:${"%02d".format(m)}"
            else -> "0:${"%02d".format(m)}"
        }
    }

    fun formatHoursMinutes(seconds: Int): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        return "${h}h${m}m"
    }

    fun secondsToMinutes(seconds: Int): Int = seconds / 60

    fun minutesToSeconds(minutes: Int): Int = minutes * 60
}
