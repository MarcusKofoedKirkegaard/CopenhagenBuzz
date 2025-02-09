package dk.itu.moapd.copenhagenbuzz.parkmkki.controller

import dk.itu.moapd.copenhagenbuzz.parkmkki.model.Event
import java.time.LocalDate

class EventController {

    private val event = Event("", "", LocalDate.now(), "", "")

    fun updateEvent(name: String, location: String, date: LocalDate, type: String, description: String) {
        event.eventName = name
        event.eventLocation = location
        event.eventDate = date
        event.eventType = type
        event.eventDescription = description
    }

    fun getEvent(): Event {
        return event
    }
}