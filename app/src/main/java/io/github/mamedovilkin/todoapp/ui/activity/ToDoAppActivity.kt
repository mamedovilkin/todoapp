package io.github.mamedovilkin.todoapp.ui.activity

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
import androidx.core.content.ContextCompat
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import io.github.mamedovilkin.todoapp.ui.screen.home.HomeScreen
import io.github.mamedovilkin.todoapp.ui.theme.ToDoAppTheme
import io.github.mamedovilkin.todoapp.util.NOTIFICATION_PERMISSION_REQUEST_CODE
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ToDoAppActivity : ComponentActivity(), KoinComponent {

    private val syncWorkerRepository: SyncWorkerRepository by inject()

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

        syncWorkerRepository.scheduleSyncTasksWork()
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val shouldOpenNewTaskDialog = intent?.getStringExtra("action") == "new_task"

            ToDoAppTheme {
                ToDoApp(
                    windowWidthSizeClass = windowSizeClass.widthSizeClass,
                    windowHeightSizeClass = windowSizeClass.heightSizeClass,
                    shouldOpenNewTaskDialog
                )
            }
        }
    }
}

@Composable
fun ToDoApp(
    windowWidthSizeClass: WindowWidthSizeClass,
    windowHeightSizeClass: WindowHeightSizeClass,
    shouldOpenNewTaskDialog: Boolean = false
) {
    Surface {
        HomeScreen(
            windowWidthSizeClass = windowWidthSizeClass,
            windowHeightSizeClass = windowHeightSizeClass,
            shouldOpenNewTaskDialog = shouldOpenNewTaskDialog
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