package io.github.mamedovilkin.todoapp.di

import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.repository.TaskRepositoryImpl
import io.github.mamedovilkin.database.room.TaskDao
import io.github.mamedovilkin.database.room.TaskDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single<TaskDao> { TaskDatabase.getDatabase(androidContext()).taskDao() }
    single<TaskRepository> { TaskRepositoryImpl(get()) }
}