package io.github.mamedovilkin.todoapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
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
import io.github.mamedovilkin.todoapp.ui.screen.settings.SettingsScreen
import io.github.mamedovilkin.todoapp.ui.theme.ToDoAppTheme
import io.github.mamedovilkin.todoapp.util.APP_LINK
import io.github.mamedovilkin.todoapp.util.FEEDBACK_EMAIL
import io.github.mamedovilkin.todoapp.util.WEBSITE_LINK
import io.github.mamedovilkin.todoapp.util.isInternetAvailable
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import java.lang.ref.WeakReference

class SettingsActivity : ComponentActivity(), KoinComponent {

    private val dataStoreRepository: DataStoreRepository by inject()
    private val firestoreRepository: FirestoreRepository by inject()
    private val syncWorkerRepository: SyncWorkerRepository by inject()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityRef = WeakReference(this)

        val vkidAuthCallback = object : VKIDAuthCallback {
            override fun onAuth(accessToken: AccessToken) {
                val userID = accessToken.userID
                val user = accessToken.userData

                activityRef.get()?.lifecycleScope?.launch {
                    dataStoreRepository.setUserID(userID.toString())
                    dataStoreRepository.setPhotoURL(user.photo200.toString())
                    dataStoreRepository.setDisplayName(user.firstName)
                    firestoreRepository.setLastSignIn(userID.toString())
                    syncWorkerRepository.scheduleSyncTasksWork()
                }
            }

            override fun onFail(fail: VKIDAuthFail) {
                activityRef.get()?.toast(fail.description)
            }
        }

        val vkidLogoutCallback = object : VKIDLogoutCallback {
            override fun onSuccess() {
                activityRef.get()?.lifecycleScope?.launch {
                    dataStoreRepository.setUserID("")
                    dataStoreRepository.setPhotoURL("")
                    dataStoreRepository.setDisplayName("")
                }
            }

            override fun onFail(fail: VKIDLogoutFail) {
                activityRef.get()?.toast(fail.description)
            }
        }

        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val packageInfo = packageManager.getPackageInfo(packageName, 0)

            ToDoAppTheme {
                SettingsScreen(
                    windowWidthSizeClass = windowSizeClass.widthSizeClass,
                    version = packageInfo.versionName.toString(),
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onSignIn = {
                        lifecycleScope.launch {
                            if (isInternetAvailable()) {
                                VKID.instance.authorize(vkidAuthCallback)
                            } else {
                                toast(getString(R.string.no_internet_connection))
                            }
                        }
                    },
                    onSignOut = {
                        lifecycleScope.launch {
                            if (isInternetAvailable()) {
                                VKID.instance.logout(vkidLogoutCallback)
                            } else {
                                toast(getString(R.string.no_internet_connection))
                            }
                        }
                    },
                    onFeedback = { sendFeedback() },
                    onRateUs = { rateUs() },
                    onTellFriend = { tellFriend() },
                    onAboutDeveloper = { openDeveloperWebsite() }
                )
            }
        }
    }

    private fun toast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun sendFeedback() {
        val subject = getString(R.string.app_name)
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:$FEEDBACK_EMAIL?subject=$subject".toUri()
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