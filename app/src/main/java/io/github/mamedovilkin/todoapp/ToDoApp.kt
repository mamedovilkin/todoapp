package io.github.mamedovilkin.todoapp

import android.app.Application
import io.github.mamedovilkin.todoapp.di.appModule
import io.github.mamedovilkin.todoapp.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ToDoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ToDoApp)
            modules(appModule, databaseModule)
        }
    }
}