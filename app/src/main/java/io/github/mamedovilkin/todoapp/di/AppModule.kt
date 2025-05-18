package io.github.mamedovilkin.todoapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.mamedovilkin.todoapp.reminder.TaskReminderRepository
import io.github.mamedovilkin.todoapp.reminder.TaskReminderRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTaskReminderRepository(@ApplicationContext context: Context): TaskReminderRepository {
        return TaskReminderRepositoryImpl(context)
    }
}