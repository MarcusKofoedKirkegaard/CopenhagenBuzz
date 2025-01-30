package dk.itu.moapd.copenhagenbuzz.parkmkki

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // A set of private constants used in this class .
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    // GUI variables .
    private lateinit var eventName: EditText
    private lateinit var eventLocation: EditText
    private lateinit var eventDate: EditText
    private lateinit var eventType: EditText
    private lateinit var eventDescription: EditText
    private lateinit var addEventButton: FloatingActionButton


    // TODO : Implement the missing GUI variables
    // An instance of the `Event ` class .
    private val event: Event = Event(
        "",
        ""
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // IMPORTANT : This is an awful implementation . I only implemented
        // // // it like that because the students need to learn more about Kotlin . This implementation is quite similar to a Java code . We will refactor this code in the
        // next exercise session .

        // Link the UI components with the Kotlin source - code .
        eventName = findViewById(R.id.edit_text_event_name)
        eventLocation = findViewById(R.id.edit_text_event_location)
        eventDate = findViewById(R.id.edit_text_event_date)
        eventType = findViewById(R.id.edit_text_event_type)
        eventDescription = findViewById(R.id.edit_text_event_description)
        addEventButton = findViewById(R.id.fab_add_event)

        // Listener for user interaction in the `Add Event ` button .
        addEventButton.setOnClickListener {

            // Only execute the following code when the user fills all
            // `EditText `.
            if (eventName.text.toString().isNotEmpty() &&
                eventLocation.text.toString().isNotEmpty()
            ) {
                // Update the object attributes .
                event.setEventName(
                    eventName.text.toString().trim()
                )
                event.setEventLocation(
                    eventLocation.text.toString().trim()
                )

                // TODO : Implement the missing code here .
                // Write in the `Logcat ` system .
                showMessage()
            }
        }
    }

    private fun showMessage() {
        Log.d(TAG, event.toString())
    }
}