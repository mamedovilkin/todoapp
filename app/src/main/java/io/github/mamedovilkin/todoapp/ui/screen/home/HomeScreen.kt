package io.github.mamedovilkin.todoapp.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.data.room.Task
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    windowWidthSizeClass: WindowWidthSizeClass,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val newTaskSheetState = rememberModalBottomSheetState()
    var showNewTaskBottomSheet by rememberSaveable { mutableStateOf(false) }
    val editTaskSheetState = rememberModalBottomSheetState()
    var showEditTaskBottomSheet by rememberSaveable { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    var task: Task? = null
    val coroutineScope = rememberCoroutineScope()
    val showUpFloatingActionButton by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = { ToDoAppTopBar() },
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
                    showNewTaskBottomSheet = true
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
                        innerPadding = innerPadding,
                        lazyListState = lazyListState,
                        tasks = result.tasks,
                        count = uiState.notDoneTasksCount,
                        query = uiState.query,
                        onEdit = {
                            task = it
                            showEditTaskBottomSheet = true
                        },
                        onSearch = {
                            viewModel.searchForTasks(it)
                        },
                        onClear = { viewModel.searchForTasks("") },
                        onToggle = { viewModel.toggleDone(it) },
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
                                        viewModel.newTask(it)
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
                        enter = slideInVertically {
                            it
                        },
                        exit = slideOutVertically {
                            it
                        }
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

        if (showNewTaskBottomSheet) {
            NewTaskBottomSheet(
                sheetState = newTaskSheetState,
                onSave = {
                    viewModel.newTask(it)
                    showNewTaskBottomSheet = false
                },
                onCancel = {
                    showNewTaskBottomSheet = false
                },
                windowWidthSizeClass = windowWidthSizeClass
            )
        }

        if (showEditTaskBottomSheet && task != null) {
            EditTaskBottomSheet(
                task = task!!,
                sheetState = editTaskSheetState,
                onSave = {
                    viewModel.updateTask(it)
                    showEditTaskBottomSheet = false
                },
                onDelete = { viewModel.deleteTask(it) },
                onCancel = {
                    showEditTaskBottomSheet = false
                },
                windowWidthSizeClass = windowWidthSizeClass
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    ToDoAppTheme {
        HomeScreen(windowWidthSizeClass = WindowWidthSizeClass.Compact)
    }
}