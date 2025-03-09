/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki.model

import androidx.lifecycle.*
import dk.itu.moapd.copenhagenbuzz.parkmkki.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

/**
 * ViewModel for managing event data asynchronously using coroutines.
 */
class DataViewModel : ViewModel() {

    /**
     * Backing field for event data stored in LiveData
     */
    private val _eventData = MutableLiveData<List<Event>>()

    /**
     * Publicly exposed immutable LiveData containing a list of events.
     */
    val eventData: LiveData<List<Event>> get() = _eventData

    // LiveData to expose errors to the UI (if any)
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        loadEvents()
    }

    /**
     * Loads events asynchronously using coroutines to prevent blocking the UI thread.
     */
    private fun loadEvents() {
        viewModelScope.launch {
            try {
                // Perform the data loading on a background thread (IO)
                val events = withContext(Dispatchers.IO) {
                    fetchEventsFromDatabase() // Simulate fetching data
                }
                _eventData.postValue(events) // Update LiveData on the main thread
            } catch (e: Exception) {
                // Handle error (e.g., network error, database error)
                _errorMessage.postValue("Error loading events: ${e.message}")
            }
        }
    }

    /**
     * Simulates fetching events from a database or API.
     * This should ideally be replaced with a repository call.
     */
    private suspend fun fetchEventsFromDatabase(): List<Event> {
        return listOf(
            Event("Concert Night", "Copenhagen", LocalDate.now(), "Concert", "Enjoy live music at the city square", R.drawable.test_image),
            Event("Food Festival", "Copenhagen", LocalDate.now(), "Festival", "Taste delicious street food from local vendors", R.drawable.test_image),
            Event("Art Exhibition", "Copenhagen", LocalDate.now(), "Exhibition", "Explore the latest contemporary art collections", R.drawable.test_image)
        )
    }

    /**
     * Adds a new event asynchronously.
     *
     * @param event The event to be added.
     */
    fun addEvent(event: Event) {
        viewModelScope.launch {
            try {
                // Simulate adding an event asynchronously
                val updatedList = _eventData.value.orEmpty().toMutableList()
                updatedList.add(event)

                // Simulate potential failure
                if (Math.random() > 0.7) {
                    throw Exception("Simulated error while adding event.")
                }

                _eventData.postValue(updatedList) // Update LiveData
            } catch (e: Exception) {
                // Handle error while adding event
                _errorMessage.postValue("Error adding event: ${e.message}")
            }
        }
    }
}