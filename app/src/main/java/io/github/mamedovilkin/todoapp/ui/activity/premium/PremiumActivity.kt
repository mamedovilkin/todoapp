package io.github.mamedovilkin.todoapp.ui.activity.premium

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import io.github.mamedovilkin.todoapp.ui.screen.premium.PremiumScreen
import io.github.mamedovilkin.todoapp.ui.theme.ToDoAppTheme
import io.github.mamedovilkin.todoapp.util.toast
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.rustore.sdk.billingclient.RuStoreBillingClient
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult
import ru.rustore.sdk.billingclient.usecase.PurchasesUseCase
import java.util.UUID

class PremiumActivity : ComponentActivity(), KoinComponent {

    private val ruStoreBillingClient: RuStoreBillingClient by inject()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        ruStoreBillingClient.onNewIntent(intent)
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            ruStoreBillingClient.onNewIntent(intent)
        }

        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

            ToDoAppTheme {
                PremiumScreen(
                    windowWidthSizeClass = windowSizeClass.widthSizeClass,
                    onBack = {
                        onBackPressedDispatcher.onBackPressed()
                    },
                    onTryItFree = {
                        val purchasesUseCase: PurchasesUseCase = ruStoreBillingClient.purchases
                        purchasesUseCase.purchaseProduct(
                            productId = "premium_monthly",
                            orderId = UUID.randomUUID().toString(),
                            quantity = 1,
                            developerPayload = null,
                        ).addOnSuccessListener { paymentResult ->
                            when (paymentResult) {
                                is PaymentResult.Cancelled -> toast("Cancelled")
                                is PaymentResult.Failure -> toast("Failure")
                                PaymentResult.InvalidPaymentState -> toast("Invalid Payment State")
                                is PaymentResult.Success -> toast("Success")
                            }
                        }.addOnFailureListener { error ->
                            Log.e("SettingActivity", error.message.toString())
                        }
                    }
                )
            }
        }
    }
}