package com.lifelog.app.data.local.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import com.lifelog.app.data.local.entity.ActivityEntity
import com.lifelog.app.data.local.entity.RecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {

    @Insert
    suspend fun insert(record: RecordEntity): Long

    @Update
    suspend fun update(record: RecordEntity)

    @Delete
    suspend fun delete(record: RecordEntity)

    @Query("SELECT * FROM records WHERE id = :id")
    suspend fun getById(id: Long): RecordEntity?

    @Query("SELECT * FROM records ORDER BY startTime DESC")
    suspend fun getAllRecords(): List<RecordEntity>

    @Query("SELECT * FROM records WHERE endTime IS NULL ORDER BY startTime DESC LIMIT 1")
    suspend fun getActiveRecord(): RecordEntity?

    @Query("SELECT * FROM records WHERE endTime IS NULL ORDER BY startTime DESC LIMIT 1")
    fun observeActiveRecord(): Flow<RecordEntity?>

    @Transaction
    @Query("""
        SELECT * FROM records
        WHERE startTime >= :dayStart AND startTime < :dayEnd
        ORDER BY startTime ASC
    """)
    fun getRecordsWithActivity(dayStart: Long, dayEnd: Long): Flow<List<RecordWithActivity>>

    @Query("""
        SELECT * FROM records
        WHERE startTime >= :dayStart AND startTime < :dayEnd
        ORDER BY startTime ASC
    """)
    suspend fun getRecordsByDay(dayStart: Long, dayEnd: Long): List<RecordEntity>

    @Query("""
        SELECT c.id as categoryId, c.name as categoryName, c.icon as categoryIcon,
               c.color as categoryColor, SUM(r.durationSeconds) as totalSeconds
        FROM records r
        INNER JOIN activities a ON r.activityId = a.id
        INNER JOIN categories c ON a.categoryId = c.id
        WHERE r.startTime >= :dayStart AND r.startTime < :dayEnd
            AND r.endTime IS NOT NULL
        GROUP BY c.id
        ORDER BY totalSeconds DESC
    """)
    suspend fun getCategorySummaryByDay(dayStart: Long, dayEnd: Long): List<CategorySummary>

    @Query("""
        SELECT a.id as activityId, a.name as activityName, a.icon as activityIcon,
               c.id as categoryId, c.name as categoryName, c.icon as categoryIcon,
               c.color as categoryColor,
               SUM(r.durationSeconds) as totalSeconds, COUNT(*) as count
        FROM records r
        INNER JOIN activities a ON r.activityId = a.id
        INNER JOIN categories c ON a.categoryId = c.id
        WHERE r.startTime >= :dayStart AND r.startTime < :dayEnd
            AND r.endTime IS NOT NULL
            AND c.id = :categoryId
        GROUP BY a.id
        ORDER BY totalSeconds DESC
    """)
    suspend fun getActivitySummaryByDay(
        dayStart: Long, dayEnd: Long, categoryId: Long
    ): List<ActivitySummary>

    @Query("""
        SELECT mood, COUNT(*) as count FROM records
        WHERE startTime >= :dayStart AND startTime < :dayEnd
            AND mood IS NOT NULL
        GROUP BY mood
    """)
    suspend fun getMoodSummary(dayStart: Long, dayEnd: Long): List<MoodCount>

    @Query("""
        SELECT DISTINCT a.id, a.name, a.icon, a.categoryId, c.name as categoryName,
               c.icon as categoryIcon, c.color as categoryColor
        FROM records r
        INNER JOIN activities a ON r.activityId = a.id
        INNER JOIN categories c ON a.categoryId = c.id
        ORDER BY r.startTime DESC
        LIMIT :limit
    """)
    suspend fun getRecentActivities(limit: Int = 5): List<RecentActivity>

    @Query("""
        SELECT SUM(durationSeconds) as totalSeconds, startTime
        FROM records
        WHERE startTime >= :monthStart AND startTime < :monthEnd
            AND endTime IS NOT NULL
        GROUP BY date(startTime / 1000, 'unixepoch', 'localtime')
        ORDER BY startTime ASC
    """)
    suspend fun getDailyTotalsForMonth(monthStart: Long, monthEnd: Long): List<DailyTotal>

    @Query("""
        SELECT c.id as categoryId, c.name as categoryName, c.icon as categoryIcon,
               c.color as categoryColor, SUM(r.durationSeconds) as totalSeconds
        FROM records r
        INNER JOIN activities a ON r.activityId = a.id
        INNER JOIN categories c ON a.categoryId = c.id
        WHERE r.startTime >= :weekStart AND r.startTime < :weekEnd
            AND r.endTime IS NOT NULL
        GROUP BY c.id
        ORDER BY totalSeconds DESC
    """)
    suspend fun getCategorySummaryByWeek(weekStart: Long, weekEnd: Long): List<CategorySummary>

    @Query("DELETE FROM records WHERE id = :id")
    suspend fun deleteById(id: Long)
}

// 关联查询结果类
data class RecordWithActivity(
    @Embedded val record: RecordEntity,
    @Relation(
        parentColumn = "activityId",
        entityColumn = "id"
    )
    val activity: ActivityEntity
)

// 聚合查询结果类
data class CategorySummary(
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val totalSeconds: Int
)

data class ActivitySummary(
    val activityId: Long,
    val activityName: String,
    val activityIcon: String,
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val totalSeconds: Int,
    val count: Int
)

data class MoodCount(
    val mood: String,
    val count: Int
)

data class RecentActivity(
    val id: Long,
    val name: String,
    val icon: String,
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String
)

data class DailyTotal(
    @ColumnInfo(name = "totalSeconds") val totalSeconds: Int,
    @ColumnInfo(name = "startTime") val startTime: Long
)
