/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.ActivityMainBinding
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.ContentMainBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.android.material.snackbar.Snackbar


/**
 * Main activity for handling the Event creation
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var contentBinding: ContentMainBinding
    private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // A set of private constants used in this class.
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    private var event: Event = Event(
        "",
        "",
        LocalDate.now(),
        "",
        ""
    )

    /**
     * Creates the MainActivity window, binds the UI Components & and adds a onClickListener
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        contentBinding = ContentMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        contentBinding = binding.contentMain
        // Listener for user interaction in the `Add Event ` button .
        contentBinding.fabAddEvent.setOnClickListener {
            // Only execute the following code when the user fills all `EditText `.
            if (contentBinding.editTextEventName.text.toString().isNotEmpty() &&
                contentBinding.editTextEventLocation.text.toString().isNotEmpty() &&
                contentBinding.editTextEventDate.text.toString().isNotEmpty() &&
                contentBinding.editTextEventType.text.toString().isNotEmpty() &&
                contentBinding.editTextEventLocation.text.toString().isNotEmpty()
            ) {
                // Update the object attributes
                event.eventName = contentBinding.editTextEventName.text.toString().trim()
                event.eventLocation = contentBinding.editTextEventLocation.text.toString().trim()
                try {
                    val dateString = contentBinding.editTextEventDate.text.toString().trim()

                    // Check if the string is empty before parsing
                    if (dateString.isNotEmpty()) {
                        event.eventDate = LocalDate.parse(dateString, dateFormat)
                    } else {
                        Log.d(TAG, "Date string is empty!")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Could not parse the date! Error: ${e.message}")
                }
                event.eventType = contentBinding.editTextEventType.text.toString().trim()
                event.eventDescription = contentBinding.editTextEventLocation.text.toString().trim()

                // Write in the `Logcat ` system .
                showMessage()
            }
        }
    }

    /**
     * Prints out the created Event to LogCat
     */
    private fun showMessage() {
        Snackbar.make(contentBinding.root, "Event added using ${event.toString()}", Snackbar.LENGTH_LONG).show()

        Log.d(TAG, event.toString())
    }
}
