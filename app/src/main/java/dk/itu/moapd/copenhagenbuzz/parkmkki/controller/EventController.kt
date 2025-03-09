package dk.itu.moapd.copenhagenbuzz.parkmkki.controller

import dk.itu.moapd.copenhagenbuzz.parkmkki.model.Event
import java.time.LocalDate

/**
 * Event Controller to manage the event class
 */
class EventController {

    private val event = Event("", "", LocalDate.now(), "", "", 0)

    /**
     * Update event
     *
     * @param name
     * @param location
     * @param date
     * @param type
     * @param description
     */
    fun updateEvent(name: String, location: String, date: LocalDate, type: String, description: String) {
        event.eventName = name
        event.eventLocation = location
        event.eventDate = date
        event.eventType = type
        event.eventDescription = description
    }

    /**
     * Gets event
     *
     * @return Event
     */
    fun getEvent(): Event {
        return event
    }
}