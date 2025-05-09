/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki.controller

import dk.itu.moapd.copenhagenbuzz.parkmkki.model.Event
import dk.itu.moapd.copenhagenbuzz.parkmkki.model.EventLocation
import java.time.LocalDate

/**
 * **EventController** is responsible for managing an instance of the [Event] model.
 * It provides methods to update and retrieve event data.
 */
class EventController {

    /**
     * Updates the stored event instance with new details.
     *
     * @param name The name of the event.
     * @param location The location where the event takes place.
     * @param date The date of the event in [LocalDate] format.
     * @param type The type or category of the event.
     * @param description A brief description of the event.
     */
    fun createEvent(uid: String, name: String, location: EventLocation, date: LocalDate, type: String, description: String): Event {
        return Event(uid, name, location, date.toEpochDay(), type, description, "")
    }
}