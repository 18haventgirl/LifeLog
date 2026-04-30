package com.lifelog.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lifelog.app.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: ActivityEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<ActivityEntity>)

    @Update
    suspend fun update(activity: ActivityEntity)

    @Delete
    suspend fun delete(activity: ActivityEntity)

    @Query("SELECT * FROM activities ORDER BY sortOrder ASC, id ASC")
    fun getAll(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities ORDER BY sortOrder ASC, id ASC")
    suspend fun getAllOnce(): List<ActivityEntity>

    @Query("SELECT * FROM activities WHERE id = :id")
    suspend fun getById(id: Long): ActivityEntity?

    @Query("SELECT * FROM activities WHERE categoryId = :categoryId ORDER BY sortOrder ASC, id ASC")
    fun getByCategoryId(categoryId: Long): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE categoryId = :categoryId ORDER BY sortOrder ASC, id ASC")
    suspend fun getByCategoryIdOnce(categoryId: Long): List<ActivityEntity>

    @Query("SELECT * FROM activities WHERE isActive = 1 ORDER BY sortOrder ASC, id ASC")
    fun getActiveActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE name = :name AND categoryId = :categoryId LIMIT 1")
    suspend fun getByNameAndCategory(name: String, categoryId: Long): ActivityEntity?

    @Query("SELECT COUNT(*) FROM activities")
    suspend fun getCount(): Int
}
