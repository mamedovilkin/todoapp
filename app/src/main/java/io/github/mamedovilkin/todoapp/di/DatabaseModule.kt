package io.github.mamedovilkin.todoapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.repository.TaskRepositoryImpl
import io.github.mamedovilkin.database.room.TaskDao
import io.github.mamedovilkin.database.room.TaskDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTaskDao(@ApplicationContext context: Context): TaskDao {
        return TaskDatabase.getDatabase(context).taskDao()
    }

    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository {
        return TaskRepositoryImpl(taskDao)
    }
}