/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki

import java.time.LocalDate

/**
 * Event data class
 *
 * @param eventName
 * @param eventLocation
 * @param eventDate
 * @param eventType
 * @param eventDescription
 *
 *@see "https://kotlinlang.org/docs/data-classes.html#properties-declared-in-the-class-body"
 */
data class Event(
    var eventName: String,
    var eventLocation: String,
    var eventDate: LocalDate,
    var eventType: String,
    var eventDescription: String
) {
    /**
     * @return eventName: String
     */
    fun getEventName(): String {
        return eventName
    }
    /**
     * @param eventName
     */
    fun setEventName(eventName: String) {
        this.eventName = eventName
    }

    /**
     * @return eventLocation: String
     */
    fun getEventLocation(): String {
        return eventLocation
    }

    /**
     * @param eventLocation
     */
    fun setEventLocation(eventLocation: String) {
        this.eventLocation = eventLocation
    }

    /**
     * @return eventDate: LocalDate
     */
    fun getEventDate(): LocalDate {
        return eventDate
    }

    /**
     * @param eventDate
     */
    fun setEventDate(eventDate: LocalDate) {
        this.eventDate = eventDate
    }

    /**
     * @return eventType: String
     */
    fun getEventType(): String {
        return eventType
    }

    /**
     * @param eventType
     */
    fun setEventType(eventType: String) {
        this.eventType = eventType
    }

    /**
     * @return eventName: String
     */
    fun getEventDescription(): String {
        return eventDescription
    }

    /**
     * @param eventDescription
     */
    fun setEventDescription(eventDescription: String) {
        this.eventDescription = eventDescription
    }

    /**
     * Prints the Event information
     *
     * @return String of Event information
     */
    override fun toString(): String {
        return " Event ( eventName =' $eventName ', " +
                "eventLocation =' $eventLocation ', " +
                "eventDate =' ${eventDate.toString()} ', " +
                "eventType =' $eventType ', " +
                "eventDescription =' $eventDescription ')"
    }
}