package dk.itu.moapd.copenhagenbuzz.parkmkki.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.copenhagenbuzz.parkmkki.R

/**
 * Activity that handles Firebase authentication via UI and manages the user login process.
 * Provides sign-in options using email or Google authentication.
 */
class FirebaseLoginActivity : AppCompatActivity() {

    // Registering the result handler for the sign-in activity result using FirebaseAuthUIActivityResultContract.
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
            result -> onSignInResult(result) // Handle the result of the sign-in attempt.
    }

    /**
     * Called when the activity is created.
     * Initializes the sign-in process by launching the Firebase authentication UI.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createSignInIntent() // Start the sign-in process.
    }

    /**
     * Creates and launches the Firebase authentication intent, which will present the sign-in UI
     * to the user, allowing them to choose either email or Google login.
     */
    private fun createSignInIntent() {
        // Define the available authentication providers.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),  // Email authentication provider.
            AuthUI.IdpConfig.GoogleBuilder().build()  // Google authentication provider.
        )

        // Build the sign-in intent with the available providers and some additional configurations.
        val signInIntent = AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers) // Set the providers.
            .setIsSmartLockEnabled(false) // Disable smart lock for saving credentials.
            .apply {
                setTosAndPrivacyPolicyUrls("https://firebase.google.com/terms/", "https://firebase.google.com/policies/") // Set the terms of service and privacy policy URLs.
            }
            .build()

        // Launch the sign-in intent using the result launcher.
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
                // If authentication is successful, navigate to the main activity.
                startMainActivity()
            } else -> {
            // Handle authentication failure (e.g., show an error message or retry).
        }
        }
    }

    /**
     * Starts the main activity after successful login and finishes the current login activity.
     */
    private fun startMainActivity() {
        Intent(this, MainActivity::class.java).apply {
            startActivity(this) // Start the main activity.
            finish() // Finish the current login activity to prevent the user from returning to it.
        }
    }

}
