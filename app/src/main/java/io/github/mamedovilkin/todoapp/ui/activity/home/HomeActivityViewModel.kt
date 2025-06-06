package io.github.mamedovilkin.todoapp.ui.activity.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.id.AccessToken
import com.vk.id.VKID
import com.vk.id.refresh.VKIDRefreshTokenCallback
import com.vk.id.refresh.VKIDRefreshTokenFail
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import kotlinx.coroutines.launch

class HomeActivityViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val syncWorkerRepository: SyncWorkerRepository
) : ViewModel() {

    fun refreshSignInWithVK() = viewModelScope.launch {
        VKID.instance.refreshToken(
            callback = object : VKIDRefreshTokenCallback {
                override fun onSuccess(token: AccessToken) {
                    val userID = token.userID.toString()
                    val photoURL = token.userData.photo200.toString()
                    val displayName = token.userData.firstName.toString()

                    saveUser(userID, photoURL, displayName)
                }

                override fun onFail(fail: VKIDRefreshTokenFail) {
                    clearUser()
                }
            }
        )
    }

    private fun saveUser(
        userID: String,
        photoURL: String,
        displayName: String,
    ) = viewModelScope.launch {
        dataStoreRepository.setUserID(userID)
        dataStoreRepository.setPhotoURL(photoURL)
        dataStoreRepository.setDisplayName(displayName)
        syncWorkerRepository.scheduleSyncTasksWork()
    }

    private fun clearUser() = viewModelScope.launch {
        dataStoreRepository.setUserID("")
        dataStoreRepository.setPhotoURL("")
        dataStoreRepository.setDisplayName("")
    }
}