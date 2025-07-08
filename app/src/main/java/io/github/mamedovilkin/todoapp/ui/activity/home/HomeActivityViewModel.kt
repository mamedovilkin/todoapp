package io.github.mamedovilkin.todoapp.ui.activity.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.rustore.sdk.billingclient.RuStoreBillingClient
import ru.rustore.sdk.billingclient.model.product.ProductType
import ru.rustore.sdk.billingclient.model.purchase.PurchaseState
import ru.rustore.sdk.billingclient.usecase.PurchasesUseCase

class HomeActivityViewModel(
    private val application: Application,
    private val taskRepository: TaskRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val taskReminderRepository: TaskReminderRepository,
    private val ruStoreBillingClient: RuStoreBillingClient
) : AndroidViewModel(application) {

    init {
        viewModelScope.launch {
            val wasFirstLaunch = dataStoreRepository.wasFirstLaunch.first()

            if (!wasFirstLaunch) {
                val tasks = taskRepository.tasks.first()

                tasks.forEach { task ->
                    taskReminderRepository.scheduleReminder(task)
                }

                dataStoreRepository.setWasFirstLaunch(true)
            }
        }
    }

    fun checkPremiumAvailability(onError: (String?) -> Unit) = viewModelScope.launch {
        val userID = dataStoreRepository.userID.first()

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
                        viewModelScope.launch {
                            setPremium(false)
                        }
                        onError(application.getString(R.string.not_authorized_in_rustore))
                    }
                }
                .addOnFailureListener { error ->
                    onError(error.message)
                }
        }
    }

    fun setPremium(isPremium: Boolean) = viewModelScope.launch {
        dataStoreRepository.setPremium(isPremium)
    }
}