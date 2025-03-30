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
    val eventName: String ="",
    val eventLocation: String="",
    val eventDate: LocalDate,
    val eventType: String="",
    val eventDescription: String="",
    val eventImageId: Int,
    var isFavorite: Boolean = false
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