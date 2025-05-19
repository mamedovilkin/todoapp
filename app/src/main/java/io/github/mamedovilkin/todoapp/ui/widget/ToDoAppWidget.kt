package io.github.mamedovilkin.todoapp.ui.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.state.PreferencesGlanceStateDefinition
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import io.github.mamedovilkin.todoapp.ui.ToDoAppActivity
import io.github.mamedovilkin.todoapp.ui.widget.action.RefreshAction
import io.github.mamedovilkin.todoapp.ui.widget.state.NoTasksState
import io.github.mamedovilkin.todoapp.ui.widget.state.TodayTasksState
import io.github.mamedovilkin.todoapp.util.isTodayTask
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ToDoAppWidget() : GlanceAppWidget(), KoinComponent {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val taskRepository: TaskRepository by inject()
        val taskReminderRepository: TaskReminderRepository by inject()

        provideContent {
            val tasks = taskRepository.tasks.collectAsState(emptyList())
            val filteredTasks = tasks.value.filter { task -> !task.isDone && isTodayTask(task.datetime) }
            val coroutineScope = rememberCoroutineScope()

            GlanceTheme {
                Content(
                    tasks = filteredTasks,
                    onToggle = {
                        coroutineScope.launch {
                            val task = it.copy(isDone = !it.isDone)

                            taskRepository.update(task)

                            if (task.isDone) {
                                taskReminderRepository.cancelReminder(task)
                            } else {
                                taskReminderRepository.scheduleReminder(task)
                            }
                        }
                    }
                )
            }
        }
    }

    @Composable
    private fun ToDoAppWidgetTitleBar(
        context: Context,
        intent: Intent
    ) {
        TitleBar(
            startIcon = ImageProvider(R.drawable.ic_task),
            title = context.getString(R.string.app_name),
            modifier = GlanceModifier
                .padding(top = 8.dp)
                .padding(end = 8.dp)
        ) {
            CircleIconButton(
                imageProvider = ImageProvider(R.drawable.ic_add),
                contentDescription = context.getString(R.string.new_task),
                onClick = actionStartActivity(intent)
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            CircleIconButton(
                imageProvider = ImageProvider(R.drawable.ic_refresh),
                contentDescription = context.getString(R.string.refresh),
                onClick = actionRunCallback<RefreshAction>()
            )
        }
    }

    @Composable
    private fun Content(
        tasks: List<Task>,
        onToggle: (Task) -> Unit,
    ) {
        val context = LocalContext.current
        val intent = Intent(context, ToDoAppActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("action", "new_task")
        }

        Scaffold(
            titleBar = {
                ToDoAppWidgetTitleBar(context, intent)
            },
            modifier = GlanceModifier.fillMaxSize()
        ) {
            if (tasks.isEmpty()) {
                NoTasksState(context = context)
            } else {
                TodayTasksState(
                    tasks = tasks,
                    onToggle = onToggle
                )
            }
        }
    }
}