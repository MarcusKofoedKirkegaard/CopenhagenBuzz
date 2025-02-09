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

    fun getEventName(): String {
        return eventName
    }

    fun setEventName(eventName: String) {
        this.eventName = eventName
    }

    fun getEventLocation(): String {
        return eventLocation
    }

    fun setEventLocation(eventLocation: String) {
        this.eventLocation = eventLocation
    }

    fun getEventDate(): LocalDate {
        return eventDate
    }

    fun setEventDate(eventDate: LocalDate) {
        this.eventDate = eventDate
    }

    fun getEventType(): String {
        return eventType
    }

    fun setEventType(eventType: String) {
        this.eventType = eventType
    }

    fun getEventDescription(): String {
        return eventDescription
    }

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