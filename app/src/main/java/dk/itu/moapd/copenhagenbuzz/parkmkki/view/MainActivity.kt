/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.MaterialToolbar
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

        // Initialize Navigation Component
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        // Set up the top app bar with navigation controller
        setSupportActionBar(binding.topAppBar)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Link bottom navigation with the navigation controller
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in listOf(R.id.nav_timeline, R.id.nav_maps, R.id.nav_calendar, R.id.nav_add_event, R.id.nav_favorites)) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                val toolbar = findViewById<MaterialToolbar>(R.id.top_app_bar)
                toolbar.navigationIcon = null
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val isLoggedIn = intent.getBooleanExtra("isLoggedIn", true)
        menu.findItem(R.id.login).isVisible = !isLoggedIn
        menu.findItem(R.id.logout).isVisible = isLoggedIn
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                navigateToLogin()
                true
            }
            R.id.login -> {
                navigateToLogin()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Navigates the user to the LoginActivity.
     */
    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
