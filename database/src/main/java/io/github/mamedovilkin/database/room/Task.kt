package io.github.mamedovilkin.database.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String = "",
    val title: String,
    val isDone: Boolean = false,
    val datetime: Long = 0L,
)