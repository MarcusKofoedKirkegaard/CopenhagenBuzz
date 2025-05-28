/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki

import android.app.Application
import com.google.android.material.color.DynamicColors

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}