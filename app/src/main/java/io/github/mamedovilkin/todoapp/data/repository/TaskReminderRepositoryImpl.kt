package io.github.mamedovilkin.todoapp.data.repository

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import io.github.mamedovilkin.todoapp.receiver.TaskReminderReceiver
import io.github.mamedovilkin.todoapp.data.room.Task
import io.github.mamedovilkin.todoapp.util.TITLE_KEY
import javax.inject.Inject

class TaskReminderRepositoryImpl @Inject constructor(
    private val context: Context
) : TaskReminderRepository {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val intent = Intent(context, TaskReminderReceiver::class.java)

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun scheduleReminder(task: Task) {
        intent.putExtra(TITLE_KEY, task.title)

        val requestCode = task.id.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.datetime, pendingIntent)
    }

    override fun cancelReminder(task: Task) {
        val requestCode = task.id.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)
    }
}