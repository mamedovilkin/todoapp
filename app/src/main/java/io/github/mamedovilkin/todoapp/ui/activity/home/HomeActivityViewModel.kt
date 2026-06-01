package io.github.mamedovilkin.todoapp.ui.activity.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.database.repository.TaskRepository
import io.github.mamedovilkin.todoapp.repository.TaskReminderRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeActivityViewModel(
    private val taskRepository: TaskRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val taskReminderRepository: TaskReminderRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            val wasFirstLaunch = dataStoreRepository.wasFirstLaunch.first()

            if (!wasFirstLaunch) {
                val tasks = taskRepository.tasks.first()

                tasks.forEach { task ->
                    taskReminderRepository.scheduleReminder(task)
                }

                dataStoreRepository.setWasFirstLaunch(true)
            }
        }
    }
}