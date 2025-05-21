package io.github.mamedovilkin.todoapp.ui.common

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.ui.theme.ToDoAppTheme
import io.github.mamedovilkin.todoapp.util.APP_LINK
import io.github.mamedovilkin.todoapp.util.convertMillisToDate
import io.github.mamedovilkin.todoapp.util.convertMillisToDatetime
import io.github.mamedovilkin.todoapp.util.convertToTime
import io.github.mamedovilkin.todoapp.util.isExpired
import java.util.Calendar
import androidx.core.net.toUri
import io.github.mamedovilkin.todoapp.util.FEEDBACK_EMAIL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoAppTopBar(modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(R.string.app_name),
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(end = 16.dp)
                )
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .testTag("Menu")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.menu),
                        tint = MaterialTheme.colorScheme.background,
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.feedback)) },
                            onClick = {
                                val subject = context.getString(R.string.app_name)
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = "mailto:$FEEDBACK_EMAIL?subject=$subject".toUri()
                                }

                                context.startActivity(intent)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.rate_us)) },
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, APP_LINK.toUri())
                                context.startActivity(intent)
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun ToDoAppTopBarPreview() {
    ToDoAppTheme {
        ToDoAppTopBar()
    }
}

@Composable
fun UpFloatingActionButton(
    onUpClick: () -> Unit,
) {
    SmallFloatingActionButton(
        onClick = onUpClick,
        shape = CircleShape,
        modifier = Modifier
            .navigationBarsPadding()
            .padding(16.dp)
            .testTag(stringResource(R.string.up))
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowUpward,
            contentDescription = stringResource(R.string.up)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UpFloatingActionButtonPreview() {
    ToDoAppTheme {
        UpFloatingActionButton(onUpClick = {})
    }
}

@Composable
fun NewTaskFloatingActionButton(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onNewTaskClick: () -> Unit
) {
    FloatingActionButton(onClick = onNewTaskClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.new_task)
            )
            AnimatedVisibility(expanded) {
                Text(
                    text = stringResource(R.string.new_task),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewTaskFloatingActionButtonPreview() {
    ToDoAppTheme {
        NewTaskFloatingActionButton(expanded = true, onNewTaskClick = {})
    }
}

@Composable
fun TaskItem(
    task: Task,
    onEdit: (Task) -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = { onEdit(task) })
            .testTag("Task")
    ) {
        Checkbox(
            checked = task.isDone,
            onCheckedChange = { onToggle() },
            modifier = Modifier
                .testTag("Toggle")
        )
        Text(
            text = task.title,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .testTag("Title"),
            style = if (task.isDone) {
                MaterialTheme.typography.headlineMedium.copy(textDecoration = TextDecoration.LineThrough)
            } else {
                MaterialTheme.typography.headlineMedium
            },
            color = if (task.isDone) Color(0xFF808080) else MaterialTheme.colorScheme.onBackground
        )
        AnimatedVisibility(task.isDone) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag(stringResource(R.string.delete))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskItemPreview() {
    ToDoAppTheme {
        TaskItem(
            task = Task(title = "Clean my room up"),
            onEdit = {},
            onToggle = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskItemDonePreview() {
    ToDoAppTheme {
        TaskItem(
            task = Task(title = "Clean my room up", isDone = true),
            onEdit = {},
            onToggle = {},
            onDelete = {}
        )
    }
}

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@Composable
fun StatisticsCard(
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (count == 0) Color(0x8061D498) else Color(0x80EFB712),
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = if (count == 0) painterResource(R.drawable.ic_done) else painterResource(R.drawable.ic_warning),
                contentDescription = if (count == 0) stringResource(R.string.done) else stringResource(R.string.warning),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .offset((-18).dp)
                    .size(124.dp)
                    .alpha(0.5F)
            )
            Column(
                modifier = Modifier
                    .offset((-18).dp)
                    .weight(1F),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (count == 0) stringResource(R.string.all_tasks_completed) else pluralStringResource(R.plurals.you_have_tasks_to_do, count, count),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (count == 0) colorResource(R.color.colorDone) else colorResource(R.color.colorWarning),
                    textAlign = TextAlign.Center
                )
                if (count == 0) {
                    val congratulations = stringArrayResource(R.array.congratulations)
                    Text(
                        text = congratulations[congratulations.indices.random()],
                        style = MaterialTheme.typography.titleSmall,
                        color = colorResource(R.color.colorDone),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatisticsCardPreview() {
    ToDoAppTheme {
        StatisticsCard(count = 10)
    }
}

@Preview(showBackground = true)
@Composable
private fun StatisticsCardDonePreview() {
    ToDoAppTheme {
        StatisticsCard(count = 0)
    }
}

@Composable
fun StickySearchBar(
    query: String,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
) {
    TextField(
        value = query,
        onValueChange = { onSearch(it) },
        placeholder = {
            Text(
                text = stringResource(R.string.search),
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.search)
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                query.isNotEmpty(),
                enter = slideInHorizontally { (it) / 3 } + fadeIn(),
                exit = slideOutHorizontally { (it) / 3 } + fadeOut(),
            ) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.testTag("Clear")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.clear)
                    )
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = CircleShape,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun StickySearchBarPreview() {
    ToDoAppTheme {
        StickySearchBar(
            query = "",
            onSearch = {},
            onClear = {}
        )
    }
}

@Composable
fun TaskList(
    innerPadding: PaddingValues,
    lazyListState: LazyListState,
    tasks: List<Task>,
    count: Int,
    query: String,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onEdit: (Task) -> Unit,
    onToggle: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .padding(innerPadding)
            .testTag("Tasks List"),
        state = lazyListState,
        contentPadding = PaddingValues(top = 0.dp, bottom = 72.dp)
    ) {
        item {
            AnimatedContent(
                targetState = count
            ) { targetCount ->
                StatisticsCard(count = targetCount)
            }
        }
        stickyHeader {
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                StickySearchBar(
                    query = query,
                    onSearch = onSearch,
                    onClear = onClear,
                )
            }
        }
        items(tasks, key = { it.id }) { task ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(fadeInSpec = null, fadeOutSpec = null)
            ) {
                Column {
                    AnimatedVisibility(!task.isDone) {
                        Text(
                            text = convertMillisToDatetime(task.datetime, LocalContext.current),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (task.isExpired()) Color.Red else Color.Unspecified,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .padding(horizontal = 16.dp)
                        )
                    }
                    TaskItem(
                        task = task,
                        onEdit = { onEdit(it) },
                        onToggle = { onToggle(task) },
                        onDelete = { onDelete(task) },
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskBottomSheet(
    sheetState: SheetState,
    onSave: (Task) -> Unit,
    onCancel: () -> Unit,
    windowHeightSizeClass: WindowHeightSizeClass
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableLongStateOf(Calendar.getInstance().timeInMillis) }
    var hour by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MINUTE)) }

    ModalBottomSheet(
        onDismissRequest = onCancel,
        sheetState = sheetState
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                )
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                maxLines = 3,
                label = { Text(text = stringResource(R.string.new_task)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Title,
                        contentDescription = stringResource(R.string.new_task)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(stringResource(R.string.new_task))
            )
            DateTimePickerTextFields(
                date = date,
                hour = hour,
                minute = minute,
                onDateSelected = { date = it },
                onTimeSelected = { it1, it2 ->
                    hour = it1
                    minute = it2
                },
                windowHeightSizeClass = windowHeightSizeClass
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1F)
                ) { Text(stringResource(R.string.cancel)) }
                Button(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = date
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        onSave(Task(title = title, datetime = calendar.timeInMillis))
                        title = ""
                        date = 0L
                        hour = 0
                        minute = 0
                    },
                    enabled = title.isNotEmpty(),
                    modifier = Modifier.weight(1F)
                ) { Text(stringResource(R.string.save)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskBottomSheet(
    task: Task,
    sheetState: SheetState,
    onSave: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onCancel: () -> Unit,
    windowHeightSizeClass: WindowHeightSizeClass
) {
    var title by remember { mutableStateOf(task.title) }
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = task.datetime
    var date by remember { mutableLongStateOf(calendar.timeInMillis) }
    var hour by remember { mutableIntStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableIntStateOf(calendar.get(Calendar.MINUTE)) }


    ModalBottomSheet(
        onDismissRequest = onCancel,
        sheetState = sheetState
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                )
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                maxLines = 3,
                label = { Text(text = stringResource(R.string.edit_task)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Title,
                        contentDescription = stringResource(R.string.edit_task)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(stringResource(R.string.edit_task))
            )
            DateTimePickerTextFields(
                date = task.datetime,
                hour = calendar.get(Calendar.HOUR_OF_DAY),
                minute = calendar.get(Calendar.MINUTE),
                onDateSelected = { date = it },
                onTimeSelected = { it1, it2 ->
                    hour = it1
                    minute = it2
                },
                windowHeightSizeClass = windowHeightSizeClass
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1F)
                ) { Text(stringResource(R.string.cancel)) }
                FilledTonalButton(
                    onClick = {
                        onDelete(task)
                        onCancel()
                    },
                    modifier = Modifier.weight(1F)
                ) { Text(stringResource(R.string.delete)) }
                Button(
                    onClick = {
                        calendar.clear()
                        calendar.timeInMillis = date
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        onSave(task.copy(title = title, datetime = calendar.timeInMillis))
                    },
                    enabled = title.isNotEmpty(),
                    modifier = Modifier.weight(1F)
                ) { Text(stringResource(R.string.save)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerTextFields(
    date: Long,
    hour: Int,
    minute: Int,
    onDateSelected: (Long) -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    windowHeightSizeClass: WindowHeightSizeClass
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date,
        initialDisplayMode = if (windowHeightSizeClass == WindowHeightSizeClass.Compact) {
            DisplayMode.Input
        } else {
            DisplayMode.Picker
        }
    )
    val timePickerState = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute,
    )
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showTimeInput by remember { mutableStateOf(false) }
    val datePickerInteractionSource = remember { MutableInteractionSource() }
    val timePickerInteractionSource = remember { MutableInteractionSource() }

    LaunchedEffect(datePickerInteractionSource) {
        datePickerInteractionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                showDatePicker = true
            }
        }
    }

    LaunchedEffect(timePickerInteractionSource) {
        timePickerInteractionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                showTimePicker = true
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis ?: 0)
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = windowHeightSizeClass != WindowHeightSizeClass.Compact,
                title = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        text = stringResource(R.string.select_date),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                    selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                    todayDateBorderColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    headlineContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            switchButton = {
                IconButton(onClick = {
                    showTimeInput = !showTimeInput
                }) {
                    Icon(
                        imageVector = if (showTimeInput) Icons.Outlined.AccessTime  else Icons.Outlined.Keyboard,
                        contentDescription = stringResource(R.string.select_time)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onTimeSelected(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) {
                    Text(stringResource(R.string.ok))
                }
            }
        ) {
            if (showTimeInput) {
                TimeInput(state = timePickerState)
            } else {
                TimePicker(state = timePickerState)
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = datePickerState.selectedDateMillis?.let { convertMillisToDate(it, LocalContext.current) } ?: "",
            onValueChange = {},
            interactionSource = datePickerInteractionSource,
            modifier = Modifier
                .weight(1F)
                .testTag("Date"),
            label = { Text(stringResource(R.string.select_date)) },
            readOnly = true,
            singleLine = true,
            leadingIcon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = stringResource(R.string.calendar)
                )
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = convertToTime(timePickerState.hour, timePickerState.minute, LocalContext.current),
            onValueChange = {},
            interactionSource = timePickerInteractionSource,
            modifier = Modifier
                .weight(1F)
                .testTag("Time"),
            label = { Text(stringResource(R.string.select_time)) },
            readOnly = true,
            singleLine = true,
            leadingIcon = {
                Icon(
                    Icons.Filled.AccessTime,
                    contentDescription = stringResource(R.string.clock)
                )
            }
        )
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    switchButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit)? = null,
    confirmButton: @Composable (() -> Unit),
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = stringResource(R.string.select_time),
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    switchButton()
                    Spacer(modifier = Modifier.weight(1f))
                    dismissButton?.invoke()
                    confirmButton()
                }
            }
        }
    }
}