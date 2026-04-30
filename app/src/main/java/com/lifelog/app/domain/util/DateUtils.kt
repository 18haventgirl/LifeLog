package com.lifelog.app.domain.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

object DateUtils {

    fun todayRange(): Pair<Long, Long> {
        val today = LocalDate.now()
        val start = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return start to end
    }

    fun dayRange(date: LocalDate): Pair<Long, Long> {
        val start = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return start to end
    }

    fun weekRange(date: LocalDate): Pair<Long, Long> {
        val monday = date.with(java.time.DayOfWeek.MONDAY)
        val sunday = monday.plusDays(7)
        val start = monday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = sunday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return start to end
    }

    fun monthRange(year: Int, month: Int): Pair<Long, Long> {
        val start = LocalDate.of(year, month, 1)
        val end = start.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1)
        val startMillis = start.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = end.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return startMillis to endMillis
    }

    fun epochToLocalDate(millis: Long): LocalDate {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun epochToLocalDateTime(millis: Long): LocalDateTime {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    fun localDateTimeToEpoch(dateTime: LocalDateTime): Long {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun formatDate(millis: Long, pattern: String = "yyyy-MM-dd"): String {
        val dt = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
        return dt.format(DateTimeFormatter.ofPattern(pattern))
    }

    fun formatDateChinese(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("yyyy年M月d日 EEEE", java.util.Locale.CHINA))
    }
}
