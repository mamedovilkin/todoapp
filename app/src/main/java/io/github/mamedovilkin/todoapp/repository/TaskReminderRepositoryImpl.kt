package io.github.mamedovilkin.todoapp.repository

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import io.github.mamedovilkin.database.room.RepeatType
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.todoapp.receiver.TaskReminderReceiver
import io.github.mamedovilkin.todoapp.util.FIVE_MINUTES_IN_MILLISECONDS
import io.github.mamedovilkin.todoapp.util.TASK_KEY
import io.github.mamedovilkin.todoapp.util.TEN_MINUTES_IN_MILLISECONDS
import java.util.Calendar

class TaskReminderRepositoryImpl(
    private val context: Context
) : TaskReminderRepository {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun scheduleReminder(task: Task): Task {
        val updatedTask = getTaskWithUpdatedDatetime(task)

        val pendingIntents = getPendingIntents(updatedTask)

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, updatedTask.datetime, pendingIntents[0])
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, updatedTask.datetime + FIVE_MINUTES_IN_MILLISECONDS, pendingIntents[1])
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, updatedTask.datetime + TEN_MINUTES_IN_MILLISECONDS, pendingIntents[2])
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, updatedTask.datetime + TEN_MINUTES_IN_MILLISECONDS, pendingIntents[3])

        return updatedTask
    }

    override fun getTaskWithUpdatedDatetime(task: Task): Task {
        val now = Calendar.getInstance()
        val due = Calendar.getInstance().apply {
            timeInMillis = task.datetime
        }

        return when (task.repeatType) {
            RepeatType.ONE_TIME -> {
                task
            }

            RepeatType.DAILY -> {
                if (!due.after(now)) due.add(Calendar.DAY_OF_YEAR, 1)
                task.copy(
                    datetime = due.timeInMillis,
                    isSynced = false
                )
            }

            RepeatType.WEEKLY -> {
                if (!due.after(now)) due.timeInMillis = getNextWeeklyReminder(task, due)
                task.copy(
                    datetime = due.timeInMillis,
                    isSynced = false
                )
            }

            RepeatType.MONTHLY -> {
                while (!due.after(now)) {
                    due.add(Calendar.MONTH, 1)
                }
                task.copy(
                    datetime = due.timeInMillis,
                    isSynced = false
                )
            }

            RepeatType.YEARLY -> {
                while (!due.after(now)) {
                    due.add(Calendar.YEAR, 1)
                }
                task.copy(
                    datetime = due.timeInMillis,
                    isSynced = false
                )
            }
        }
    }

    override fun getNextWeeklyReminder(task: Task, base: Calendar): Long {
        val days = task.repeatDaysOfWeek.map {
            when (it) {
                0 -> Calendar.MONDAY
                1 -> Calendar.TUESDAY
                2 -> Calendar.WEDNESDAY
                3 -> Calendar.THURSDAY
                4 -> Calendar.FRIDAY
                5 -> Calendar.SATURDAY
                6 -> Calendar.SUNDAY
                else -> throw IllegalArgumentException("Invalid index: $it")
            }
        }

        val now = Calendar.getInstance().apply {
            timeInMillis = base.timeInMillis
        }

        val todayDayOfWeek = now.get(Calendar.DAY_OF_WEEK)

        val difference = days.minOf { (7 - todayDayOfWeek) + it % 7 }

        now.add(Calendar.DAY_OF_MONTH, difference)

        return now.timeInMillis
    }

    override fun cancelReminder(task: Task) {
        val pendingIntents = getPendingIntents(task)

        alarmManager.cancel(pendingIntents[0])
        alarmManager.cancel(pendingIntents[1])
        alarmManager.cancel(pendingIntents[2])
        alarmManager.cancel(pendingIntents[3])
    }

    override fun getPendingIntents(task: Task): List<PendingIntent> {
        val offsets = listOf(0, FIVE_MINUTES_IN_MILLISECONDS, TEN_MINUTES_IN_MILLISECONDS, -1)

        return offsets.map { offset ->
            val intent = Intent(context, TaskReminderReceiver::class.java).apply {
                putExtra(TASK_KEY, task)
                putExtra("OFFSET", offset)
            }

            val requestCode = task.id.hashCode() + offset.hashCode()

            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }
}