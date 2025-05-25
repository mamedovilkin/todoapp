package io.github.mamedovilkin.todoapp

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import io.github.mamedovilkin.auth.repository.AuthRepository
import io.github.mamedovilkin.auth.repository.AuthRepositoryImpl
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.database.repository.DataStoreRepositoryImpl
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.repository.TaskRepositoryImpl
import io.github.mamedovilkin.database.room.TaskDao
import io.github.mamedovilkin.database.room.TaskDatabase
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepositoryImpl
import io.github.mamedovilkin.todoapp.ui.screen.home.HomeViewModel
import io.github.mamedovilkin.todoapp.ui.screen.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class ToDoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        startKoin {
            androidContext(this@ToDoApp)
            modules(
                // App
                module {
                    single<TaskReminderRepository> {
                        val context = androidContext()
                        TaskReminderRepositoryImpl(context)
                    }
                    viewModel { HomeViewModel(get(), get(), get(), get()) }
                    viewModel { SettingsViewModel(get(), get()) }
                },
                // Auth
                module {
                    single<FirebaseAuth> { Firebase.auth }
                    single<AuthRepository> {
                        val context = androidContext()
                        val serverClientId = context.getString(R.string.default_web_client_id)

                        AuthRepositoryImpl(
                            context = context,
                            serverClientId = serverClientId,
                            auth = get()
                        )
                    }
                },
                // Database
                module {
                    single<TaskDao> {
                        val context = androidContext()
                        TaskDatabase.getDatabase(context).taskDao()
                    }
                    single<TaskRepository> { TaskRepositoryImpl(get()) }
                    single<DataStore<Preferences>> {
                        val context = androidContext()

                        PreferenceDataStoreFactory.create {
                            context.preferencesDataStoreFile("todoapp_preferences")
                        }
                    }
                    single<DataStoreRepository> { DataStoreRepositoryImpl(get()) }
                }
            )
        }
    }
}