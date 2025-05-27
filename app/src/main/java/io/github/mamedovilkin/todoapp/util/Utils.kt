package io.github.mamedovilkin.todoapp.util

import android.content.Context
import android.text.format.DateFormat
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.database.room.Task
import io.github.mamedovilkin.database.room.isTaskThisYear
import io.github.mamedovilkin.database.room.isTodayTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun convertMillisToDate(millis: Long, context: Context): String {
    val formatter = SimpleDateFormat(context.resources.getString(R.string.date_pattern), Locale.getDefault())
    return formatter.format(Date(millis))
}

fun convertMillisToTime(millis: Long, context: Context): String {
    val pattern = if (DateFormat.is24HourFormat(context)) "HH:mm" else "hh:mm a"
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
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

fun convertMillisToDatetime(task: Task, context: Context): String {
    val pattern = getPattern(task, context)

    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(task.datetime)).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }
}

private fun getPattern(task: Task, context: Context): String {
    if (DateFormat.is24HourFormat(context)) {
        return if (task.isTodayTask()) {
            context.resources.getString(R.string.datetime_today_24hour_pattern)
        } else if (!task.isTaskThisYear()) {
            context.resources.getString(R.string.datetime_year_24hour_pattern)
        } else {
            context.resources.getString(R.string.datetime_24hour_pattern)
        }
    } else {
        return if (task.isTodayTask()) {
            context.resources.getString(R.string.datetime_today_pattern)
        } else if (!task.isTaskThisYear()) {
            context.resources.getString(R.string.datetime_year_pattern)
        } else {
            context.resources.getString(R.string.datetime_pattern)
        }
    }
}

suspend fun isInternetAvailable(): Boolean = withContext(Dispatchers.IO) {
    try {
        val client = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("https://clients3.google.com/generate_204")
            .build()

        val response = client.newCall(request).execute()
        response.code == 204
    } catch (_: IOException) {
        false
    }
}