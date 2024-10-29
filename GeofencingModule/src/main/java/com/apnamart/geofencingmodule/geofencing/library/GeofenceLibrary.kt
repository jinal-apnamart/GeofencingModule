package com.apnamart.geofencingmodule.geofencing.library

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.apnamart.geofencingmodule.geofencing.core.GeofenceConstants
import com.apnamart.geofencingmodule.geofencing.core.GeofenceManager
import com.apnamart.geofencingmodule.geofencing.data.GeofenceData
import com.apnamart.geofencingmodule.geofencing.event_handler.GeofenceEventHandler
import com.apnamart.geofencingmodule.geofencing.provider.GeofenceDataProvider
import com.apnamart.geofencingmodule.geofencing.permissions.LocationPermissionHelper
import com.apnamart.geofencingmodule.geofencing.worker.GeofenceWorker
import java.util.concurrent.TimeUnit

object GeofenceLibrary {

    private lateinit var dataProvider: GeofenceDataProvider
    private lateinit var eventHandler: GeofenceEventHandler

    fun initialize(
        context: Context,
        dataProvider: GeofenceDataProvider,
        eventHandler: GeofenceEventHandler
    ) {
        this.dataProvider = dataProvider
        this.eventHandler = eventHandler

        // Schedule the GeofenceWorker to run every 15 minutes
        scheduleGeofenceWorker(context)
    }

    private fun scheduleGeofenceWorker(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Create the worker request with the factory method
        val workRequest = PeriodicWorkRequestBuilder<GeofenceWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag(GeofenceConstants.GEOFENCE_SERVICE_WORKER_JOB)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            GeofenceConstants.GEOFENCE_SERVICE_WORKER,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    // Function to get the event handler for geofence transitions
    fun getEventHandler(): GeofenceEventHandler {
        return eventHandler
    }



    fun schedulePeriodicWorkerWithConstraints(
        workManager: WorkManager,
        tag: String,
        workerName: String,
        existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy,
        duration: Pair<Long, TimeUnit>,
        flexDuration: Pair<Long, TimeUnit>,
        workerClass: Class<out CoroutineWorker>,
        constraints: Constraints,
    ) {
        val worker =
            PeriodicWorkRequest.Builder(
                workerClass, duration.first,
                duration.second, flexDuration.first,
                flexDuration.second,
            ).addTag(tag).setConstraints(constraints).build()

        workManager.enqueueUniquePeriodicWork(
            workerName, existingPeriodicWorkPolicy, worker
        )
    }
}
