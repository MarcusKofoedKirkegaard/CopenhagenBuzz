/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import dk.itu.moapd.copenhagenbuzz.parkmkki.R
import java.time.LocalDate

/**
 * ViewModel for managing event data.
 *
 * This ViewModel acts as a communication layer between the UI and data sources.
 * It holds and manages event-related LiveData, ensuring data survives configuration changes.
 */
class DataViewModel : ViewModel() {

    /**
     * Backing field for event data stored in LiveData.
     * Initially populated with sample events.
     */
    private val _eventData = MutableLiveData<List<Event>>().apply {
        value = listOf(
            Event("Concert Night", "copenhagen", LocalDate.now(), "Concert", "Enjoy live music at the city square", R.drawable.test_image),
            Event("Food Festival", "copenhagen", LocalDate.now(), "Concert", "Taste delicious street food from local vendors", R.drawable.test_image),
            Event("Art Exhibition", "copenhagen", LocalDate.now(), "Concert", "Explore the latest contemporary art collections", R.drawable.test_image)
        )
    }

    /**
     * Publicly exposed immutable LiveData containing a list of events.
     * Observers can subscribe to receive updates when event data changes.
     */
    val eventData: LiveData<List<Event>> get() = _eventData
}