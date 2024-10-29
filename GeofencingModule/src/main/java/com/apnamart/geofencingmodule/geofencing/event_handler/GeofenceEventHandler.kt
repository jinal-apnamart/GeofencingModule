package com.apnamart.geofencingmodule.geofencing.event_handler

import com.apnamart.geofencingmodule.geofencing.data.GeofenceData

interface GeofenceEventHandler {
    fun onGeofenceEntered(geofenceList: List<GeofenceData>)
    fun onGeofenceExited(geofenceList: List<GeofenceData>)
    fun onGeofenceDwelled(geofenceList: List<GeofenceData>)
    fun onGeofenceError(errorMessage: String)
}
