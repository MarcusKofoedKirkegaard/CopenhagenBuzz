package dk.itu.moapd.copenhagenbuzz.parkmkki.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult

/**
 * Activity that handles Firebase authentication via UI and manages the user login process.
 * Provides sign-in options using email or Google authentication.
 */
class FirebaseLoginActivity : AppCompatActivity() {

    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
            result -> onSignInResult(result)
    }

    /**
     * Called when the activity is created.
     * Initializes the sign-in process by launching the Firebase authentication UI.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createSignInIntent()
    }

    /**
     * Creates and launches the Firebase authentication intent, which will present the sign-in UI
     * to the user, allowing them to choose either email or Google login.
     */
    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .apply {
                setTosAndPrivacyPolicyUrls("https://firebase.google.com/terms/", "https://firebase.google.com/policies/")
            }
            .build()

        signInLauncher.launch(signInIntent)
    }

    /**
     * Called when the result of the sign-in attempt is received.
     * Handles the result of the authentication, and if successful, navigates to the main activity.
     *
     * @param result The result of the authentication attempt.
     */
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        when(result.resultCode) {
            RESULT_OK -> {
                startMainActivity()
            } else -> {}
        }
    }

    /**
     * Starts the main activity after successful login and finishes the current login activity.
     */
    private fun startMainActivity() {
        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }
}