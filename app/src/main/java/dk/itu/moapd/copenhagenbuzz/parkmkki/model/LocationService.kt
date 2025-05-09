package dk.itu.moapd.copenhagenbuzz.parkmkki.model

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

/**
 * A service that provides the user's current location in the foreground.
 * It uses Google's FusedLocationProviderClient to fetch the location and broadcasts it locally.
 */
class LocationService : Service() {

    /**
     * Binder class to allow binding the service to other components, such as an activity.
     * It provides access to the service instance.
     */
    inner class LocalBinder : Binder() {
        internal val service: LocationService
            get() = this@LocationService // Returns the instance of LocationService
    }

    private val localBinder = LocalBinder() // LocalBinder instance to provide service access
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient // Client to get location
    private lateinit var locationCallback: LocationCallback // Callback to handle location updates

    companion object {
        // The package name used to uniquely identify actions and extras.
        private const val PACKAGE_NAME = "dk.itu.moapd.copenhagenbuzz.parkmkki.model"

        // Action used to broadcast location updates.
        internal const val ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST = "$PACKAGE_NAME.action.FOREGROUND_ONLY_LOCATION_BROADCAST"

        // Extra key for passing the location object in the intent.
        internal const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"
    }

    /**
     * Called when the service is created. Initializes the location client and callback.
     */
    override fun onCreate() {
        super.onCreate()

        // Initialize the FusedLocationProviderClient to request location updates
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Create a location callback to handle location updates
        locationCallback = object : LocationCallback() {
            /**
             * Called when a location result is received.
             * This will be triggered every time the location is updated.
             *
             * @param locationResult The result containing the last location update.
             */
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                // Get the most recent location from the result
                val currentLocation = locationResult.lastLocation

                // Create an intent to broadcast the location update
                val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
                intent.putExtra(EXTRA_LOCATION, currentLocation) // Add location as an extra

                // Send the broadcast with the location update
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
        }
    }

    /**
     * Called when an activity binds to the service.
     * It returns the binder so that the activity can interact with the service.
     *
     * @param intent The intent used to bind to the service.
     * @return The binder that allows interaction with the service.
     */
    override fun onBind(intent: Intent): IBinder {
        return localBinder // Return the binder to allow clients to bind and communicate with the service
    }
}
