package io.github.mamedovilkin.todoapp.ui.activity.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vk.id.AccessToken
import com.vk.id.VKID
import com.vk.id.VKIDAuthFail
import com.vk.id.auth.VKIDAuthCallback
import com.vk.id.logout.VKIDLogoutCallback
import com.vk.id.logout.VKIDLogoutFail
import io.github.mamedovilkin.database.repository.DataStoreRepository
import io.github.mamedovilkin.database.repository.FirestoreRepository
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsActivityViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val firestoreRepository: FirestoreRepository,
    private val syncWorkerRepository: SyncWorkerRepository
) : ViewModel() {

    val userID = dataStoreRepository.userID
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ""
        )

    fun signInWithVK() = viewModelScope.launch {
        VKID.instance.authorize(object : VKIDAuthCallback {
            override fun onAuth(token: AccessToken) {
                val userID = token.userID.toString()
                val photoURL = token.userData.photo200.toString()
                val displayName = token.userData.firstName.toString()

                saveUser(userID, photoURL, displayName)
            }

            override fun onFail(fail: VKIDAuthFail) {
                clearUser()
            }
        })
    }

    fun signOut(onError: (String?) -> Unit) = viewModelScope.launch {
        VKID.instance.logout(object : VKIDLogoutCallback {
            override fun onSuccess() {
                clearUser()
            }

            override fun onFail(fail: VKIDLogoutFail) {
                onError(fail.description)
            }
        })
    }

    private fun saveUser(
        userID: String,
        photoURL: String,
        displayName: String,
    ) = viewModelScope.launch {
        dataStoreRepository.setUserID(userID)
        dataStoreRepository.setPhotoURL(photoURL)
        dataStoreRepository.setDisplayName(displayName)
        firestoreRepository.setLastSignIn(userID)
        syncWorkerRepository.scheduleSyncTasksWork()
    }

    private fun clearUser() = viewModelScope.launch {
        dataStoreRepository.setUserID("")
        dataStoreRepository.setPhotoURL("")
        dataStoreRepository.setDisplayName("")
    }
}