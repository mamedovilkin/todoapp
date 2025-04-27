package io.github.mamedovilkin.todoapp.util

import android.content.Context
import android.text.format.DateFormat
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.data.room.Task
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