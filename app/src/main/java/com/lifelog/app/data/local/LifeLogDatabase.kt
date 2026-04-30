package com.lifelog.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lifelog.app.data.local.converter.Converters
import com.lifelog.app.data.local.dao.ActivityDao
import com.lifelog.app.data.local.dao.CategoryDao
import com.lifelog.app.data.local.dao.GoalDao
import com.lifelog.app.data.local.dao.RecordDao
import com.lifelog.app.data.local.entity.ActivityEntity
import com.lifelog.app.data.local.entity.CategoryEntity
import com.lifelog.app.data.local.entity.GoalEntity
import com.lifelog.app.data.local.entity.RecordEntity

@Database(
    entities = [
        CategoryEntity::class,
        ActivityEntity::class,
        RecordEntity::class,
        GoalEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LifeLogDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun activityDao(): ActivityDao
    abstract fun recordDao(): RecordDao
    abstract fun goalDao(): GoalDao
}
