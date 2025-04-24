package io.github.mamedovilkin.todoapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
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
import dagger.hilt.android.AndroidEntryPoint
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.ui.screens.HomeScreen
import io.github.mamedovilkin.todoapp.ui.theme.ToDoAppTheme

@AndroidEntryPoint
class ToDoAppActivity : ComponentActivity() {

    private val notificationPermissionRequestCode = 101

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
                    notificationPermissionRequestCode
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        if (requestCode == notificationPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                    getString(R.string.notification_permission_granted), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,
                    getString(R.string.notification_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

            ToDoAppTheme {
                ToDoApp(windowWidthSizeClass = windowSizeClass.widthSizeClass)
            }
        }
    }
}

@Composable
fun ToDoApp(
    windowWidthSizeClass: WindowWidthSizeClass
) {
    Surface {
        HomeScreen(windowWidthSizeClass = windowWidthSizeClass)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToDoAppTheme {
        ToDoApp(windowWidthSizeClass = WindowWidthSizeClass.Compact)
    }
}