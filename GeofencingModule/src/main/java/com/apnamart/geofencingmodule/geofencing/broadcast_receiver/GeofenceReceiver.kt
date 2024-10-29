package com.apnamart.geofencingmodule.geofencing.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.apnamart.geofencingmodule.geofencing.data.GeofenceData
import com.apnamart.geofencingmodule.geofencing.event_handler.GeofenceEventHandler
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver() : BroadcastReceiver() {

    private val eventHandler: GeofenceEventHandler? = null
    init {
//        eventHandler = GeofenceEventHandler()
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Handle the received broadcast
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent?.hasError() == true) {
            // Handle error
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            eventHandler?.onGeofenceError(errorMessage)
            return
        }

        // Get the triggering geofence IDs
        val triggeringGeofences = geofencingEvent?.triggeringGeofences
        val transitionType = geofencingEvent?.geofenceTransition

        triggeringGeofences?.let {
            val geofenceList = mutableListOf<GeofenceData>()
            for (geofence in triggeringGeofences) {
               geofenceList.add(GeofenceData(
                    requestId = geofence.requestId,
                    latitude = geofence.latitude,
                    longitude = geofence.longitude,
                    radius = geofence.radius,
                    transitionType = transitionType ?: 0
                )
               )
            }
            handleGeofenceTransition(transitionType ?: 0, geofenceList)
        }
    }

    private fun handleGeofenceTransition(transitionType: Int, geofenceData: List<GeofenceData>) {
        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                eventHandler?.onGeofenceEntered(geofenceData)
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                eventHandler?.onGeofenceExited(geofenceData)
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                eventHandler?.onGeofenceDwelled(geofenceData)
            }
            else -> {
                Log.e(TAG, "Unknown geofence transition type: $transitionType")
            }
        }
    }

    companion object {
        private const val TAG = "GeofenceReceiver"
    }
}
