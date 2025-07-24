package io.github.mamedovilkin.todoapp.ui.common

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import io.github.mamedovilkin.database.room.PriorityType
import io.github.mamedovilkin.database.room.RepeatType
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.database.room.isExpired
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.ui.activity.premium.PremiumActivity
import io.github.mamedovilkin.todoapp.ui.activity.settings.SettingsActivity
import io.github.mamedovilkin.todoapp.ui.theme.ToDoAppTheme
import io.github.mamedovilkin.todoapp.ui.theme.greenColor
import io.github.mamedovilkin.todoapp.ui.theme.orangeColor
import io.github.mamedovilkin.todoapp.ui.theme.redColor
import io.github.mamedovilkin.todoapp.ui.theme.textColor
import io.github.mamedovilkin.todoapp.util.convertMillisToDate
import io.github.mamedovilkin.todoapp.util.convertMillisToDatetime
import io.github.mamedovilkin.todoapp.util.convertToTime
import io.github.mamedovilkin.todoapp.util.getDate
import io.github.mamedovilkin.todoapp.util.getGreeting
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun ToDoAppTopBar(
    modifier: Modifier = Modifier,
    userID: String,
    photoURL: String,
) {
    val context = LocalContext.current

    CenterAlignedTopAppBar(
        title = {
            Icon(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(R.string.app_name),
                tint = MaterialTheme.colorScheme.background
            )
        },
        actions = {
            IconButton(
                onClick = {
                    context.startActivity(
                        Intent(context, SettingsActivity::class.java)
                    )
                },
                modifier = Modifier.testTag(stringResource(R.string.settings))
            ) {
                if (userID.isEmpty() || photoURL.isEmpty()) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(R.string.settings),
                        tint = MaterialTheme.colorScheme.background,
                    )
                } else {
                    GlideImage(
                        model = photoURL,
                        contentDescription = stringResource(R.string.settings),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                    )
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
        ToDoAppTopBar(userID = "", photoURL = "")
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
    isPremium: Boolean,
    onEdit: (Task) -> Unit,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityTypes = stringArrayResource(R.array.priority_types)
    val priorityColor = remember(task.priorityType) {
        when (task.priorityType) {
            PriorityType.NONE -> Color.Unspecified
            PriorityType.LOW -> greenColor
            PriorityType.MEDIUM -> orangeColor
            PriorityType.HIGH -> redColor
        }
    }

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
            modifier = Modifier.testTag("Toggle")
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (task.category.isNotEmpty() && !task.isDone || task.priorityType != PriorityType.NONE) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    AnimatedVisibility(task.category.isNotEmpty() && !task.isDone) {
                        Text(
                            text = task.category,
                            maxLines = 1,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp)
                                .testTag(stringResource(R.string.category)),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    AnimatedVisibility(task.priorityType != PriorityType.NONE) {
                        Text(
                            text = priorityTypes[PriorityType.entries.indexOf(task.priorityType)].lowercase(),
                            maxLines = 1,
                            modifier = Modifier
                                .background(
                                    color = priorityColor,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp)
                                .testTag(stringResource(R.string.priority)),
                            fontSize = 12.sp,
                            color = Color.White,
                        )
                    }
                }
            }
            Text(
                text = task.title,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.testTag("Title"),
                style = if (task.isDone) {
                    MaterialTheme.typography.headlineMedium.copy(textDecoration = TextDecoration.LineThrough)
                } else {
                    MaterialTheme.typography.headlineMedium
                },
                color = if (task.isDone) Color.Gray else MaterialTheme.colorScheme.onPrimaryContainer
            )
            AnimatedVisibility(task.description.isNotEmpty() && !task.isDone) {
                Text(
                    text = task.description,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag(stringResource(R.string.description)),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Gray
                )
            }
            AnimatedVisibility(isPremium && task.isDone && task.repeatType != RepeatType.ONE_TIME) {
                Text(
                    text = stringResource(R.string.next, convertMillisToDatetime(task, LocalContext.current)),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Gray
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
            isPremium = false,
            onEdit = {},
            onToggle = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskItemDonePreview() {
    ToDoAppTheme {
        TaskItem(
            task = Task(title = "Clean my room up", isDone = true),
            isPremium = false,
            onEdit = {},
            onToggle = {}
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
    isPremium: Boolean,
    showVerticalGradient: Boolean,
    selectedPriority: PriorityType,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onPriority: (PriorityType) -> Unit,
) {
    var expandedPriority by remember { mutableStateOf(false) }
    val priorityFilter = stringArrayResource(R.array.priority_filter)

    Column {
        Surface(
            modifier = Modifier.background( MaterialTheme.colorScheme.background)
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
                        query.isEmpty() && isPremium,
                        enter = slideInHorizontally { (it) / 3 } + fadeIn(),
                        exit = slideOutHorizontally { (it) / 3 } + fadeOut(),
                    ) {
                        IconButton(
                            onClick = { expandedPriority = true },
                            modifier = Modifier.testTag(stringResource(R.string.priority))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FilterList,
                                contentDescription = stringResource(R.string.priority)
                            )
                        }
                        DropdownMenu(
                            expanded = expandedPriority,
                            onDismissRequest = { expandedPriority = false }
                        ) {
                            priorityFilter.forEach { item ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = item,
                                            maxLines = 1,
                                            style = if (item == priorityFilter[PriorityType.entries.indexOf(selectedPriority)]) {
                                                MaterialTheme.typography.displaySmall
                                            } else {
                                                MaterialTheme.typography.headlineSmall
                                            },
                                            color = if (item == priorityFilter[PriorityType.entries.indexOf(selectedPriority)]) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                Color.Unspecified
                                            }
                                        )
                                    },
                                    onClick = {
                                        onPriority(PriorityType.entries[priorityFilter.indexOf(item)])
                                        expandedPriority = false
                                    }
                                )
                            }
                        }
                    }
                    AnimatedVisibility(
                        query.isNotEmpty(),
                        enter = slideInHorizontally { (it) / 3 } + fadeIn(),
                        exit = slideOutHorizontally { (it) / 3 } + fadeOut(),
                    ) {
                        IconButton(
                            onClick = onClear,
                            modifier = Modifier.testTag(stringResource(R.string.clear))
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
        if (showVerticalGradient) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                Color.Transparent
                            ),
                        ),
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StickySearchBarPreview() {
    ToDoAppTheme {
        StickySearchBar(
            query = "",
            isPremium = true,
            showVerticalGradient = false,
            selectedPriority = PriorityType.NONE,
            onSearch = {},
            onClear = {},
            onPriority = {}
        )
    }
}

@Composable
fun TaskList(
    displayName: String,
    showStatistics: Boolean,
    isPremium: Boolean,
    innerPadding: PaddingValues,
    lazyListState: LazyListState,
    tasks: List<Task>,
    count: Int,
    query: String,
    showVerticalGradient: Boolean,
    selectedCategory: String,
    categories: Set<String>,
    selectedPriority: PriorityType,
    onSelection: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onPriority: (PriorityType) -> Unit,
    onEdit: (Task) -> Unit,
    onToggle: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPremiumAd by remember { mutableStateOf(true) }
    val filteredTasks by remember(query, selectedCategory, selectedPriority, tasks) {
        derivedStateOf {
            if (query.isNotEmpty()) {
                tasks.filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
            } else if (selectedCategory.isNotEmpty()) {
                tasks.filter { it.category == selectedCategory }
            } else if (selectedPriority != PriorityType.NONE) {
                tasks.filter { it.priorityType == selectedPriority }
            } else {
                tasks
            }
        }
    }

    val isScrollingFast by remember {
        derivedStateOf {
            lazyListState.isScrollInProgress && filteredTasks.size > 5
        }
    }

    LazyColumn(
        modifier = modifier
            .padding(innerPadding)
            .testTag("Tasks List"),
        state = lazyListState,
        contentPadding = PaddingValues(top = 0.dp, bottom = 72.dp)
    ) {
        if (!isPremium && showPremiumAd) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_task),
                            contentDescription = stringResource(R.string.app_name),
                            tint = textColor.getColor(context),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.premium),
                            color = textColor.getColor(context),
                            style = MaterialTheme.typography.displaySmall
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = stringResource(R.string.close),
                            tint = textColor.getColor(context),
                            modifier = Modifier
                                .size(18.dp)
                                .clickable(onClick = {
                                    showPremiumAd = false
                                })
                                .testTag("Close")
                        )
                    }
                    Text(
                        text = stringResource(R.string.premium_short_summary),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Text(
                        text = stringResource(R.string.learn_more),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .padding(horizontal = 16.dp)
                            .clickable(onClick = {
                                context.startActivity(
                                    Intent(context, PremiumActivity::class.java)
                                )
                            })
                    )
                }
            }
        }

        if (displayName.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = getDate(LocalContext.current),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = getGreeting(LocalContext.current, displayName),
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }
        }

        if (showStatistics) {
            item {
                AnimatedContent(
                    targetState = count
                ) { targetCount ->
                    StatisticsCard(count = targetCount)
                }
            }
        }

        stickyHeader {
            StickySearchBar(
                query = query,
                isPremium = isPremium,
                showVerticalGradient = showVerticalGradient,
                selectedPriority = selectedPriority,
                onSearch = onSearch,
                onClear = onClear,
                onPriority = onPriority
            )
        }

        item {
            AnimatedVisibility(categories.isNotEmpty()) {
                CategoryChips(
                    selectedCategory = selectedCategory,
                    categories = categories.sortedWith(compareBy<String> {
                        if (it == selectedCategory) 0 else 1
                    }.thenBy { it }).toSet(),
                    onSelection = onSelection,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        items(items = filteredTasks, key = { it.id }) { task ->
            var offsetX by remember { mutableFloatStateOf(0f) }
            val maxOffset = 56f

            val animatedOffsetX by animateDpAsState(
                targetValue = offsetX.dp
            )

            Box(if (isScrollingFast) Modifier else Modifier.animateItem()) {
                AnimatedVisibility(
                    offsetX < 0f,
                    enter = slideInHorizontally { (it) / 3 } + fadeIn(),
                    exit = slideOutHorizontally { (it) / 3 } + fadeOut(),
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    IconButton(
                        onClick = {
                            offsetX = 0f
                            onDelete(task)
                        },
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .align(Alignment.CenterEnd)
                            .testTag(stringResource(R.string.delete))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .offset { IntOffset(animatedOffsetX.roundToPx(), 0) }
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onHorizontalDrag = { change, dragAmount ->
                                    change.consume()
                                    offsetX = (offsetX + dragAmount).coerceIn(-maxOffset, 0f)
                                },
                                onDragEnd = {
                                    offsetX = if (offsetX < -maxOffset / 2) -maxOffset else 0f
                                }
                            )
                        }
                ) {
                    AnimatedVisibility(!task.isDone && task.datetime != 0L) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = convertMillisToDatetime(task, LocalContext.current),
                                style = MaterialTheme.typography.titleMedium,
                                color = if (task.isExpired()) redColor else Color.Unspecified,
                            )
                            AnimatedVisibility(isPremium && task.repeatType != RepeatType.ONE_TIME) {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = stringResource(R.string.repeat),
                                    tint = if (task.isExpired()) redColor else LocalContentColor.current,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .testTag(stringResource(R.string.repeat))
                                )
                            }
                        }
                    }
                    TaskItem(
                        task = task,
                        isPremium = isPremium,
                        onEdit = { onEdit(it) },
                        onToggle = { onToggle(task) }
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
    isPremium: Boolean,
    categories: Set<String>,
    onSave: (Task) -> Unit,
    onCancel: () -> Unit,
    windowHeightSizeClass: WindowHeightSizeClass
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var expandedRepeat by remember { mutableStateOf(false) }
    val repeatTypes = stringArrayResource(R.array.repeat_types)
    var selectedRepeat by remember { mutableStateOf(repeatTypes[0]) }
    val repeatDaysOfWeek = stringArrayResource(R.array.repeat_days_of_week)
    val selectedRepeatDaysOfWeek = remember { mutableListOf(0, 1, 2, 3, 4, 5, 6) }
    var date by remember { mutableLongStateOf(Calendar.getInstance().timeInMillis) }
    var hour by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MINUTE)) }
    var addReminder by remember { mutableStateOf(false) }
    var expandedPriority by remember { mutableStateOf(false) }
    val priorityTypes = stringArrayResource(R.array.priority_types)
    var selectedPriority by remember { mutableStateOf(priorityTypes[0]) }
    var expandedSuggestions by remember { mutableStateOf(false) }
    val suggestions = remember(category) {
        if (category.isNotEmpty()) {
            categories.filter { it.startsWith(category, ignoreCase = true) && it != category }
        } else {
            categories.toList()
        }
    }
    val density = LocalDensity.current
    val y = with(density) { 56.dp.roundToPx() }

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
            TitleTextFieldBottomSheet(
                title = title,
                onTitleChange = { title = it }
            )
            DescriptionTextFieldBottomSheet(
                description = description,
                onDescriptionChange = { description = it }
            )
            if (isPremium || categories.size < 3) {
                Column {
                    CategoryTextFieldBottomSheet(
                        category = category,
                        onCategoryChange = {
                            category = it
                            expandedSuggestions = it.isNotEmpty()
                        },
                        onCategoryFocusChange = { expandedSuggestions = it }
                    )
                    if (expandedSuggestions) {
                        SuggestionsPopup(
                            y = y,
                            onDismissRequest = { expandedSuggestions = false },
                            suggestions = suggestions,
                            onSuggestion = {
                                category = it
                                expandedSuggestions = false
                            }
                        )
                    }
                }
            }
            if (isPremium) {
                PriorityDropdownMenu(
                    expandedPriority = expandedPriority,
                    onExpandedChange = { expandedPriority = !expandedPriority },
                    selectedPriority = selectedPriority,
                    onDismissRequest = { expandedPriority = false },
                    priorityTypes = priorityTypes,
                    onPriority = {
                        selectedPriority = it
                        expandedPriority = false
                    }
                )
            }
            AnimatedVisibility(addReminder) {
                Column {
                    if (isPremium) {
                        RepeatDropdownMenu(
                            expandedRepeat = expandedRepeat,
                            onExpandedChange = { expandedRepeat = !expandedRepeat },
                            selectedRepeat = selectedRepeat,
                            onDismissRequest = { expandedRepeat = false },
                            repeatTypes = repeatTypes,
                            onRepeat = {
                                selectedRepeat = it
                                expandedRepeat = false
                            },
                            repeatDaysOfWeek = repeatDaysOfWeek,
                            selectedRepeatDaysOfWeek = selectedRepeatDaysOfWeek
                        )
                    }
                    DateTimePickerTextFields(
                        date = date,
                        hour = hour,
                        minute = minute,
                        onDateSelected = { date = it },
                        onTimeSelected = { it1, it2 ->
                            hour = it1
                            minute = it2
                        },
                        windowHeightSizeClass = windowHeightSizeClass,
                    )
                }
            }
            AddReminderCheckbox(
                addReminder = addReminder,
                onAddReminder = { addReminder = !addReminder },
                onCheckedChange = { addReminder = it }
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
                        onSave(Task(
                            title = title.trim(),
                            description = description.trim(),
                            category = category.lowercase().trim(),
                            datetime = if (addReminder) calendar.timeInMillis else 0L,
                            repeatType = if (addReminder) if (selectedRepeat == repeatTypes[2] && selectedRepeatDaysOfWeek.size == repeatDaysOfWeek.size) RepeatType.DAILY else RepeatType.entries[repeatTypes.indexOf(selectedRepeat)] else RepeatType.ONE_TIME,
                            repeatDaysOfWeek = if (selectedRepeat == repeatTypes[2] && selectedRepeatDaysOfWeek.size != repeatDaysOfWeek.size && addReminder) selectedRepeatDaysOfWeek.toList() else emptyList(),
                            priorityType = PriorityType.entries[priorityTypes.indexOf(selectedPriority)]
                        ))
                        title = ""
                        date = 0L
                        hour = 0
                        minute = 0
                    },
                    enabled = title.isNotEmpty() && category.length <= 25,
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
    isPremium: Boolean,
    categories: Set<String>,
    onSave: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onCancel: () -> Unit,
    windowHeightSizeClass: WindowHeightSizeClass
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var category by remember { mutableStateOf(task.category) }
    var expandedRepeat by remember { mutableStateOf(false) }
    val repeatTypes = stringArrayResource(R.array.repeat_types)
    var selectedRepeat by remember { mutableStateOf(repeatTypes[RepeatType.entries.indexOf(task.repeatType)]) }
    val repeatDaysOfWeek = stringArrayResource(R.array.repeat_days_of_week)
    val selectedRepeatDaysOfWeek = task.repeatDaysOfWeek.toMutableList()
    var expandedPriority by remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = task.datetime
    var date by remember { mutableLongStateOf(calendar.timeInMillis) }
    var hour by remember { mutableIntStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableIntStateOf(calendar.get(Calendar.MINUTE)) }
    var addReminder by remember { mutableStateOf(task.datetime != 0L) }
    val priorityTypes = stringArrayResource(R.array.priority_types)
    var selectedPriority by remember { mutableStateOf(priorityTypes[PriorityType.entries.indexOf(task.priorityType)]) }
    var expandedSuggestions by remember { mutableStateOf(false) }
    val suggestions = remember(category) {
        if (category.isNotEmpty()) {
            categories.filter { it.startsWith(category, ignoreCase = true) && it != category }
        } else {
            categories.toList()
        }
    }
    val density = LocalDensity.current
    val y = with(density) { 56.dp.roundToPx() }

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
            TitleTextFieldBottomSheet(
                title = title,
                onTitleChange = { title = it }
            )
            DescriptionTextFieldBottomSheet(
                description = description,
                onDescriptionChange = { description = it }
            )
            if (isPremium || categories.size < 3) {
                Column {
                    CategoryTextFieldBottomSheet(
                        category = category,
                        onCategoryChange = {
                            category = it
                            expandedSuggestions = it.isNotEmpty()
                        },
                        onCategoryFocusChange = { expandedSuggestions = it }
                    )
                    if (expandedSuggestions) {
                        SuggestionsPopup(
                            y = y,
                            onDismissRequest = { expandedSuggestions = false },
                            suggestions = suggestions,
                            onSuggestion = {
                                category = it
                                expandedSuggestions = false
                            }
                        )
                    }
                }
            }
            if (isPremium) {
                PriorityDropdownMenu(
                    expandedPriority = expandedPriority,
                    onExpandedChange = { expandedPriority = !expandedPriority },
                    selectedPriority = selectedPriority,
                    onDismissRequest = { expandedPriority = false },
                    priorityTypes = priorityTypes,
                    onPriority = {
                        selectedPriority = it
                        expandedPriority = false
                    }
                )
            }
            AnimatedVisibility(addReminder) {
                Column {
                    if (isPremium) {
                        RepeatDropdownMenu(
                            expandedRepeat = expandedRepeat,
                            onExpandedChange = { expandedRepeat = !expandedRepeat },
                            selectedRepeat = selectedRepeat,
                            onDismissRequest = { expandedRepeat = false },
                            repeatTypes = repeatTypes,
                            onRepeat = {
                                selectedRepeat = it
                                expandedRepeat = false
                            },
                            repeatDaysOfWeek = repeatDaysOfWeek,
                            selectedRepeatDaysOfWeek = selectedRepeatDaysOfWeek
                        )
                    }
                    DateTimePickerTextFields(
                        date = date,
                        hour = calendar.get(Calendar.HOUR_OF_DAY),
                        minute = calendar.get(Calendar.MINUTE),
                        onDateSelected = { date = it },
                        onTimeSelected = { it1, it2 ->
                            hour = it1
                            minute = it2
                        },
                        windowHeightSizeClass = windowHeightSizeClass,
                    )
                }
            }
            AddReminderCheckbox(
                addReminder = addReminder,
                onAddReminder = {
                    addReminder = !addReminder
                    if (task.datetime == 0L) {
                        calendar.timeInMillis = System.currentTimeMillis()
                    } else {
                        calendar.timeInMillis = task.datetime
                    }
                    date = calendar.timeInMillis
                    hour = calendar.get(Calendar.HOUR_OF_DAY)
                    minute = calendar.get(Calendar.MINUTE)
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                },
                onCheckedChange = {
                    addReminder = it
                    if (task.datetime == 0L) {
                        calendar.timeInMillis = System.currentTimeMillis()
                    } else {
                        calendar.timeInMillis = task.datetime
                    }
                    date = calendar.timeInMillis
                    hour = calendar.get(Calendar.HOUR_OF_DAY)
                    minute = calendar.get(Calendar.MINUTE)
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                }
            )
            Button(
                onClick = {
                    calendar.clear()
                    calendar.timeInMillis = date
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    onSave(task.copy(
                        title = title.trim(),
                        description = description.trim(),
                        category = category.lowercase().trim(),
                        datetime = if (addReminder) calendar.timeInMillis else 0L,
                        repeatType = if (addReminder) if (selectedRepeat == repeatTypes[2] && selectedRepeatDaysOfWeek.size == repeatDaysOfWeek.size) RepeatType.DAILY else RepeatType.entries[repeatTypes.indexOf(selectedRepeat)] else RepeatType.ONE_TIME,
                        repeatDaysOfWeek = if (selectedRepeat == repeatTypes[2] && selectedRepeatDaysOfWeek.size != repeatDaysOfWeek.size && addReminder) selectedRepeatDaysOfWeek.toList() else emptyList(),
                        priorityType = PriorityType.entries[priorityTypes.indexOf(selectedPriority)]
                    ))
                },
                enabled = title.isNotEmpty() && category.length <= 25,
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.save)) }
            FilledTonalButton(
                onClick = {
                    onDelete(task)
                    onCancel()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.delete)) }
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.cancel)) }
        }
    }
}

