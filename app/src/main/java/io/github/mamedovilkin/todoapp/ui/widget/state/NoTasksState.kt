package io.github.mamedovilkin.todoapp.ui.widget.state

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.ui.theme.textColor

@Composable
fun NoTasksState(context: Context) {
    Text(
        text = context.getString(R.string.there_are_no_tasks_for_today),
        style = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            color = textColor
        ),
        modifier = GlanceModifier
            .padding(vertical = 16.dp)
    )
}