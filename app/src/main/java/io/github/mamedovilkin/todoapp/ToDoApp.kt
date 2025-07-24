package io.github.mamedovilkin.todoapp

import android.app.Application
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.vk.id.VKID
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.database.repository.DataStoreRepositoryImpl
import io.github.mamedovilkin.database.repository.FirestoreRepository
import io.github.mamedovilkin.database.repository.FirestoreRepositoryImpl
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.repository.TaskRepositoryImpl
import io.github.mamedovilkin.database.room.TaskDao
import io.github.mamedovilkin.database.room.TaskDatabase
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepositoryImpl
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepositoryImpl
import io.github.mamedovilkin.todoapp.ui.activity.home.HomeActivityViewModel
import io.github.mamedovilkin.todoapp.ui.activity.premium.PremiumActivityViewModel
import io.github.mamedovilkin.todoapp.ui.activity.settings.SettingsActivityViewModel
import io.github.mamedovilkin.todoapp.ui.screen.home.HomeViewModel
import io.github.mamedovilkin.todoapp.ui.screen.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.rustore.sdk.billingclient.RuStoreBillingClient
import ru.rustore.sdk.billingclient.RuStoreBillingClientFactory
import ru.rustore.sdk.billingclient.presentation.BillingClientTheme
import ru.rustore.sdk.billingclient.provider.BillingClientThemeProvider

class ToDoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        VKID.init(this)

        startKoin {
            androidContext(this@ToDoApp)
            modules(
                module {
                    // RuStore BillingClient
                    single<RuStoreBillingClient> { RuStoreBillingClientFactory.create(
                        context = androidContext(),
                        consoleApplicationId = "2063629026",
                        deeplinkScheme = "io.github.mamedovilkin.todoapp.scheme",
                        themeProvider = object : BillingClientThemeProvider {
                            override fun provide(): BillingClientTheme {
                                val currentNightMode = androidContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                                return if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                                    BillingClientTheme.Dark
                                } else {
                                    BillingClientTheme.Light
                                }
                            }
                        },
                    ) }

                    // Firebase
                    single<FirebaseFirestore> { Firebase.firestore }

                    // Room
                    single<TaskDao> { TaskDatabase.getDatabase(androidContext()).taskDao() }

                    // DataStore
                    single<DataStore<Preferences>> {
                        PreferenceDataStoreFactory.create {
                            androidContext().preferencesDataStoreFile("todoapp_preferences")
                        }
                    }

                    // Repository
                    single<TaskReminderRepository> { TaskReminderRepositoryImpl(androidContext(), get()) }
                    single<SyncWorkerRepository> { SyncWorkerRepositoryImpl(androidContext()) }
                    single<TaskRepository> { TaskRepositoryImpl(get()) }
                    single<DataStoreRepository> { DataStoreRepositoryImpl(get()) }
                    single<FirestoreRepository> { FirestoreRepositoryImpl(this@ToDoApp, get()) }

                    // ViewModel
                    viewModel { HomeActivityViewModel(this@ToDoApp, get(), get(), get(), get()) }
                    viewModel { HomeViewModel(this@ToDoApp, get(), get(), get(), get(), get()) }
                    viewModel { PremiumActivityViewModel(this@ToDoApp, get(), get(), get(), get()) }
                    viewModel { SettingsActivityViewModel(this@ToDoApp, get(), get(), get(), get(), get(), get()) }
                    viewModel { SettingsViewModel(get(), get(), get(), get(), get()) }
                }
            )
        }
    }
}