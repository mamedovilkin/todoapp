package io.github.mamedovilkin.todoapp.ui.activity.premium

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.todoapp.R
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rustore.sdk.billingclient.RuStoreBillingClient
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult
import ru.rustore.sdk.billingclient.usecase.PurchasesUseCase
import java.util.UUID

class PremiumActivityViewModel(
    private val application: Application,
    private val dataStoreRepository: DataStoreRepository,
    private val ruStoreBillingClient: RuStoreBillingClient
) : AndroidViewModel(application) {

    val isPremium = dataStoreRepository.isPremium
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    fun subscribe(
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) = viewModelScope.launch {
        ruStoreBillingClient.userInfo.getAuthorizationStatus()
            .addOnSuccessListener {
                if (it.authorized) {
                    val purchasesUseCase: PurchasesUseCase = ruStoreBillingClient.purchases
                    purchasesUseCase.purchaseProduct(
                        productId = "premium_monthly",
                        orderId = UUID.randomUUID().toString(),
                        quantity = 1,
                        developerPayload = null,
                    ).addOnSuccessListener { paymentResult ->
                        viewModelScope.launch {
                            when (paymentResult) {
                                PaymentResult.InvalidPaymentState -> {
                                    dataStoreRepository.setPremium(false)
                                    onError(application.getString(R.string.invalid_payment_state))
                                }
                                is PaymentResult.Cancelled -> {
                                    dataStoreRepository.setPremium(false)
                                    onError(application.getString(R.string.cancelled))
                                }
                                is PaymentResult.Failure -> {
                                    dataStoreRepository.setPremium(false)
                                    onError(application.getString(R.string.failure))
                                }
                                is PaymentResult.Success -> {
                                    purchasesUseCase.getPurchases()
                                        .addOnSuccessListener { purchases ->
                                            viewModelScope.launch {
                                                val hasPremium = purchases.any { it.productId == "premium_monthly" }
                                                dataStoreRepository.setPremium(hasPremium)
                                                onSuccess()
                                            }
                                        }
                                        .addOnFailureListener { error ->
                                            viewModelScope.launch {
                                                dataStoreRepository.setPremium(false)
                                            }
                                            onError(error.message.toString())
                                        }
                                }
                            }
                        }
                    }.addOnFailureListener { error ->
                        viewModelScope.launch {
                            dataStoreRepository.setPremium(false)
                        }
                        onError(error.message.toString())
                    }
                } else {
                    onError(application.getString(R.string.not_authorized_in_rustore))
                }
            }
            .addOnFailureListener { error ->
                onError(error.message)
            }
    }
}