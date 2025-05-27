package io.github.mamedovilkin.todoapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.net.toUri
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.repository.SyncWorkerRepository
import io.github.mamedovilkin.todoapp.ui.screen.settings.SettingsScreen
import io.github.mamedovilkin.todoapp.ui.theme.ToDoAppTheme
import io.github.mamedovilkin.todoapp.util.APP_LINK
import io.github.mamedovilkin.todoapp.util.FEEDBACK_EMAIL
import io.github.mamedovilkin.todoapp.util.WEBSITE_LINK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

class SettingsActivity : ComponentActivity(), KoinComponent {

    private val auth: FirebaseAuth by inject()
    private val syncWorkerRepository: SyncWorkerRepository by inject()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val packageInfo = packageManager.getPackageInfo(packageName, 0)

            ToDoAppTheme {
                SettingsScreen(
                    windowWidthSizeClass = windowSizeClass.widthSizeClass,
                    version = packageInfo.versionName.toString(),
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onSignIn = { launchCredentialManager() },
                    onSignOut = { signOut() },
                    onFeedback = { sendFeedback() },
                    onRateUs = { rateUs() },
                    onTellFriend = { tellFriend() },
                    onAboutDeveloper = { openDeveloperWebsite() }
                )
            }
        }
    }

    private fun launchCredentialManager() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                val localManager = CredentialManager.create(applicationContext)
                try {
                    localManager.getCredential(
                        context = applicationContext,
                        request = request
                    )
                } catch (e: GetCredentialException) {
                    withContext(Dispatchers.Main) {
                        toast(e.localizedMessage)
                    }
                    null
                } catch (e: NoCredentialException) {
                    withContext(Dispatchers.Main) {
                        toast(e.localizedMessage)
                    }
                    null
                }
            }

            result?.credential?.let { handleSignIn(it) }
        }
    }

    private fun handleSignIn(credential: Credential) {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            toast("Credential is not of type Google ID!")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    syncWorkerRepository.scheduleSyncTasksWork()
                } else {
                    toast(task.exception?.localizedMessage)
                }
            }
    }

    private fun signOut() {
        auth.signOut()

        lifecycleScope.launch {
            try {
                val clearRequest = ClearCredentialStateRequest()
                val credentialManager = CredentialManager.create(applicationContext)
                credentialManager.clearCredentialState(clearRequest)
            } catch (e: ClearCredentialException) {
               withContext(Dispatchers.Main) {
                   toast(e.localizedMessage)
               }
            }
        }
    }

    private fun toast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun sendFeedback() {
        val subject = getString(R.string.app_name)
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:$FEEDBACK_EMAIL?subject=$subject".toUri()
        }
        startActivity(intent)
    }

    private fun rateUs() {
        val intent = Intent(Intent.ACTION_VIEW, APP_LINK.toUri())
        startActivity(intent)
    }

    private fun tellFriend() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.tell_a_friend_text))
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(intent, null)
        startActivity(shareIntent)
    }

    private fun openDeveloperWebsite() {
        val intent = Intent(Intent.ACTION_VIEW, WEBSITE_LINK.toUri())
        startActivity(intent)
    }
}