@Composable
fun TitleTextFieldBottomSheet(
    title: String,
    onTitleChange: (String) -> Unit
) {
    OutlinedTextField(
        value = title,
        onValueChange = onTitleChange,
        maxLines = 3,
        label = { Text(text = stringResource(R.string.title)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Title,
                contentDescription = stringResource(R.string.title)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .testTag(stringResource(R.string.title))
    )
}

@Composable
fun DescriptionTextFieldBottomSheet(
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        maxLines = 5,
        label = { Text(text = stringResource(R.string.description)) },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_description),
                contentDescription = stringResource(R.string.description)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp, max = 200.dp)
            .testTag(stringResource(R.string.description))
    )
}

@Composable
fun CategoryTextFieldBottomSheet(
    category: String,
    onCategoryChange: (String) -> Unit,
    onCategoryFocusChange: (Boolean) -> Unit,
) {
    OutlinedTextField(
        value = category,
        onValueChange = onCategoryChange,
        singleLine = true,
        label = { Text(text = stringResource(R.string.category)) },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_category),
                contentDescription = stringResource(R.string.category)
            )
        },
        trailingIcon = {
            if (category.length > 25) {
                Text(
                    text = "${category.length}/25",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(end = 16.dp)
                )
            } else {
                Text(
                    text = "${category.length}/25",
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        },
        isError = category.length > 25,
        modifier = Modifier
            .onFocusChanged { onCategoryFocusChange(it.isFocused) }
            .fillMaxWidth()
            .testTag(stringResource(R.string.category))
    )
}

