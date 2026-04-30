package com.lifelog.app.data.repository

import com.google.gson.Gson
import com.lifelog.app.data.local.dao.CategorySummary
import com.lifelog.app.data.local.dao.DailyTotal
import com.lifelog.app.data.local.dao.MoodCount
import com.lifelog.app.data.local.dao.RecordDao
import com.lifelog.app.data.local.entity.RecordEntity
import com.lifelog.app.data.model.ManualRecordData
import com.lifelog.app.data.model.RecordWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface RecordRepository {
    fun observeActiveRecord(): Flow<RecordEntity?>
    suspend fun getActiveRecord(): RecordEntity?
    suspend fun startRecord(activityId: Long, startTime: Long, recordType: String): Long
    suspend fun finishRecord(recordId: Long, endTime: Long)
    suspend fun addManualRecord(data: ManualRecordData): Long
    fun getRecordsByDay(dayStart: Long, dayEnd: Long): Flow<List<RecordWithDetails>>
    suspend fun getCategorySummaryByDay(dayStart: Long, dayEnd: Long): List<CategorySummary>
    suspend fun getMoodSummary(dayStart: Long, dayEnd: Long): List<MoodCount>
    suspend fun getDailyTotalsForMonth(monthStart: Long, monthEnd: Long): List<DailyTotal>
    suspend fun getTodayRecordCount(dayStart: Long, dayEnd: Long): Int
    suspend fun deleteRecord(id: Long)
}

@Singleton
class RecordRepositoryImpl @Inject constructor(
    private val recordDao: RecordDao,
    private val activityDao: com.lifelog.app.data.local.dao.ActivityDao,
    private val categoryDao: com.lifelog.app.data.local.dao.CategoryDao
) : RecordRepository {

    private val gson = Gson()

    override fun observeActiveRecord(): Flow<RecordEntity?> = recordDao.observeActiveRecord()

    override suspend fun getActiveRecord(): RecordEntity? = recordDao.getActiveRecord()

    override suspend fun startRecord(
        activityId: Long,
        startTime: Long,
        recordType: String
    ): Long {
        return recordDao.insert(
            RecordEntity(
                activityId = activityId,
                startTime = startTime,
                endTime = null,
                durationSeconds = 0,
                recordType = recordType
            )
        )
    }

    override suspend fun finishRecord(recordId: Long, endTime: Long) {
        val record = recordDao.getById(recordId) ?: return
        val duration = ((endTime - record.startTime) / 1000).toInt()
        recordDao.update(record.copy(endTime = endTime, durationSeconds = duration))
    }

    override suspend fun addManualRecord(data: ManualRecordData): Long {
        val duration = ((data.endTime - data.startTime) / 1000).toInt()
        val record = RecordEntity(
            activityId = data.activityId,
            startTime = data.startTime,
            endTime = data.endTime,
            durationSeconds = duration,
            note = data.note,
            mood = data.mood,
            tags = gson.toJson(data.tags),
            recordType = "manual"
        )
        return recordDao.insert(record)
    }

    override fun getRecordsByDay(dayStart: Long, dayEnd: Long): Flow<List<RecordWithDetails>> {
        return recordDao.getRecordsWithActivity(dayStart, dayEnd).map { list ->
            list.map { raw ->
                val category = categoryDao.getById(raw.activity.categoryId)
                RecordWithDetails(
                    id = raw.record.id,
                    activityName = raw.activity.name,
                    activityIcon = raw.activity.icon,
                    categoryName = category?.name ?: "",
                    categoryColor = category?.color ?: "#6B7280",
                    startTime = raw.record.startTime,
                    endTime = raw.record.endTime,
                    durationSeconds = raw.record.durationSeconds,
                    note = raw.record.note,
                    mood = raw.record.mood,
                    tags = try {
                        gson.fromJson(raw.record.tags, Array<String>::class.java).toList()
                    } catch (_: Exception) {
                        emptyList()
                    },
                    recordType = raw.record.recordType
                )
            }
        }
    }

    override suspend fun getCategorySummaryByDay(
        dayStart: Long,
        dayEnd: Long
    ): List<CategorySummary> = recordDao.getCategorySummaryByDay(dayStart, dayEnd)

    override suspend fun getMoodSummary(
        dayStart: Long,
        dayEnd: Long
    ): List<MoodCount> = recordDao.getMoodSummary(dayStart, dayEnd)

    override suspend fun getDailyTotalsForMonth(
        monthStart: Long,
        monthEnd: Long
    ): List<DailyTotal> = recordDao.getDailyTotalsForMonth(monthStart, monthEnd)

    override suspend fun getTodayRecordCount(dayStart: Long, dayEnd: Long): Int {
        return recordDao.getRecordsByDay(dayStart, dayEnd).size
    }

    override suspend fun deleteRecord(id: Long) = recordDao.deleteById(id)
}
