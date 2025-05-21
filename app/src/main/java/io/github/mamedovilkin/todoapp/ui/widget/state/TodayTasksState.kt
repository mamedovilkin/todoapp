package io.github.mamedovilkin.todoapp.ui.widget.state

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.ui.ToDoAppActivity
import io.github.mamedovilkin.todoapp.ui.theme.textColor
import io.github.mamedovilkin.todoapp.util.convertMillisToTime

@Composable
fun TodayTasksState(
    tasks: List<Task>,
    onToggle: (Task) -> Unit,
) {
    LazyColumn(modifier = GlanceModifier.padding(bottom = 8.dp)) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onToggle = onToggle
            )
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onToggle: (Task) -> Unit,
) {
    Column {
        Text(
            text = convertMillisToTime(task.datetime, LocalContext.current),
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = textColor
            ),
            modifier = GlanceModifier.padding(horizontal = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .cornerRadius(4.dp)
                .clickable(onClick = actionStartActivity(
                    Intent(LocalContext.current, ToDoAppActivity::class.java)
                ))
        ) {
            CheckBox(
                checked = task.isDone,
                onCheckedChange = { onToggle(task) },
                modifier = GlanceModifier.padding(end = 4.dp)
            )
            Text(
                text = task.title,
                maxLines = 1,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    color = if (task.isDone) ColorProvider(
                        day = Color.Gray,
                        night = Color.Gray
                    ) else textColor,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
                )
            )
        }
    }
}