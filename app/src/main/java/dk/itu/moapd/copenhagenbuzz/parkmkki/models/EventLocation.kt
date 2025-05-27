/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventLocation(
    val latitude: Double =0.0,
    val longitude: Double =0.0,
    val address: String="",
) : Parcelable