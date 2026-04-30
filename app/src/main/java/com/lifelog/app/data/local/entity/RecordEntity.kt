package com.lifelog.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "records",
    foreignKeys = [ForeignKey(
        entity = ActivityEntity::class,
        parentColumns = ["id"],
        childColumns = ["activityId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("activityId"), Index("startTime")]
)
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val activityId: Long,
    val startTime: Long,
    val endTime: Long? = null,
    val durationSeconds: Int = 0,
    val note: String = "",
    val mood: String? = null,
    val tags: String = "[]",
    val recordType: String = "timer",
    val createdAt: Long = System.currentTimeMillis()
)
