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
    private val _eventData = MutableLiveData<MutableList<Event>>()
    private val _favoritesData = MutableLiveData<MutableList<Event>>()

    /**
     * Publicly exposed immutable LiveData containing a list of events.
     */
    val eventData: LiveData<MutableList<Event>> get() = _eventData
    val favoritesData: LiveData<MutableList<Event>> get() = _favoritesData

    // LiveData to expose errors to the UI (if any)
    private val _errorMessage = MutableLiveData<String?>()

    init {
        loadEvents()
    }

    /**
     * Updates the favorite status of an event.
     */
    fun updateEvent(updatedEvent: Event) {
        // Get the current list of events
        val events = _eventData.value

        // Find the event by its ID and update the favorite status if it exists
        events?.let {
            val index = it.indexOfFirst { event -> event.eventName == updatedEvent.eventName }
            if (index != -1) {
                it[index] = updatedEvent // Update the event

                // Post the updated list to LiveData without replacing the entire list
                _eventData.postValue(it)

                // Update the favorites list based on the current state of events
                val favoritedEvents = it.filter { it.isFavorite }.toMutableList()
                _favoritesData.postValue(favoritedEvents)
            }
        }

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
                val favorites = withContext(Dispatchers.IO) {
                    fetchFavoritesFromDatabase()
                }
                _favoritesData.postValue(favorites) // Update LiveData on the main thread
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
    private fun fetchEventsFromDatabase(): MutableList<Event> {
        return mutableListOf(
            Event("Concert Night", "Copenhagen", LocalDate.now(), "Concert", "Enjoy live music at the city square", R.drawable.test_image, true),
            Event("Food Festival", "Copenhagen", LocalDate.now(), "Festival", "Taste delicious street food from local vendors", R.drawable.test_image, false),
            Event("Art Exhibition", "Copenhagen", LocalDate.now(), "Exhibition", "Explore the latest contemporary art collections", R.drawable.test_image, false)
        )
    }

    private fun fetchFavoritesFromDatabase(): MutableList<Event> {
        return fetchEventsFromDatabase().filter { it.isFavorite }.toMutableList()
    }
}