package com.apnamart.geofencingmodule.geofencing.core


import android.annotation.SuppressLint
import android.content.Context
import android.app.PendingIntent
import com.apnamart.geofencingmodule.geofencing.data.GeofenceData
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import android.content.Intent
import com.apnamart.geofencingmodule.geofencing.broadcast_receiver.GeofenceReceiver
import com.apnamart.geofencingmodule.geofencing.permissions.LocationPermissionHelper

/**
 * Implementation of the GeofenceManager interface.
 * This class handles the addition, removal, and management of geofences.
 */
class GeofenceManagerImpl(private val context: Context) : GeofenceManager {

    // Google Play Services GeofencingClient
    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    @SuppressLint("MissingPermission")
    override fun addGeofences(
        geofences: List<GeofenceData>,
        pendingIntent: PendingIntent,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Check if location permissions are granted
        if (LocationPermissionHelper.checkLocationPermissions(context)) {
            val geofenceList = createGeofenceList(geofences, onFailure)

            // Build the geofencing request
            val geofencingRequest = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geofenceList)
                .build()

            // Add the geofences to the geofencing client
            geofencingClient.addGeofences(geofencingRequest, pendingIntent).apply {
                addOnSuccessListener { onSuccess() } // Callback on success
                addOnFailureListener { onFailure(it) } // Callback on failure
            }
        } else {
          onFailure(IllegalStateException())
        }
    }

    override fun removeAllGeofences(pendingIntent: PendingIntent, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Remove geofences associated with the given PendingIntent
        geofencingClient.removeGeofences(pendingIntent).apply {
            addOnSuccessListener { onSuccess() } // Callback on success
            addOnFailureListener {
                // Handle failure if necessary
            }
        }
    }

    override fun removeAndAddGeofences(
        geofences: List<GeofenceData>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val pendingIntent = createPendingIntent(context, GeofenceReceiver::class.java, GeofenceConstants.GEO_LOCATION_INTENT_ACTION)
        // Remove all geofences and then add new ones
        removeAllGeofences(pendingIntent, {
            addGeofences(geofences, pendingIntent, onSuccess, onFailure)
        },{
            onFailure(it)
        })
    }

    /**
     * Converts a list of GeofenceData objects to a list of Geofence objects.
     *
     * @param geofences List of geofence data to be converted.
     * @param onFailure Callback invoked when there is a failure in creating a geofence.
     * @return List of Geofence objects.
     */
    private fun createGeofenceList(geofences: List<GeofenceData>, onFailure: (Exception) -> Unit): List<Geofence> {
        return geofences.mapNotNull { it.toGeofence(onFailure) }
    }

    private fun createPendingIntent(context: Context, receiverClass: Class<*>, action : String): PendingIntent {
        val intent = Intent(context, receiverClass)
        return PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
}
