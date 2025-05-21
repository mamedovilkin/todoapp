package io.github.mamedovilkin.database.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("SELECT * FROM tasks ORDER BY isDone, datetime ASC")
    fun getTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE title LIKE :query ORDER BY isDone, datetime ASC")
    fun searchForTasks(query: String): Flow<List<Task>>
}