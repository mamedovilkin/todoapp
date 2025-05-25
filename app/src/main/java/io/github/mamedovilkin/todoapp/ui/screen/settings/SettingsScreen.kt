package io.github.mamedovilkin.todoapp.ui.screen.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.ui.common.Setting
import io.github.mamedovilkin.todoapp.ui.common.SettingsCategory
import io.github.mamedovilkin.todoapp.ui.common.SettingsTopBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SettingsScreen(
    windowWidthSizeClass: WindowWidthSizeClass,
    context: Context,
    version: String,
    onBack: () -> Unit,
    onFeedback: () -> Unit,
    onRateUs: () -> Unit,
    onTellFriend: () -> Unit,
    onAboutDeveloper: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser = uiState.currentUser
    val exception = uiState.exception

    LaunchedEffect(Unit) {
        viewModel.isSignedIn()
        viewModel.getShowStatistics()

        if (exception != null) {
            Toast.makeText(context, exception.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    if (uiState.showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowDialog(false) },
            title = {
                Text(
                    text = stringResource(R.string.sign_out),
                    style = MaterialTheme.typography.displayMedium
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.do_you_really_want_to_sign_out),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.signOut()
                    viewModel.setShowDialog(false)
                }) {
                    Text(stringResource(R.string.sign_out))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowDialog(false) }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }

    Scaffold(
        topBar = {
            SettingsTopBar(onBack = onBack)
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = if (windowWidthSizeClass == WindowWidthSizeClass.Compact) {
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                } else {
                    Modifier
                        .width(600.dp)
                        .padding(innerPadding)
                },
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    SettingsCategory(
                        category = stringResource(R.string.settings)
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    if (currentUser == null) {
                                        viewModel.signInWithGoogle()
                                    } else {
                                        viewModel.setShowDialog(true)
                                    }
                                }
                                .testTag(stringResource(R.string.google_account))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                if (currentUser == null || currentUser.photoUrl == null) {
                                    Icon(
                                        imageVector = Icons.Outlined.AccountCircle,
                                        contentDescription = stringResource(R.string.google_account),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(48.dp)
                                    )
                                } else {
                                    GlideImage(
                                        model = currentUser.photoUrl.toString(),
                                        contentDescription = stringResource(R.string.google_account),
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                    )
                                }
                                Column(
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = currentUser?.displayName ?: stringResource(R.string.google_account),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        style = MaterialTheme.typography.displayMedium
                                    )
                                    Text(
                                        text = currentUser?.email ?: stringResource(R.string.sign_in_to_your_google_account),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {}
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_statistics),
                                    contentDescription = stringResource(R.string.show_statistics),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column(
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.show_statistics),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }
                            }
                            Switch(
                                checked = uiState.showStatistics,
                                onCheckedChange = {
                                    viewModel.setShowStatistics(it)
                                },
                                colors = SwitchDefaults.colors(
                                    uncheckedTrackColor = MaterialTheme.colorScheme.background
                                ),
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .testTag("Show Statistics")
                            )
                        }
                    }
                }

                item {
                    SettingsCategory(
                        category = stringResource(R.string.about)
                    )
                }

                item {
                    Setting(
                        imageVector = Icons.Outlined.Feedback,
                        title = stringResource(R.string.feedback),
                        onClick = onFeedback
                    )
                }

                item {
                    Setting(
                        imageVector = Icons.Outlined.StarOutline,
                        title = stringResource(R.string.rate_us),
                        onClick = onRateUs
                    )
                }

                item {
                    Setting(
                        imageVector = Icons.Outlined.Share,
                        title = stringResource(R.string.tell_a_friend),
                        onClick = onTellFriend
                    )
                }

                item {
                    Setting(
                        imageVector = Icons.Outlined.Code,
                        title = stringResource(R.string.about_developer),
                        onClick = onAboutDeveloper
                    )
                }

                item {
                    Setting(
                        imageVector = Icons.Outlined.Info,
                        title = stringResource(R.string.version, version),
                        onClick = {},
                        showArrow = false
                    )
                }
            }
        }
    }
}