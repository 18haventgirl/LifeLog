package com.lifelog.app.data.repository

import com.lifelog.app.data.local.dao.ActivityDao
import com.lifelog.app.data.local.dao.CategoryDao
import com.lifelog.app.data.local.entity.CategoryEntity
import com.lifelog.app.data.model.CategoryWithActivities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

interface CategoryRepository {
    fun getAll(): Flow<List<CategoryEntity>>
    suspend fun getAllOnce(): List<CategoryEntity>
    suspend fun getById(id: Long): CategoryEntity?
    suspend fun getByName(name: String): CategoryEntity?
    fun getAllWithActivities(): Flow<List<CategoryWithActivities>>
    suspend fun insert(category: CategoryEntity): Long
    suspend fun update(category: CategoryEntity)
    suspend fun delete(category: CategoryEntity)
}

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val activityDao: ActivityDao
) : CategoryRepository {

    override fun getAll(): Flow<List<CategoryEntity>> = categoryDao.getAll()

    override suspend fun getAllOnce(): List<CategoryEntity> = categoryDao.getAllOnce()

    override suspend fun getById(id: Long): CategoryEntity? = categoryDao.getById(id)

    override suspend fun getByName(name: String): CategoryEntity? = categoryDao.getByName(name)

    override fun getAllWithActivities(): Flow<List<CategoryWithActivities>> {
        return combine(categoryDao.getAll(), activityDao.getAll()) { categories, activities ->
            categories.map { cat ->
                CategoryWithActivities(
                    id = cat.id,
                    name = cat.name,
                    icon = cat.icon,
                    color = cat.color,
                    sortOrder = cat.sortOrder,
                    isSystem = cat.isSystem,
                    activities = activities.filter { it.categoryId == cat.id }
                )
            }
        }
    }

    override suspend fun insert(category: CategoryEntity): Long = categoryDao.insert(category)

    override suspend fun update(category: CategoryEntity) = categoryDao.update(category)

    override suspend fun delete(category: CategoryEntity) = categoryDao.delete(category)
}
