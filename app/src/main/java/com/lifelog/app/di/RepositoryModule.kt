package com.lifelog.app.di

import com.lifelog.app.data.repository.ActivityRepository
import com.lifelog.app.data.repository.ActivityRepositoryImpl
import com.lifelog.app.data.repository.CategoryRepository
import com.lifelog.app.data.repository.CategoryRepositoryImpl
import com.lifelog.app.data.repository.GoalRepository
import com.lifelog.app.data.repository.GoalRepositoryImpl
import com.lifelog.app.data.repository.RecordRepository
import com.lifelog.app.data.repository.RecordRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindActivityRepository(impl: ActivityRepositoryImpl): ActivityRepository

    @Binds
    @Singleton
    abstract fun bindRecordRepository(impl: RecordRepositoryImpl): RecordRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(impl: GoalRepositoryImpl): GoalRepository
}
