package io.github.mamedovilkin.database.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "tasks")
@Parcelize
data class Task(
    @PrimaryKey val id: String = "",
    val title: String,
    val isDone: Boolean = false,
    val datetime: Long = 0L,
) : Parcelable