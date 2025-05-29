package io.github.mamedovilkin.database.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Calendar

@Entity(tableName = "tasks")
@Parcelize
data class Task(
    @PrimaryKey val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val isDone: Boolean = false,
    val datetime: Long = 0L,
    val isSynced: Boolean = false
) : Parcelable

fun Task.toHashMap(): HashMap<String, Any> {
    return hashMapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "category" to category,
        "isDone" to isDone,
        "datetime" to datetime,
        "isSynced" to isSynced,
    )
}

fun Task.isTodayTask(): Boolean {
    val taskCalendar = Calendar.getInstance()
    taskCalendar.timeInMillis = datetime
    val taskDayOfYear = taskCalendar.get(Calendar.DAY_OF_YEAR)
    val taskYear = taskCalendar.get(Calendar.YEAR)

    val currentCalendar = Calendar.getInstance()
    val currentDayOfYear = currentCalendar.get(Calendar.DAY_OF_YEAR)
    val currentYear = currentCalendar.get(Calendar.YEAR)

    return taskDayOfYear == currentDayOfYear && taskYear == currentYear
}

fun Task.isTaskThisYear(): Boolean {
    val taskCalendar = Calendar.getInstance()
    taskCalendar.timeInMillis = datetime
    val taskYear = taskCalendar.get(Calendar.YEAR)

    val currentCalendar = Calendar.getInstance()
    val currentYear = currentCalendar.get(Calendar.YEAR)

    return taskYear == currentYear
}

fun Task.isExpired(): Boolean {
    return Calendar.getInstance().timeInMillis > datetime
}