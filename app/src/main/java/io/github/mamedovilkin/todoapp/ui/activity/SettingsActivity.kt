package io.github.mamedovilkin.todoapp.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.net.toUri
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.ui.screen.settings.SettingsScreen
import io.github.mamedovilkin.todoapp.ui.theme.ToDoAppTheme
import io.github.mamedovilkin.todoapp.util.APP_LINK
import io.github.mamedovilkin.todoapp.util.FEEDBACK_EMAIL
import io.github.mamedovilkin.todoapp.util.WEBSITE_LINK

class SettingsActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val packageInfo = packageManager.getPackageInfo(packageName, 0)

        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

            ToDoAppTheme {
                SettingsScreen(
                    windowWidthSizeClass = windowSizeClass.widthSizeClass,
                    context = this,
                    version = packageInfo.versionName.toString(),
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onFeedback = {
                        val subject = getString(R.string.app_name)
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = "mailto:$FEEDBACK_EMAIL?subject=$subject".toUri()
                        }
                        startActivity(intent)
                    },
                    onRateUs = {
                        val intent = Intent(Intent.ACTION_VIEW, APP_LINK.toUri())
                        startActivity(intent)
                    },
                    onTellFriend = {
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, getString(R.string.tell_a_friend_text))
                            type = "text/plain"
                        }

                        val shareIntent = Intent.createChooser(intent, null)
                        startActivity(shareIntent)
                    },
                    onAboutDeveloper = {
                        val intent = Intent(Intent.ACTION_VIEW, WEBSITE_LINK.toUri())
                        startActivity(intent)
                    }
                )
            }
        }
    }
}