/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki.controller

import dk.itu.moapd.copenhagenbuzz.parkmkki.model.Event
import java.time.LocalDate

/**
 * **EventController** is responsible for managing an instance of the [Event] model.
 * It provides methods to update and retrieve event data.
 */
class EventController {

    /**
     * A single instance of an [Event] used for temporary storage.
     * Defaults to an empty event with the current date.
     */
    private val event = Event("", "", LocalDate.now(), "", "", 0)

    /**
     * Updates the stored event instance with new details.
     *
     * @param name The name of the event.
     * @param location The location where the event takes place.
     * @param date The date of the event in [LocalDate] format.
     * @param type The type or category of the event.
     * @param description A brief description of the event.
     */
    fun updateEvent(name: String, location: String, date: LocalDate, type: String, description: String) {
        event.eventName = name
        event.eventLocation = location
        event.eventDate = date
        event.eventType = type
        event.eventDescription = description
    }

    /**
     * Retrieves the current event instance.
     *
     * @return The stored [Event] object containing the latest event details.
     */
    fun getEvent(): Event {
        return event
    }
}