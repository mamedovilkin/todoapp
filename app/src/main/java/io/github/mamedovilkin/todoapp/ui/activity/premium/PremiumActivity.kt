package io.github.mamedovilkin.todoapp.ui.activity.premium

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.ui.screen.premium.PremiumScreen
import io.github.mamedovilkin.todoapp.ui.theme.ToDoAppTheme
import io.github.mamedovilkin.todoapp.util.isInternetAvailable
import io.github.mamedovilkin.todoapp.util.toast
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.rustore.sdk.core.util.RuStoreUtils

class PremiumActivity : ComponentActivity(), KoinComponent {

    private val premiumActivityViewModel: PremiumActivityViewModel by inject()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val userID: String by premiumActivityViewModel.userID.collectAsState()
            val isPremium by premiumActivityViewModel.isPremium.collectAsState()

            ToDoAppTheme {
                PremiumScreen(
                    windowWidthSizeClass = windowSizeClass.widthSizeClass,
                    onBack = {
                        onBackPressedDispatcher.onBackPressed()
                    },
                    onTryItFree = { productId ->
                        lifecycleScope.launch {
                            if (isInternetAvailable()) {
                                if (RuStoreUtils.isRuStoreInstalled(this@PremiumActivity)) {
                                    if (userID.isEmpty()) {
                                        premiumActivityViewModel.signInWithVK { error ->
                                            if (error != null) {
                                                toast(error)
                                            }
                                        }
                                    } else {
                                        premiumActivityViewModel.subscribe(
                                            userID = userID,
                                            productId = productId,
                                            onSuccess = {
                                                finish()
                                            },
                                            onError = { error ->
                                                toast(error)
                                            }
                                        )
                                    }
                                } else {
                                    premiumActivityViewModel.setPremium(false)
                                    toast(getString(R.string.rustore_is_not_installed_on_this_device))
                                }
                            }
                        }
                    },
                    isPremium = isPremium
                )
            }
        }
    }
}