/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki.views.activities
import android.content.Intent
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.ActivityLoginBinding
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

/**
 * Activity for handling user login.
 *
 * This activity provides login functionality for both registered users and guest users.
 */
class LoginActivity : AppCompatActivity() {

    /**
     * View binding for accessing UI elements in `activity_login.xml`.
     */
    private lateinit var binding: ActivityLoginBinding

    /**
     * Called when the activity is first created.
     *
     * Initializes UI components and sets up click listeners for login and guest mode.
     *
     * @param savedInstanceState Previously saved instance state, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("isLoggedIn", true)
            }
            startActivity(intent)
            finish()
        }

        binding.fabGuest.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("isLoggedIn", false)
            }
            startActivity(intent)
            finish()
        }
    }

    /**
     * Saves the instance state when the activity is about to be destroyed.
     *
     * @param outState Bundle in which to place saved state data.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        binding.main.apply {
        }
        super.onSaveInstanceState(outState)
    }
}