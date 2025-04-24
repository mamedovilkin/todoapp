package io.github.mamedovilkin.todoapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.mamedovilkin.todoapp.data.repository.TaskReminderRepository
import io.github.mamedovilkin.todoapp.data.repository.TaskReminderRepositoryImpl
import io.github.mamedovilkin.todoapp.data.repository.TaskRepository
import io.github.mamedovilkin.todoapp.data.repository.TaskRepositoryImpl
import io.github.mamedovilkin.todoapp.data.room.TaskDao
import io.github.mamedovilkin.todoapp.data.room.TaskDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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

    @Provides
    @Singleton
    fun provideTaskReminderRepository(@ApplicationContext context: Context): TaskReminderRepository {
        return TaskReminderRepositoryImpl(context)
    }
}