@Composable
fun SuggestionsPopup(
    y: Int,
    onDismissRequest: () -> Unit,
    suggestions: List<String>,
    onSuggestion: (String) -> Unit,
) {
    Popup(
        offset = IntOffset(x = 0, y = y),
        properties = PopupProperties(focusable = false),
        onDismissRequest = onDismissRequest
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .padding(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(suggestions) { suggestion ->
                Text(
                    text = suggestion,
                    modifier = Modifier
                        .clickable {
                            onSuggestion(suggestion)
                        }
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityDropdownMenu(
    expandedPriority: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedPriority: String,
    onDismissRequest: () -> Unit,
    priorityTypes: Array<String>,
    onPriority: (String) -> Unit,
) {
    ExposedDropdownMenuBox(
        expanded = expandedPriority,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedPriority,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.priority)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.FilterList,
                    contentDescription = stringResource(R.string.priority)
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriority)
            },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expandedPriority,
            onDismissRequest = onDismissRequest
        ) {
            priorityTypes.forEach { selected ->
                DropdownMenuItem(
                    text = { Text(selected) },
                    onClick = { onPriority(selected) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatDropdownMenu(
    expandedRepeat: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedRepeat: String,
    repeatTypes: Array<String>,
    onDismissRequest: () -> Unit,
    onRepeat: (String) -> Unit,
    repeatDaysOfWeek: Array<String>,
    selectedRepeatDaysOfWeek: MutableList<Int>
) {
    ExposedDropdownMenuBox(
        expanded = expandedRepeat,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedRepeat,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.repeat)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Repeat,
                    contentDescription = stringResource(R.string.repeat)
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRepeat)
            },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expandedRepeat,
            onDismissRequest = onDismissRequest
        ) {
            repeatTypes.forEach { selected ->
                DropdownMenuItem(
                    text = { Text(selected) },
                    onClick = { onRepeat(selected) }
                )
            }
        }
    }
    AnimatedVisibility(selectedRepeat == repeatTypes[2]) {
        LazyRow(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(repeatDaysOfWeek) { dayOfWeek ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var isChecked by remember { mutableStateOf(selectedRepeatDaysOfWeek.contains(repeatDaysOfWeek.indexOf(dayOfWeek))) }

                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            if (it) {
                                isChecked = true
                                selectedRepeatDaysOfWeek.add(repeatDaysOfWeek.indexOf(dayOfWeek))
                            } else {
                                if (selectedRepeatDaysOfWeek.size > 1) {
                                    isChecked = false
                                    selectedRepeatDaysOfWeek.remove(repeatDaysOfWeek.indexOf(dayOfWeek))
                                }
                            }
                        }
                    )
                    Text(dayOfWeek)
                }
            }
        }
    }
}

