/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.copenhagenbuzz.parkmkki.controller.EventController
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.FragmentAddEventBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Fragment for adding a new event.
 *
 * This fragment allows users to input event details and save them using the `EventController`.
 */
class AddEventFragment : Fragment() {

    /**
     * View binding for accessing UI elements in `fragment_add_event.xml`.
     */
    private var _binding: FragmentAddEventBinding? = null

    /**
     * Non-nullable binding property to ensure safe access.
     */
    private val binding get() = requireNotNull(_binding) {
        "Cannot access binding because it is null. Is the view visible?"
    }

    /**
     * Controller instance for managing event data.
     */
    private val eventController = EventController()

    /**
     * Date formatter for parsing user input dates.
     */
    private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    /**
     * Inflates the fragment's layout and initializes view binding.
     *
     * @param inflater LayoutInflater for inflating views.
     * @param container Parent container, or `null` if none.
     * @param savedInstanceState Previously saved instance state.
     * @return The root view of the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called after the view has been created.
     *
     * Sets up the UI, including handling button clicks for adding an event.
     *
     * @param view The fragment's root view.
     * @param savedInstanceState Saved instance state, if any.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the floating action button click listener for adding an event
        binding.fabAddEvent.setOnClickListener {
            if (validateInputs()) {
                try {
                    val date = LocalDate.parse(
                        binding.editTextEventDate.text.toString().trim(),
                        dateFormat
                    )

                    // Update event using the EventController
                    eventController.updateEvent(
                        binding.editTextEventName.text.toString().trim(),
                        binding.editTextEventLocation.text.toString().trim(),
                        date,
                        binding.editTextEventType.text.toString().trim(),
                        binding.editTextEventLocation.text.toString().trim()
                    )

                    // Show confirmation message
                    showMessage()
                } catch (e: Exception) {
                    Log.e("AddEventFragment", "Could not parse the date! Error: ${e.message}")
                }
            }
        }
    }

    /**
     * Validates user input fields to ensure they are not empty.
     *
     * @return `true` if all input fields are valid, otherwise `false`.
     */
    private fun validateInputs(): Boolean {
        return !binding.editTextEventName.text.isNullOrEmpty() &&
                !binding.editTextEventLocation.text.isNullOrEmpty() &&
                !binding.editTextEventDate.text.isNullOrEmpty() &&
                !binding.editTextEventType.text.isNullOrEmpty()
    }

    /**
     * Displays a confirmation message when an event is successfully added.
     */
    private fun showMessage() {
        val event = eventController.getEvent()
        Snackbar.make(binding.root, "Event added: ${event.toString()}", Snackbar.LENGTH_LONG).show()
        Log.d("AddEventFragment", event.toString())
    }

    /**
     * Cleans up the binding when the view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
