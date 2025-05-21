package io.github.mamedovilkin.todoapp.ui.widget.action

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import io.github.mamedovilkin.todoapp.ui.widget.ToDoAppWidget

class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        ToDoAppWidget().updateAll(context)
    }
}