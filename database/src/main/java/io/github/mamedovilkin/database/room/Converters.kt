package io.github.mamedovilkin.database.room

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromListToString(list: List<Int>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun fromStringToList(data: String): List<Int> {
        return if (data.isEmpty()) emptyList()
        else data.split(",").map { it.toInt() }
    }
}