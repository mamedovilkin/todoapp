package io.github.mamedovilkin.todoapp.di

import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepositoryImpl
import io.github.mamedovilkin.todoapp.ui.screen.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<TaskReminderRepository> { TaskReminderRepositoryImpl(androidContext()) }
    viewModel { HomeViewModel(get(), get()) }
}