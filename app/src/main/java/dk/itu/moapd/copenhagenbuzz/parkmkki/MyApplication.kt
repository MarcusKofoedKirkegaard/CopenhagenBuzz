package dk.itu.moapd.copenhagenbuzz.parkmkki

import android.app.Application
import com.google.android.material.color.DynamicColors


class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}