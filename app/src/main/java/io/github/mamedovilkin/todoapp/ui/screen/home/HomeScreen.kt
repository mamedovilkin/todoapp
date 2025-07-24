package io.github.mamedovilkin.todoapp.ui.screen.home

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.ui.common.EditTaskBottomSheet
import io.github.mamedovilkin.todoapp.ui.common.NewTaskBottomSheet
import io.github.mamedovilkin.todoapp.ui.common.NewTaskFloatingActionButton
import io.github.mamedovilkin.todoapp.ui.common.TaskList
import io.github.mamedovilkin.todoapp.ui.common.ToDoAppTopBar
import io.github.mamedovilkin.todoapp.ui.common.UpFloatingActionButton
import io.github.mamedovilkin.todoapp.ui.common.isScrollingUp
import io.github.mamedovilkin.todoapp.ui.screen.state.ErrorScreen
import io.github.mamedovilkin.todoapp.ui.screen.state.LoadingScreen
import io.github.mamedovilkin.todoapp.ui.screen.state.NoTasksScreen
import io.github.mamedovilkin.todoapp.ui.theme.ToDoAppTheme
import io.github.mamedovilkin.todoapp.util.APP_LINK
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    windowWidthSizeClass: WindowWidthSizeClass,
    windowHeightSizeClass: WindowHeightSizeClass,
    shouldOpenNewTaskDialog: Boolean = false,
    shouldOpenEditTaskDialog: Boolean = false,
    task: Task? = null,
    viewModel: HomeViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val newTaskSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val editTaskSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val showUpFloatingActionButton by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val exception = uiState.exception
    val isLatestVersion by viewModel.isLatestVersion.collectAsState()
    val userID by viewModel.userID.collectAsState()
    val photoURL by viewModel.photoURL.collectAsState()
    val displayName by viewModel.displayName.collectAsState()
    val showStatistics by viewModel.showStatistics.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setShowNewTaskBottomSheet(shouldOpenNewTaskDialog)
        viewModel.setShowEditTaskBottomSheet(shouldOpenEditTaskDialog)
        task?.let { viewModel.setTaskToEdit(it) }
        viewModel.observeTasks()

        exception?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = it.message.toString(),
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        topBar = {
            ToDoAppTopBar(
                userID = userID,
                photoURL = photoURL
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) { snackbarData ->
            val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
                if (it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd) {
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
                true
            })

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {},
            ) {
                Snackbar(snackbarData)
            }
        }
        },
        floatingActionButton = {
            NewTaskFloatingActionButton(
                expanded = lazyListState.isScrollingUp(),
                onNewTaskClick = {
                    viewModel.setShowNewTaskBottomSheet(true)
                },
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        when (val result = uiState.result) {
            is Result.Failure -> {
                ErrorScreen(message = result.error.message.toString())
            }
            is Result.Success -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    TaskList(
                        displayName = displayName,
                        showStatistics = showStatistics,
                        isPremium = isPremium,
                        innerPadding = innerPadding,
                        lazyListState = lazyListState,
                        tasks = result.tasks,
                        count = uiState.notDoneTasksCount,
                        query = uiState.query,
                        showVerticalGradient = showUpFloatingActionButton,
                        selectedCategory = uiState.selectedCategory,
                        categories = result.categories,
                        selectedPriority = uiState.selectedPriority,
                        onSelection = { viewModel.setSelectedCategory(it) },
                        onEdit = {
                            viewModel.setTaskToEdit(it)
                            viewModel.setShowEditTaskBottomSheet(true)
                        },
                        onSearch = {
                            viewModel.searchForTasks(it)
                        },
                        onClear = { viewModel.searchForTasks("") },
                        onPriority = { viewModel.setSelectedPriority(it) },
                        onToggle = { viewModel.toggleTask(it.copy(isDone = !it.isDone)) },
                        onDelete = {
                            viewModel.deleteTask(it)

                            coroutineScope.launch {
                                val snackBar = snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.task_deleted),
                                    actionLabel = context.getString(R.string.undo),
                                    duration = SnackbarDuration.Short
                                )

                                when (snackBar) {
                                    SnackbarResult.ActionPerformed -> {
                                        viewModel.newTask(it.copy(
                                            isSynced = false,
                                            updatedAt = System.currentTimeMillis()
                                        ))
                                    }
                                    SnackbarResult.Dismissed -> {}
                                }
                            }
                        },
                        modifier = if (windowWidthSizeClass == WindowWidthSizeClass.Compact) {
                            Modifier.fillMaxSize()
                        } else {
                            Modifier
                                .width(600.dp)
                                .fillMaxHeight()
                        },
                    )
                    AnimatedVisibility(
                        showUpFloatingActionButton,
                        enter = slideInVertically { it },
                        exit = slideOutVertically { it }
                    ) {
                        UpFloatingActionButton(
                            onUpClick = {
                                coroutineScope.launch {
                                    lazyListState.animateScrollToItem(index = 0)
                                }
                            }
                        )
                    }
                }
            }
            is Result.NoTasks -> {
                NoTasksScreen()
            }
            else -> {
                LoadingScreen()
            }
        }

        if (uiState.showNewTaskBottomSheet) {
            NewTaskBottomSheet(
                sheetState = newTaskSheetState,
                isPremium = isPremium,
                categories = (uiState.result as? Result.Success)?.categories ?: emptySet(),
                onSave = {
                    viewModel.newTask(it)
                    viewModel.setShowNewTaskBottomSheet(false)
                },
                onCancel = {
                    viewModel.setShowNewTaskBottomSheet(false)
                },
                windowHeightSizeClass = windowHeightSizeClass
            )
        }

        if (uiState.showEditTaskBottomSheet && uiState.task != null) {
            EditTaskBottomSheet(
                task = uiState.task!!,
                isPremium = isPremium,
                categories = (uiState.result as? Result.Success)?.categories ?: emptySet(),
                sheetState = editTaskSheetState,
                onSave = {
                    viewModel.updateTask(it.copy(isDone = if (System.currentTimeMillis() > it.datetime) it.isDone else false))
                    viewModel.setShowEditTaskBottomSheet(false)
                },
                onDelete = { viewModel.deleteTask(it) },
                onCancel = {
                    viewModel.setShowEditTaskBottomSheet(false)
                },
                windowHeightSizeClass = windowHeightSizeClass
            )
        }

        if (!isLatestVersion) {
            AlertDialog(
                onDismissRequest = {},
                title = {
                    Text(
                        text = stringResource(R.string.update_title),
                        style = MaterialTheme.typography.displayMedium
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.update_description),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, APP_LINK.toUri()))
                    }) {
                        Text(stringResource(R.string.update).uppercase())
                    }
                },
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    ToDoAppTheme {
        HomeScreen(
            windowWidthSizeClass = WindowWidthSizeClass.Compact,
            windowHeightSizeClass = WindowHeightSizeClass.Expanded
        )
    }
}