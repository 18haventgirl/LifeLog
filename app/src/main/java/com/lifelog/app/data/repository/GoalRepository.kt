package com.lifelog.app.data.repository

import com.lifelog.app.data.local.dao.GoalDao
import com.lifelog.app.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface GoalRepository {
    fun getActiveGoals(): Flow<List<GoalEntity>>
    fun getAll(): Flow<List<GoalEntity>>
    suspend fun getById(id: Long): GoalEntity?
    suspend fun getByActivityId(activityId: Long): GoalEntity?
    suspend fun getByCategoryId(categoryId: Long): GoalEntity?
    suspend fun insert(goal: GoalEntity): Long
    suspend fun update(goal: GoalEntity)
    suspend fun delete(goal: GoalEntity)
    suspend fun deleteById(id: Long)
}

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {

    override fun getActiveGoals(): Flow<List<GoalEntity>> = goalDao.getActiveGoals()
    override fun getAll(): Flow<List<GoalEntity>> = goalDao.getAll()
    override suspend fun getById(id: Long): GoalEntity? = goalDao.getById(id)
    override suspend fun getByActivityId(activityId: Long): GoalEntity? = goalDao.getByActivityId(activityId)
    override suspend fun getByCategoryId(categoryId: Long): GoalEntity? = goalDao.getByCategoryId(categoryId)
    override suspend fun insert(goal: GoalEntity): Long = goalDao.insert(goal)
    override suspend fun update(goal: GoalEntity) = goalDao.update(goal)
    override suspend fun delete(goal: GoalEntity) = goalDao.delete(goal)
    override suspend fun deleteById(id: Long) = goalDao.deleteById(id)
}
