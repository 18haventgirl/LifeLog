package com.lifelog.app.data.repository

import com.lifelog.app.data.local.dao.ActivityDao
import com.lifelog.app.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface ActivityRepository {
    fun getAll(): Flow<List<ActivityEntity>>
    suspend fun getAllOnce(): List<ActivityEntity>
    suspend fun getById(id: Long): ActivityEntity?
    fun getByCategoryId(categoryId: Long): Flow<List<ActivityEntity>>
    suspend fun getByCategoryIdOnce(categoryId: Long): List<ActivityEntity>
    suspend fun insert(activity: ActivityEntity): Long
    suspend fun update(activity: ActivityEntity)
    suspend fun delete(activity: ActivityEntity)
    suspend fun insertAll(activities: List<ActivityEntity>)
    suspend fun getByNameAndCategory(name: String, categoryId: Long): ActivityEntity?
}

@Singleton
class ActivityRepositoryImpl @Inject constructor(
    private val activityDao: ActivityDao
) : ActivityRepository {

    override fun getAll(): Flow<List<ActivityEntity>> = activityDao.getAll()

    override suspend fun getAllOnce(): List<ActivityEntity> = activityDao.getAllOnce()

    override suspend fun getById(id: Long): ActivityEntity? = activityDao.getById(id)

    override fun getByCategoryId(categoryId: Long): Flow<List<ActivityEntity>> =
        activityDao.getByCategoryId(categoryId)

    override suspend fun getByCategoryIdOnce(categoryId: Long): List<ActivityEntity> =
        activityDao.getByCategoryIdOnce(categoryId)

    override suspend fun insert(activity: ActivityEntity): Long = activityDao.insert(activity)

    override suspend fun update(activity: ActivityEntity) = activityDao.update(activity)

    override suspend fun delete(activity: ActivityEntity) = activityDao.delete(activity)

    override suspend fun insertAll(activities: List<ActivityEntity>) = activityDao.insertAll(activities)

    override suspend fun getByNameAndCategory(name: String, categoryId: Long): ActivityEntity? =
        activityDao.getByNameAndCategory(name, categoryId)
}
