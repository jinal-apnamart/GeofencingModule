package com.apnamart.geofencingmodule.geofencing.worker

import android.content.Context
import android.content.IntentFilter
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.apnamart.geofencingmodule.geofencing.broadcast_receiver.GeofenceReceiver
import com.apnamart.geofencingmodule.geofencing.core.GeofenceConstants
import com.apnamart.geofencingmodule.geofencing.core.GeofenceManager
import com.apnamart.geofencingmodule.geofencing.core.GeofenceManagerImpl
import com.apnamart.geofencingmodule.geofencing.data.GeofenceData
import com.apnamart.geofencingmodule.geofencing.permissions.LocationPermissionHelper
import com.apnamart.geofencingmodule.geofencing.provider.GeofenceDataProvider
import com.google.android.gms.location.Geofence

class GeofenceWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    val geofenceManager: GeofenceManager = GeofenceManagerImpl(context)
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {

        Log.d("GeofencingWorker", "worker running")
        if (!LocationPermissionHelper.checkLocationPermissions(context)) {
            return Result.success()
        }

//        val geofenceDataList =  listOf(
//            GeofenceData(
//                requestId = AppConstants.MARK_OUT_RADIUS,
//                radius = cacheData.markOutThreshold,
//                latitude = cacheData.latitude,
//                longitude = cacheData.longitude,
//                transitionType = Geofence.GEOFENCE_TRANSITION_EXIT
//            ),
//            GeofenceData(
//                requestId = AppConstants.REACHED_STORE_RADIUS,
//                radius = cacheData.radius,
//                latitude = cacheData.latitude,
//                longitude = cacheData.longitude,
//                delay = 1 * 1000,
//                transitionType = Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT
//            )
//        )

//        geofenceManager.removeAndAddGeofences(
//            geofenceDataList,
//            onSuccess = {
//
//            },
//            onFailure = {
//
//            },
//        )

        ContextCompat.registerReceiver(
            context,
            GeofenceReceiver(),
            IntentFilter(GeofenceConstants.GEO_LOCATION_INTENT_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        return Result.success()
    }

}
class GeofenceWorkerFactory {

    companion object {
        fun create(
            context: Context,
            workerParams: WorkerParameters,
            locationPermissionHelper: LocationPermissionHelper,
            geofenceDataProvider: GeofenceDataProvider,
            geofenceManager: GeofenceManager
        ): GeofenceWorker {
            return GeofenceWorker(
                context,
                workerParams,
                geofenceManager
            )
        }
    }
}