@Composable
fun AddReminderCheckbox(
    addReminder: Boolean,
    onAddReminder: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onAddReminder() }
            .testTag(stringResource(R.string.add_reminder))
    ) {
        Checkbox(
            checked = addReminder,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = stringResource(R.string.add_reminder),
            modifier = Modifier.padding(start = 4.dp)
        )
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
                if (windowHeightSizeClass != WindowHeightSizeClass.Compact) {
                    IconButton(onClick = {
                        showTimeInput = !showTimeInput
                    }) {
                        Icon(
                            imageVector = if (showTimeInput) Icons.Outlined.AccessTime  else Icons.Outlined.Keyboard,
                            contentDescription = stringResource(R.string.select_time)
                        )
                    }
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
            if (showTimeInput || windowHeightSizeClass == WindowHeightSizeClass.Compact) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag(stringResource(R.string.back))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.background,
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.settings),
                color = MaterialTheme.colorScheme.background,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsTopBarPreview() {
    ToDoAppTheme {
        SettingsTopBar({})
    }
}

@Composable
fun Setting(
    imageVector: ImageVector,
    title: String,
    subtitle: String? = null,
    showEndIcon: Boolean = true,
    onClick: () -> Unit,
) {
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
                .clickable { onClick() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .weight(1F)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
            AnimatedVisibility(showEndIcon) {
                Icon(
                    painter = painterResource(R.drawable.ic_open_in_new),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingPreview() {
    ToDoAppTheme {
        Setting(
            imageVector = Icons.Outlined.Feedback,
            title = stringResource(R.string.feedback),
            showEndIcon = true,
            onClick = {}
        )
    }
}

@Composable
fun SettingsCategory(
    category: String
) {
    Text(
        text = category,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsCategoryPreview() {
    ToDoAppTheme {
        SettingsCategory(stringResource(R.string.settings))
    }
}

@Composable
fun CategoryChips(
    selectedCategory: String,
    categories: Set<String>,
    onSelection: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.size(48.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(categories.toList()) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onSelection(category) },
                label = { Text(category) }
            )
        }
    }
}

@Composable
fun ToDoAppDialog(
    title: String,
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.displayMedium
            )
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(title)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Spinner(
    items: Array<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, expanded)
                .clickable(onClick = { expanded = !expanded })
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = selectedItem,
                style = MaterialTheme.typography.headlineSmall
            )
            Icon(
                imageVector = if (expanded) Icons.Rounded.ArrowDropUp else Icons.Rounded.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item,
                            maxLines = 1,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}