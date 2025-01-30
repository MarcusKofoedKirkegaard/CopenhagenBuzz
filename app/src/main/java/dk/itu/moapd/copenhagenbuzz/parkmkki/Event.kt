package dk.itu.moapd.copenhagenbuzz.parkmkki

class Event {
    private var eventName: String
    private var eventLocation: String

    constructor(eventName: String, eventLocation: String) {
        this.eventName = eventName
        this.eventLocation = eventLocation

    // Initialize the missing attributes .
    }

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


    // Implement the missing accessors and mutators methods .
    override fun toString(): String {
        return " Event ( eventName =' $eventName ', eventLocation =' $eventLocation ')"
    }
}