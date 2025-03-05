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

class AddEventFragment : Fragment() {

    private var _binding: FragmentAddEventBinding? = null
    private val binding get() = requireNotNull(_binding) {
        "Cannot access binding because it is null. Is the view visible?"
    }

    private val eventController = EventController() // Use controller
    private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //return inflater.inflate(R.layout.fragment_add_event, container, false)
        _binding = FragmentAddEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddEvent.setOnClickListener {
            if (validateInputs()) {
                try {
                    val date = LocalDate.parse(binding.editTextEventDate.text.toString().trim(), dateFormat)
                    eventController.updateEvent(
                        binding.editTextEventName.text.toString().trim(),
                        binding.editTextEventLocation.text.toString().trim(),
                        date,
                        binding.editTextEventType.text.toString().trim(),
                        binding.editTextEventLocation.text.toString().trim()
                    )
                    showMessage()
                } catch (e: Exception) {
                    Log.e("AddEventFragment", "Could not parse the date! Error: ${e.message}")
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        return !binding.editTextEventName.text.isNullOrEmpty() &&
                !binding.editTextEventLocation.text.isNullOrEmpty() &&
                !binding.editTextEventDate.text.isNullOrEmpty() &&
                !binding.editTextEventType.text.isNullOrEmpty()
    }

    private fun showMessage() {
        val event = eventController.getEvent()
        Snackbar.make(binding.root, "Event added: ${event.toString()}", Snackbar.LENGTH_LONG).show()
        Log.d("AddEventFragment", event.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
