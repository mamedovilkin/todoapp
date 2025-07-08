package io.github.mamedovilkin.todoapp.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import io.github.mamedovilkin.todoapp.ui.widget.ToDoAppWidget

class ToDoAppWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = ToDoAppWidget()
}