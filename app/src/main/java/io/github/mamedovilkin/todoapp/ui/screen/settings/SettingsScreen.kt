package io.github.mamedovilkin.todoapp.ui.screen.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import io.github.mamedovilkin.todoapp.ui.common.ToDoAppDialog
import io.github.mamedovilkin.todoapp.util.isInternetAvailable
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SettingsScreen(
    windowWidthSizeClass: WindowWidthSizeClass,
    version: String,
    onBack: () -> Unit,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onPremium: () -> Unit,
    onManageSubscription: () -> Unit,
    onFeedback: () -> Unit,
    onRateUs: () -> Unit,
    onTellFriend: () -> Unit,
    onAboutDeveloper: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val userID by viewModel.userID.collectAsState()
    val photoURL by viewModel.photoURL.collectAsState()
    val displayName by viewModel.displayName.collectAsState()
    val showStatistics by viewModel.showStatistics.collectAsState()

    if (uiState.showSignOutDialog) {
        ToDoAppDialog(
            title = stringResource(R.string.sign_out),
            text = stringResource(R.string.do_you_really_want_to_sign_out),
            onDismiss = { viewModel.setShowSignOutDialog(false) },
            onConfirm = onSignOut
        )
    }

    if (uiState.showDeleteAllDataDialog) {
        ToDoAppDialog(
            title = stringResource(R.string.delete_all_data),
            text = stringResource(R.string.do_you_really_want_to_delete_all_data),
            onDismiss = { viewModel.setShowDeleteAllDataDialog(false) },
            onConfirm = {
                coroutineScope.launch {
                    if (isInternetAvailable()) {
                        viewModel.deleteAllData()
                    } else {
                        Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            SettingsTopBar(onBack = onBack)
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
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
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (userID.isEmpty()) {
                                        onSignIn()
                                    } else {
                                        viewModel.setShowSignOutDialog(true)
                                    }
                                }
                                .testTag(stringResource(R.string.vk_id))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                if (userID.isEmpty() || photoURL.isEmpty()) {
                                    Icon(
                                        imageVector = Icons.Outlined.AccountCircle,
                                        contentDescription = stringResource(R.string.vk_id),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(48.dp)
                                    )
                                } else {
                                    GlideImage(
                                        model = photoURL.toString(),
                                        contentDescription = stringResource(R.string.vk_id),
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
                                        text = if (displayName.isEmpty()) stringResource(R.string.vk_id) else displayName,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        style = MaterialTheme.typography.displayMedium
                                    )
                                    Text(
                                        text = if (userID.isEmpty()) stringResource(R.string.sign_in_with_your_vk_id) else "ID: $userID",
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
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (false) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.premium),
                                    style = MaterialTheme.typography.displayMedium
                                )
                                FilledTonalButton(
                                    onClick = onManageSubscription,
                                    colors = ButtonDefaults.buttonColors(
                                        contentColor = MaterialTheme.colorScheme.primary,
                                        containerColor = MaterialTheme.colorScheme.background
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.manage_subscription),
                                        maxLines = 1
                                    )
                                }
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.free),
                                    style = MaterialTheme.typography.displayMedium
                                )
                                Button(onClick = onPremium) {
                                    Text(stringResource(R.string.go_premium))
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
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setShowDeleteAllDataDialog(true) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = stringResource(R.string.delete_all_data),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column(
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.delete_all_data),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        style = MaterialTheme.typography.headlineMedium
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
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {}
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(16.dp)
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
                                checked = showStatistics,
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
                        showEndIcon = false,
                        onClick = {}
                    )
                }
            }
        }
    }
}