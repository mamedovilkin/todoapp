package io.github.mamedovilkin.todoapp.ui.activity.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.ui.screen.home.HomeScreen
import io.github.mamedovilkin.todoapp.ui.theme.ToDoAppTheme
import io.github.mamedovilkin.todoapp.util.NOTIFICATION_ID
import io.github.mamedovilkin.todoapp.util.NOTIFICATION_PERMISSION_REQUEST_CODE
import io.github.mamedovilkin.todoapp.util.TASK_KEY
import io.github.mamedovilkin.todoapp.util.isInternetAvailable
import io.github.mamedovilkin.todoapp.util.toast
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.rustore.sdk.core.util.RuStoreUtils

class HomeActivity : ComponentActivity(), KoinComponent {

    private val homeActivityViewModel: HomeActivityViewModel by inject()

    override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }

        lifecycleScope.launch {
            if (isInternetAvailable()) {
                if (RuStoreUtils.isRuStoreInstalled(this@HomeActivity)) {
                    homeActivityViewModel.checkPremiumAvailability { error ->
                        if (error != null) {
                            toast(error)
                        }
                    }
                } else {
                    homeActivityViewModel.setPremium(false)
                    toast(getString(R.string.rustore_is_not_installed_on_this_device))
                }
            } else {
                toast(getString(R.string.no_internet_connection))
            }
        }
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val shouldOpenNewTaskDialog = intent?.getStringExtra("action") == "todoapp://new_task/"
            val shouldOpenEditTaskDialog = intent?.getStringExtra("action") == "todoapp://reschedule/"
            val task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(TASK_KEY, Task::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(TASK_KEY)
            }

            if (shouldOpenEditTaskDialog) {
                val notificationManager = NotificationManagerCompat.from(this)
                notificationManager.cancel(NOTIFICATION_ID)
            }

            ToDoAppTheme {
                ToDoApp(
                    windowWidthSizeClass = windowSizeClass.widthSizeClass,
                    windowHeightSizeClass = windowSizeClass.heightSizeClass,
                    shouldOpenNewTaskDialog = shouldOpenNewTaskDialog,
                    shouldOpenEditTaskDialog = shouldOpenEditTaskDialog,
                    task = task
                )
            }
        }
    }
}

@Composable
fun ToDoApp(
    windowWidthSizeClass: WindowWidthSizeClass,
    windowHeightSizeClass: WindowHeightSizeClass,
    shouldOpenNewTaskDialog: Boolean = false,
    shouldOpenEditTaskDialog: Boolean = false,
    task: Task? = null
) {
    Surface {
        HomeScreen(
            windowWidthSizeClass = windowWidthSizeClass,
            windowHeightSizeClass = windowHeightSizeClass,
            shouldOpenNewTaskDialog = shouldOpenNewTaskDialog,
            shouldOpenEditTaskDialog = shouldOpenEditTaskDialog,
            task = task
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToDoAppTheme {
        ToDoApp(
            windowWidthSizeClass = WindowWidthSizeClass.Compact,
            windowHeightSizeClass = WindowHeightSizeClass.Expanded
        )
    }
}