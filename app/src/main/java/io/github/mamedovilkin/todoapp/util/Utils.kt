package io.github.mamedovilkin.todoapp.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.format.DateFormat
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.data.room.Task
import io.github.mamedovilkin.todoapp.ui.ToDoAppActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun convertMillisToDate(millis: Long, context: Context): String {
    val formatter = SimpleDateFormat(context.resources.getString(R.string.date_pattern), Locale.getDefault())
    return formatter.format(Date(millis))
}

fun convertToTime(hour: Int, minute: Int, context: Context): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    val pattern = if (DateFormat.is24HourFormat(context)) "HH:mm" else "hh:mm"
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(calendar.timeInMillis))
}

fun convertMillisToDatetime(millis: Long, context: Context): String {
    val pattern = if (DateFormat.is24HourFormat(context)) {
        if (isTodayTask(millis)) {
            context.resources.getString(R.string.datetime_today_24hour_pattern)
        } else if (!isTaskThisYear(millis)) {
            context.resources.getString(R.string.datetime_year_24hour_pattern)
        } else {
            context.resources.getString(R.string.datetime_24hour_pattern)
        }
    } else {
        if (isTodayTask(millis)) {
            context.resources.getString(R.string.datetime_today_pattern)
        } else if (!isTaskThisYear(millis)) {
            context.resources.getString(R.string.datetime_year_pattern)
        } else {
            context.resources.getString(R.string.datetime_pattern)
        }
    }

    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(millis)).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }
}

fun isTodayTask(millis: Long): Boolean {
    val taskCalendar = Calendar.getInstance()
    taskCalendar.timeInMillis = millis
    val taskDayOfYear = taskCalendar.get(Calendar.DAY_OF_YEAR)
    val taskYear = taskCalendar.get(Calendar.YEAR)

    val currentCalendar = Calendar.getInstance()
    currentCalendar.timeInMillis = System.currentTimeMillis()
    val currentDayOfYear = currentCalendar.get(Calendar.DAY_OF_YEAR)
    val currentYear = currentCalendar.get(Calendar.YEAR)

    return taskDayOfYear == currentDayOfYear && taskYear == currentYear
}

fun isTaskThisYear(millis: Long): Boolean {
    val taskCalendar = Calendar.getInstance()
    taskCalendar.timeInMillis = millis
    val taskYear = taskCalendar.get(Calendar.YEAR)

    val currentCalendar = Calendar.getInstance()
    currentCalendar.timeInMillis = System.currentTimeMillis()
    val currentYear = currentCalendar.get(Calendar.YEAR)

    return taskYear == currentYear
}

fun Task.isExpired(): Boolean {
    return Calendar.getInstance().timeInMillis > datetime
}

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun makeReminderNotification(title: String, context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.resources.getString(R.string.notification_channel),
            importance
        )
        channel.description = context.resources.getString(R.string.notification_channel)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }

    val pendingIntent: PendingIntent = createPendingIntent(context)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_task)
        .setContentTitle(title)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
}

fun createPendingIntent(appContext: Context): PendingIntent {
    val intent = Intent(appContext, ToDoAppActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    var flags = PendingIntent.FLAG_UPDATE_CURRENT
    flags = flags or PendingIntent.FLAG_IMMUTABLE

    return PendingIntent.getActivity(
        appContext,
        REQUEST_CODE,
        intent,
        flags
    )
}