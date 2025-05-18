package io.github.mamedovilkin.todoapp.reminder

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.util.FIVE_MINUTES_IN_MILLISECONDS
import io.github.mamedovilkin.todoapp.util.TEN_MINUTES_IN_MILLISECONDS
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

        val pendingIntents = getPendingIntents(task)

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.datetime, pendingIntents[0])
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.datetime + FIVE_MINUTES_IN_MILLISECONDS, pendingIntents[1])
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.datetime + TEN_MINUTES_IN_MILLISECONDS, pendingIntents[2])
    }

    override fun cancelReminder(task: Task) {
        val pendingIntents = getPendingIntents(task)

        alarmManager.cancel(pendingIntents[0])
        alarmManager.cancel(pendingIntents[1])
        alarmManager.cancel(pendingIntents[2])
    }

    override fun getPendingIntents(task: Task): List<PendingIntent> {
        val requestCode = task.id.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)

        val requestCodeAfterFiveMinutes = task.id.hashCode() + FIVE_MINUTES_IN_MILLISECONDS
        val pendingIntentAfterFiveMinutes = PendingIntent.getBroadcast(context, requestCodeAfterFiveMinutes, intent, PendingIntent.FLAG_IMMUTABLE)

        val requestCodeAfterTenMinutes = task.id.hashCode() + TEN_MINUTES_IN_MILLISECONDS
        val pendingIntentAfterTenMinutes = PendingIntent.getBroadcast(context, requestCodeAfterTenMinutes, intent, PendingIntent.FLAG_IMMUTABLE)

        return listOf(pendingIntent, pendingIntentAfterFiveMinutes, pendingIntentAfterTenMinutes)
    }
}