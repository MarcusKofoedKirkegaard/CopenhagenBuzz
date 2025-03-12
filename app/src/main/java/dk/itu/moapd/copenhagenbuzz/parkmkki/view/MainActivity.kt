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
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.ActivityMainBinding
import dk.itu.moapd.copenhagenbuzz.parkmkki.R

/**
 * MainActivity serves as the entry point of the application and manages navigation,
 * authentication status, and UI setup.
 */
class MainActivity : AppCompatActivity() {
    /**
     * View binding for accessing UI elements in `activity_main.xml`.
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * Configuration for the navigation bar to manage top-level destinations.
     */
    private lateinit var appBarConfiguration: AppBarConfiguration

    /**
     * Called when the activity is created.
     *
     * Initializes UI components, sets up navigation, and handles login/logout visibility.
     *
     * @param savedInstanceState The saved state of the activity, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the view using ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if the user is logged in
        val isLoggedIn = intent.getBooleanExtra("isLoggedIn", true)
        binding.contentMain.login.isVisible = !isLoggedIn
        binding.contentMain.logout.isVisible = isLoggedIn

        // Set up login button click listener
        binding.contentMain.login.setOnClickListener {
            navigateToLogin()
        }

        // Set up logout button click listener
        binding.contentMain.logout.setOnClickListener {
            navigateToLogin()
        }

        // Initialize Navigation Component
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        // Set up the top app bar with navigation controller
        setSupportActionBar(binding.contentMain.topAppBar)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Link bottom navigation with the navigation controller
        NavigationUI.setupWithNavController(binding.contentMain.bottomNavigation, navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in listOf(R.id.nav_timeline, R.id.nav_maps, R.id.nav_calendar, R.id.nav_add_event, R.id.nav_favorites)) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                val toolbar = findViewById<Toolbar>(R.id.top_app_bar)
                toolbar.navigationIcon = null
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }

    }

    /**
     * Navigates the user to the LoginActivity.
     */
    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
