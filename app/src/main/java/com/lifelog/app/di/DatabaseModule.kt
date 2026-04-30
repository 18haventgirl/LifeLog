package com.lifelog.app.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lifelog.app.data.local.LifeLogDatabase
import com.lifelog.app.data.local.dao.ActivityDao
import com.lifelog.app.data.local.dao.CategoryDao
import com.lifelog.app.data.local.dao.GoalDao
import com.lifelog.app.data.local.dao.RecordDao
import com.lifelog.app.data.local.entity.ActivityEntity
import com.lifelog.app.data.local.entity.CategoryEntity
import com.lifelog.app.data.model.PresetData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        categoryDaoProvider: Provider<CategoryDao>,
        activityDaoProvider: Provider<ActivityDao>
    ): LifeLogDatabase {
        return Room.databaseBuilder(
            context,
            LifeLogDatabase::class.java,
            "lifelog.db"
        )
            .addCallback(object : androidx.room.RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        seedPresetData(categoryDaoProvider.get(), activityDaoProvider.get())
                    }
                }
            })
            .build()
    }

    @Provides
    fun provideCategoryDao(db: LifeLogDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideActivityDao(db: LifeLogDatabase): ActivityDao = db.activityDao()

    @Provides
    fun provideRecordDao(db: LifeLogDatabase): RecordDao = db.recordDao()

    @Provides
    fun provideGoalDao(db: LifeLogDatabase): GoalDao = db.goalDao()

    private suspend fun seedPresetData(
        categoryDao: CategoryDao,
        activityDao: ActivityDao
    ) {
        PresetData.categories.forEachIndexed { index, preset ->
            val categoryId = categoryDao.insert(
                CategoryEntity(
                    name = preset.name,
                    icon = preset.icon,
                    color = preset.color,
                    sortOrder = index,
                    isSystem = true
                )
            )
            preset.activities.forEachIndexed { activityIndex, activityName ->
                activityDao.insert(
                    ActivityEntity(
                        name = activityName,
                        categoryId = categoryId,
                        icon = preset.icon,
                        sortOrder = activityIndex
                    )
                )
            }
        }
    }
}
