/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki.views.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.ActivityMainBinding
import dk.itu.moapd.copenhagenbuzz.parkmkki.R
import android.util.Log

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

    private lateinit var auth: FirebaseAuth

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

        // LOOK THIS UP!
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in listOf(R.id.nav_timeline, R.id.nav_maps, R.id.nav_add_event, R.id.nav_favorites)) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                val toolbar = findViewById<MaterialToolbar>(R.id.top_app_bar)
                toolbar.navigationIcon = null
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }

        auth = FirebaseAuth.getInstance()
        checkAndRequestNotificationPermission()

    }

    override fun onStart() {
        super.onStart()

        auth.currentUser ?: startLoginActivity()
    }

    private fun startLoginActivity() {
        Intent(this, FirebaseLoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
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
                FirebaseAuth.getInstance().signOut()
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
        Intent(this, FirebaseLoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
    }

    private val REQUEST_CODE_POST_NOTIFICATIONS = 1001

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.i("MainActivity", "Notification permission granted")
            } else {
                Log.w("MainActivity", "Notification permission denied")
            }
        }
    }
}


