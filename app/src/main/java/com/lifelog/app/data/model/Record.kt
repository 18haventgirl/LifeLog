package com.lifelog.app.data.model

import com.lifelog.app.data.local.entity.RecordEntity
import java.time.Instant
import java.time.ZoneId

data class Record(
    val id: Long,
    val activityId: Long,
    val startTime: Long,
    val endTime: Long?,
    val durationSeconds: Int,
    val note: String,
    val mood: String?,
    val tags: List<String>,
    val recordType: String
)

data class RecordWithDetails(
    val id: Long,
    val activityName: String,
    val activityIcon: String,
    val categoryName: String,
    val categoryColor: String,
    val startTime: Long,
    val endTime: Long?,
    val durationSeconds: Int,
    val note: String,
    val mood: String?,
    val tags: List<String>,
    val recordType: String
) {
    val startHour: Int
        get() = Instant.ofEpochMilli(startTime).atZone(ZoneId.systemDefault()).hour

    val startMinute: Int
        get() = Instant.ofEpochMilli(startTime).atZone(ZoneId.systemDefault()).minute
}

fun RecordEntity.toDomain(tagsList: List<String> = emptyList()) = Record(
    id = id,
    activityId = activityId,
    startTime = startTime,
    endTime = endTime,
    durationSeconds = durationSeconds,
    note = note,
    mood = mood,
    tags = tagsList,
    recordType = recordType
)

data class ManualRecordData(
    val activityId: Long,
    val startTime: Long,
    val endTime: Long,
    val note: String = "",
    val mood: String? = null,
    val tags: List<String> = emptyList()
)
