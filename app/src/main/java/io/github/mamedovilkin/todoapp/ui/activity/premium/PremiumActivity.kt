package io.github.mamedovilkin.todoapp.ui.activity.premium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import io.github.mamedovilkin.todoapp.ui.screen.premium.PremiumScreen
import io.github.mamedovilkin.todoapp.ui.theme.ToDoAppTheme
import io.github.mamedovilkin.todoapp.util.toast
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PremiumActivity : ComponentActivity(), KoinComponent {

    private val premiumActivityViewModel: PremiumActivityViewModel by inject()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val isPremium by premiumActivityViewModel.isPremium.collectAsState()

            ToDoAppTheme {
                PremiumScreen(
                    windowWidthSizeClass = windowSizeClass.widthSizeClass,
                    onBack = {
                        onBackPressedDispatcher.onBackPressed()
                    },
                    onTryItFree = {
                        premiumActivityViewModel.subscribe(
                            onSuccess = {
                                finish()
                            },
                            onError = { error ->
                                toast(error)
                            }
                        )
                    },
                    isPremium = isPremium
                )
            }
        }
    }
}