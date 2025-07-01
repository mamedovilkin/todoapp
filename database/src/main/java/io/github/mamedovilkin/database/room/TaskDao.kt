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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<Task>)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()

    @Update
    suspend fun update(task: Task)

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTask(id: String): Task?

    @Query("SELECT * FROM tasks ORDER BY isDone, datetime ASC")
    fun getTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isSynced = 0")
    fun getUnSyncedTasks(): Flow<List<Task>>
}