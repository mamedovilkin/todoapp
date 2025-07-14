package io.github.mamedovilkin.todoapp.ui.activity.settings

import android.app.Application
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
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rustore.sdk.billingclient.RuStoreBillingClient
import ru.rustore.sdk.billingclient.model.product.ProductType
import ru.rustore.sdk.billingclient.model.purchase.PurchaseState
import ru.rustore.sdk.billingclient.usecase.PurchasesUseCase

class SettingsActivityViewModel(
    private val application: Application,
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
                                    val subscriptionToken = firestoreRepository.getSubscriptionToken(userID)
                                    val hasPremium = purchases.any { purchase ->
                                        purchase.productType == ProductType.SUBSCRIPTION
                                        && purchase.purchaseState == PurchaseState.CONFIRMED
                                        && purchase.subscriptionToken == subscriptionToken
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
    }

    private fun clearUser() = viewModelScope.launch {
        dataStoreRepository.setUserID("")
        dataStoreRepository.setPhotoURL("")
        dataStoreRepository.setDisplayName("")
        dataStoreRepository.setPremium(false)
    }
}