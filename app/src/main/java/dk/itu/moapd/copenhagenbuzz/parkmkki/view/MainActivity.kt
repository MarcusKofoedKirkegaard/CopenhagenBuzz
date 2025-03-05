/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.ActivityMainBinding
import dk.itu.moapd.copenhagenbuzz.parkmkki.R

/**
 * Main activity for handling the Event creation
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isLoggedIn = intent.getBooleanExtra("isLoggedIn", true)
        binding.contentMain.login.isVisible = !isLoggedIn
        binding.contentMain.logout.isVisible = isLoggedIn

        binding.contentMain.login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.contentMain.logout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Load the fragment into fragment_container
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddEventFragment())
                .commit()
        }
    }
}
