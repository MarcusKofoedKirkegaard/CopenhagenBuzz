/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki.view
import android.content.Intent
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.ActivityLoginBinding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    companion object {
        private const val Something_test = "Something_Test"
    }

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
        }

        binding.fabGuest.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("isLoggedIn", false)
            }
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        binding.main.apply {
            //outState.putString(Something_test, toString()) //etc.
        }
        super.onSaveInstanceState(outState)
    }
}