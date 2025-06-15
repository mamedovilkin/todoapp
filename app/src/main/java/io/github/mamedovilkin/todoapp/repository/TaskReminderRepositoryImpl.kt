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
import io.github.mamedovilkin.todoapp.util.FIFTEEN_MINUTES_OFFSET
import io.github.mamedovilkin.todoapp.util.FIVE_MINUTES_OFFSET
import io.github.mamedovilkin.todoapp.util.TASK_KEY
import io.github.mamedovilkin.todoapp.util.TEN_MINUTES_OFFSET
import io.github.mamedovilkin.todoapp.util.TWENTY_MINUTES_OFFSET
import java.util.Calendar

class TaskReminderRepositoryImpl(
    private val context: Context
) : TaskReminderRepository {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun scheduleReminder(task: Task, isPremium: Boolean): Task {
        val updatedTask = getTaskWithUpdatedDatetime(task)

        val pendingIntents = getPendingIntents(updatedTask, isPremium)

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, updatedTask.datetime, pendingIntents[0])
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, updatedTask.datetime + FIVE_MINUTES_OFFSET, pendingIntents[1])
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, updatedTask.datetime + TEN_MINUTES_OFFSET, pendingIntents[2])

        if (isPremium) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, updatedTask.datetime + FIFTEEN_MINUTES_OFFSET, pendingIntents[3])
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, updatedTask.datetime + TWENTY_MINUTES_OFFSET, pendingIntents[4])
        }

        return updatedTask
    }

    override fun getTaskWithUpdatedDatetime(task: Task): Task {
        val now = Calendar.getInstance()
        val due = Calendar.getInstance().apply {
            timeInMillis = task.datetime
        }

        return when (task.repeatType) {
            RepeatType.ONE_TIME -> task

            RepeatType.DAILY -> {
                while (!due.after(now)) due.add(Calendar.DAY_OF_YEAR, 1)
                task.copy(
                    datetime = due.timeInMillis,
                    isSynced = false
                )
            }

            RepeatType.WEEKLY -> {
                while (!due.after(now)) due.timeInMillis = getNextWeeklyReminder(task, due)
                task.copy(
                    datetime = due.timeInMillis,
                    isSynced = false
                )
            }

            RepeatType.MONTHLY -> {
                while (!due.after(now)) due.add(Calendar.MONTH, 1)
                task.copy(
                    datetime = due.timeInMillis,
                    isSynced = false
                )
            }

            RepeatType.YEARLY -> {
                while (!due.after(now)) due.add(Calendar.YEAR, 1)
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

        val difference = days.minOf {
            val diff = it - todayDayOfWeek
            if (diff <= 0) diff + 7 else diff
        }

        now.add(Calendar.DAY_OF_MONTH, difference)

        return now.timeInMillis
    }

    override fun cancelReminder(task: Task, isPremium: Boolean) {
        val pendingIntents = getPendingIntents(task, isPremium)

        alarmManager.cancel(pendingIntents[0])
        alarmManager.cancel(pendingIntents[1])
        alarmManager.cancel(pendingIntents[2])

        if (isPremium) {
            alarmManager.cancel(pendingIntents[3])
            alarmManager.cancel(pendingIntents[4])
        }
    }

    override fun getPendingIntents(task: Task, isPremium: Boolean): List<PendingIntent> {
        val offsets = mutableListOf(
            0,
            FIVE_MINUTES_OFFSET,
            TEN_MINUTES_OFFSET
        )

        if (isPremium) {
            offsets.addAll(listOf(FIFTEEN_MINUTES_OFFSET, TWENTY_MINUTES_OFFSET))
        }

        return offsets.map { offset ->
            val intent = Intent(context, TaskReminderReceiver::class.java).apply {
                putExtra(TASK_KEY, task)
            }

            val requestCode = 31 * task.id.hashCode() + offset.hashCode()

            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }
}