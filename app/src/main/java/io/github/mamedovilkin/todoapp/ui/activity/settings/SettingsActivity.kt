package io.github.mamedovilkin.todoapp.ui.activity.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.ui.activity.premium.PremiumActivity
import io.github.mamedovilkin.todoapp.ui.screen.settings.SettingsScreen
import io.github.mamedovilkin.todoapp.ui.theme.ToDoAppTheme
import io.github.mamedovilkin.todoapp.util.APP_LINK
import io.github.mamedovilkin.todoapp.util.FEEDBACK_EMAIL
import io.github.mamedovilkin.todoapp.util.WEBSITE_LINK
import io.github.mamedovilkin.todoapp.util.isInternetAvailable
import io.github.mamedovilkin.todoapp.util.toast
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsActivity : ComponentActivity(), KoinComponent {

    private val settingsActivityViewModel: SettingsActivityViewModel by inject()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val userID: String by settingsActivityViewModel.userID.collectAsState()

            ToDoAppTheme {
                SettingsScreen(
                    windowWidthSizeClass = windowSizeClass.widthSizeClass,
                    version = packageInfo.versionName.toString(),
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onSignIn = {
                        lifecycleScope.launch {
                            if (isInternetAvailable()) {
                                settingsActivityViewModel.signInWithVK()
                            } else {
                                toast(getString(R.string.no_internet_connection))
                            }
                        }
                    },
                    onSignOut = {
                        lifecycleScope.launch {
                            if (isInternetAvailable()) {
                                settingsActivityViewModel.signOut { message ->
                                    message?.let {
                                        toast(it)
                                    }
                                }
                            } else {
                                toast(getString(R.string.no_internet_connection))
                            }
                        }
                    },
                    onPremium = {
                        lifecycleScope.launch {
                            if (userID.isEmpty()) {
                                if (isInternetAvailable()) {
                                    settingsActivityViewModel.signInWithVK()
                                } else {
                                    toast(getString(R.string.no_internet_connection))
                                }

                            } else {
                                startActivity(
                                    Intent(
                                        this@SettingsActivity,
                                        PremiumActivity::class.java
                                    )
                                )
                            }
                        }
                    },
                    onManageSubscription = { manageSubscription() },
                    onFeedback = { sendFeedback() },
                    onRateUs = { rateUs() },
                    onTellFriend = { tellFriend() },
                    onAboutDeveloper = { openDeveloperWebsite() }
                )
            }
        }
    }

    private fun manageSubscription() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = "rustore://profile/subscriptions".toUri()
        startActivity(intent)
    }

    private fun sendFeedback() {
        val subject = getString(R.string.app_name)
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:${FEEDBACK_EMAIL}?subject=$subject".toUri()
        }
        startActivity(intent)
    }

    private fun rateUs() {
        val intent = Intent(Intent.ACTION_VIEW, APP_LINK.toUri())
        startActivity(intent)
    }

    private fun tellFriend() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.tell_a_friend_text))
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(intent, null)
        startActivity(shareIntent)
    }

    private fun openDeveloperWebsite() {
        val intent = Intent(Intent.ACTION_VIEW, WEBSITE_LINK.toUri())
        startActivity(intent)
    }
}