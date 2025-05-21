/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki.models

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
    val eventCreator: String ="",
    val eventName: String ="",
    val eventLocation: EventLocation = EventLocation(0.0, 0.0, "Unknown"),
    val eventDate: Long=0,
    val eventType: String="",
    val eventDescription: String="",
    var eventImagePath: String="",
    var eventThumbsUp: Int=0
) {


    /**
     * Prints the Event information
     *
     * @return String of Event information
     */
    override fun toString(): String {
        return " Event ( eventName =' $eventName ', " +
                "eventLocation =' $eventLocation ', " +
                "eventDate =' $eventDate ', " +
                "eventType =' $eventType ', " +
                "eventDescription =' $eventDescription ')"
    }
}