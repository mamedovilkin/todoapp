package io.github.mamedovilkin.todoapp.ui.activity.settings

import android.app.Application
import android.provider.CalendarContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vk.id.AccessToken
import com.vk.id.VKID
import com.vk.id.VKIDAuthFail
import com.vk.id.auth.VKIDAuthCallback
import com.vk.id.logout.VKIDLogoutCallback
import com.vk.id.logout.VKIDLogoutFail
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.database.repository.FirestoreRepository
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rustore.sdk.billingclient.RuStoreBillingClient
import ru.rustore.sdk.billingclient.model.product.ProductType
import ru.rustore.sdk.billingclient.model.purchase.PurchaseState
import ru.rustore.sdk.billingclient.usecase.PurchasesUseCase
import java.util.UUID

class SettingsActivityViewModel(
    private val application: Application,
    private val taskRepository: TaskRepository,
    private val taskReminderRepository: TaskReminderRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val firestoreRepository: FirestoreRepository,
    private val syncWorkerRepository: SyncWorkerRepository,
    private val ruStoreBillingClient: RuStoreBillingClient
) : AndroidViewModel(application) {

    val userID = dataStoreRepository.userID
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ""
        )

    fun signInWithVK(onError: (String?) -> Unit) = viewModelScope.launch {
        VKID.instance.authorize(object : VKIDAuthCallback {
            override fun onAuth(accessToken: AccessToken) {
                val userID = accessToken.userID.toString()
                val photoURL = accessToken.userData.photo200.toString()
                val displayName = accessToken.userData.firstName

                saveUser(userID, photoURL, displayName)
                checkPremiumAvailability(userID, onError = {
                    onError(it)
                })
            }

            override fun onFail(fail: VKIDAuthFail) {
                onError(fail.description)
            }
        })
    }

    private fun checkPremiumAvailability(
        userID: String,
        onError: (String?) -> Unit
    ) = viewModelScope.launch {
        if (userID.isNotEmpty()) {
            ruStoreBillingClient.userInfo.getAuthorizationStatus()
                .addOnSuccessListener {
                    if (it.authorized) {
                        val purchasesUseCase: PurchasesUseCase = ruStoreBillingClient.purchases

                        purchasesUseCase.getPurchases()
                            .addOnSuccessListener { purchases ->
                                viewModelScope.launch {
                                    val hasPremium = purchases.any { purchase ->
                                        purchase.productType == ProductType.SUBSCRIPTION
                                        && purchase.purchaseState == PurchaseState.CONFIRMED
                                        && (purchase.productId == "premium_monthly" || purchase.productId == "premium_annual")
                                    }
                                    setPremium(hasPremium)
                                }
                            }
                            .addOnFailureListener { error ->
                                onError(error.message)
                            }
                    } else {
                        setPremium(false)
                        onError(application.getString(R.string.not_authorized_in_rustore))
                    }
                }
                .addOnFailureListener { error ->
                    onError(error.message)
                }
        }
    }

    fun signOut(onError: (String?) -> Unit) = viewModelScope.launch {
        VKID.instance.logout(object : VKIDLogoutCallback {
            override fun onSuccess() {
                clearUser()
            }

            override fun onFail(fail: VKIDLogoutFail) {
                onError(fail.description)
            }
        })
    }

    fun setPremium(isPremium: Boolean) = viewModelScope.launch {
        dataStoreRepository.setPremium(isPremium)

        if (isPremium) {
            syncWorkerRepository.scheduleSyncToggleTasksWork()
        } else {
            syncWorkerRepository.cancelScheduleSyncToggleTasksWork()
        }
    }

    private fun saveUser(
        userID: String,
        photoURL: String,
        displayName: String,
    ) = viewModelScope.launch {
        dataStoreRepository.setUserID(userID)
        dataStoreRepository.setPhotoURL(photoURL)
        dataStoreRepository.setDisplayName(displayName)
        firestoreRepository.setLastSignIn(userID)
        syncWorkerRepository.scheduleSyncTasksWork()
        syncWorkerRepository.scheduleSyncPeriodicTasksWork()
    }

    private fun clearUser() = viewModelScope.launch {
        dataStoreRepository.setUserID("")
        dataStoreRepository.setPhotoURL("")
        dataStoreRepository.setDisplayName("")
        dataStoreRepository.setPremium(false)
    }

    fun getTasksFromCalendar() {
        val tasks = mutableListOf<Task>()

        val projection = arrayOf(
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
        )

        val selection = "${CalendarContract.Events.DTSTART} >= ?"
        val now = System.currentTimeMillis().toString()
        val selectionArgs = arrayOf(now)

        val cursor = application.contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${CalendarContract.Events.DTSTART} ASC"
        )

        cursor?.use {
            val titleIndex = it.getColumnIndex(CalendarContract.Events.TITLE)
            val descriptionIndex = it.getColumnIndex(CalendarContract.Events.DESCRIPTION)
            val startIndex = it.getColumnIndex(CalendarContract.Events.DTSTART)

            viewModelScope.launch {
                while (it.moveToNext()) {
                    val title = it.getString(titleIndex) ?: application.getString(R.string.unknown)
                    val description = it.getString(descriptionIndex) ?: ""
                    val datetime = it.getLong(startIndex)

                    tasks.add(
                        Task(
                            id = UUID.randomUUID().toString(),
                            title = title,
                            description = description,
                            datetime = datetime
                        )
                    )
                }
            }
        }

        viewModelScope.launch {
            tasks.forEach { task ->
                var newTask = task

                if (newTask.datetime != 0L) {
                    newTask = taskReminderRepository.scheduleReminder(newTask)
                }

                taskRepository.insert(newTask)
                syncWorkerRepository.scheduleSyncTasksWork()
            }
        }
    }
}