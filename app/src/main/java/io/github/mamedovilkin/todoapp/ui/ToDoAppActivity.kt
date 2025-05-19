package io.github.mamedovilkin.todoapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.github.mamedovilkin.todoapp.ui.screen.home.HomeScreen
import io.github.mamedovilkin.todoapp.ui.theme.ToDoAppTheme
import io.github.mamedovilkin.todoapp.util.NOTIFICATION_PERMISSION_REQUEST_CODE

class ToDoAppActivity : ComponentActivity() {

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
                    shouldOpenNewTaskDialog
                )
            }
        }
    }
}

@Composable
fun ToDoApp(
    windowWidthSizeClass: WindowWidthSizeClass,
    shouldOpenNewTaskDialog: Boolean = false
) {
    Surface {
        HomeScreen(
            windowWidthSizeClass = windowWidthSizeClass,
            shouldOpenNewTaskDialog = shouldOpenNewTaskDialog
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToDoAppTheme {
        ToDoApp(windowWidthSizeClass = WindowWidthSizeClass.Compact)
    }
}