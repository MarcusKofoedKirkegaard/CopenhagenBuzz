/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki.model

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
    var eventName: String ="",
    var eventLocation: String="",
    var eventDate: LocalDate,
    var eventType: String="",
    var eventDescription: String="",
    var eventImageId: Int
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