package dk.itu.moapd.copenhagenbuzz.parkmkki

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.ActivityMainBinding
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.ContentMainBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var contentBinding: ContentMainBinding
    private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // A set of private constants used in this class.
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    private val event: Event = Event(
        "",
        "",
        LocalDate.now(),
        "",
        ""
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        contentBinding = ContentMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                event.setEventName(
                    contentBinding.editTextEventName.text.toString().trim()
                )
                event.setEventLocation(
                    contentBinding.editTextEventLocation.text.toString().trim()
                )
                try {
                    val dateString = contentBinding.editTextEventDate.text.toString().trim()

                    // Check if the string is empty before parsing
                    if (dateString.isNotEmpty()) {
                        event.setEventDate(LocalDate.parse(dateString, dateFormat))
                    } else {
                        Log.d(TAG, "Date string is empty!")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Could not parse the date! Error: ${e.message}")
                }
                event.setEventType(
                    contentBinding.editTextEventType.text.toString().trim()
                )
                event.setEventDescription(
                    contentBinding.editTextEventLocation.text.toString().trim()
                )
                // Write in the `Logcat ` system .
                showMessage()
            }
        }
    }

    private fun showMessage() {
        // I don't know why this does not print to the console in debug
        Log.d(TAG, event.toString())
    }
}
