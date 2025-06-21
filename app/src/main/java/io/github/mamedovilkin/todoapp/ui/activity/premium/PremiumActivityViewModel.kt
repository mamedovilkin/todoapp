package io.github.mamedovilkin.todoapp.ui.activity.premium

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
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
    private val ruStoreBillingClient: RuStoreBillingClient,
    private val syncWorkerRepository: SyncWorkerRepository
) : AndroidViewModel(application) {

    val isPremium = dataStoreRepository.isPremium
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
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
                                    setPremium(false)
                                    onError(application.getString(R.string.invalid_payment_state))
                                }
                                is PaymentResult.Cancelled -> {
                                    setPremium(false)
                                    onError(application.getString(R.string.cancelled))
                                }
                                is PaymentResult.Failure -> {
                                    setPremium(false)
                                    onError(application.getString(R.string.failure))
                                }
                                is PaymentResult.Success -> {
                                    purchasesUseCase.getPurchases()
                                        .addOnSuccessListener { purchases ->
                                            viewModelScope.launch {
                                                val hasPremium = purchases.any { purchase -> purchase.productId == "premium_monthly" }
                                                setPremium(hasPremium)
                                                onSuccess()
                                            }
                                        }
                                        .addOnFailureListener { error ->
                                            setPremium(false)
                                            onError(error.message.toString())
                                        }
                                }
                            }
                        }
                    }.addOnFailureListener { error ->
                        setPremium(false)
                        onError(error.message.toString())
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

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun setPremium(isPremium: Boolean) = viewModelScope.launch {
        dataStoreRepository.setPremium(isPremium)

        if (isPremium) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH

                val channel = NotificationChannel(
                    application.resources.getString(R.string.premium).uppercase(),
                    application.resources.getString(R.string.premium),
                    importance
                )

                channel.description = application.resources.getString(R.string.premium)

                val notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

                notificationManager?.createNotificationChannel(channel)
            }

            val builder = NotificationCompat.Builder(application, application.resources.getString(R.string.premium).uppercase())
                .setSmallIcon(R.drawable.ic_task)
                .setContentTitle(application.getString(R.string.premium))
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(application.getString(R.string.premium_notification))
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(LongArray(0))
                .setAutoCancel(true)

            NotificationManagerCompat.from(application).notify(2, builder.build())

            syncWorkerRepository.scheduleSyncToggleTasksWork()
        } else {
            syncWorkerRepository.cancelScheduleSyncToggleTasksWork()
        }
    }
}