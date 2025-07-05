package com.example.rooster.feature.iot.data.repository

import android.util.Log
import com.example.rooster.core.common.Result
import com.example.rooster.feature.iot.data.local.SensorDataDao
import com.example.rooster.feature.iot.data.local.toDomain
import com.example.rooster.feature.iot.data.local.toEntity
import com.example.rooster.feature.iot.data.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "IoTRepositoryImpl"

// Define Firebase paths (these are examples and need to match actual DB structure)
private const val FB_DEVICES_PATH = "iot_devices"
private const val FB_READINGS_PATH_PREFIX = "iot_readings" // e.g., iot_readings/temperature/deviceId
private const val FB_ALERTS_PATH = "iot_alerts"
private const val FB_DEVICE_CONFIGS_PATH = "iot_device_configs"

// Placeholder paths for analytics data
private const val FB_PRODUCTION_FORECASTS_PATH = "production_forecasts"
private const val FB_PERFORMANCE_PREDICTIONS_PATH = "performance_predictions"
private const val FB_FEED_RECOMMENDATIONS_PATH = "feed_recommendations"
private const val FB_HEALTH_TRENDS_PATH = "health_trends"

// Placeholder paths for smart automation settings
private const val FB_FEEDING_SCHEDULES_PATH = "feeding_schedules"
private const val FB_CLIMATE_SETTINGS_PATH = "climate_settings"
private const val FB_MAINTENANCE_REMINDERS_PATH = "maintenance_reminders"


@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class IoTRepositoryImpl @Inject constructor(
    private val localDataSource: SensorDataDao,
    private val firestore: FirebaseFirestore, // For structured data like device info, configs, alerts
    private val firebaseRtdb: FirebaseDatabase // For high-frequency sensor readings if preferred
) : IoTRepository {

    // --- Device Info ---
    override fun getAllDeviceInfos(): Flow<Result<List<DeviceInfo>>> = callbackFlow {
        trySend(Result.Loading)
        val listener = firestore.collection(FB_DEVICES_PATH)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(Exception("Error fetching device infos: ${error.message}", error)))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val devices = snapshot.toObjects<DeviceInfo>()
                    // Update local cache
                    // GlobalScope.launch { localDataSource.insertDeviceInfos(devices.map { it.toEntity() }) }
                    trySend(Result.Success(devices))
                } else {
                     trySend(Result.Error(Exception("No device data found")))
                }
            }
        awaitClose { listener.remove() }
    }.catch { e -> emit(Result.Error(Exception("Flow error in getAllDeviceInfos: ${e.message}", e))) }
    // Fallback to local cache if needed, or combine with networkBoundResource pattern

    override fun getDeviceInfo(deviceId: String): Flow<Result<DeviceInfo?>> = callbackFlow {
        trySend(Result.Loading)
        val listener = firestore.collection(FB_DEVICES_PATH).document(deviceId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(Exception("Error fetching device info for $deviceId: ${error.message}", error)))
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val device = snapshot.toObject<DeviceInfo>()
                    // GlobalScope.launch { device?.let { localDataSource.insertDeviceInfos(listOf(it.toEntity())) } }
                    trySend(Result.Success(device))
                } else {
                    trySend(Result.Success(null)) // Or Result.Error if device must exist
                }
            }
        awaitClose { listener.remove() }
    }.catch { e -> emit(Result.Error(Exception("Flow error in getDeviceInfo: ${e.message}", e))) }

    override suspend fun refreshDeviceInfos() {
        try {
            val snapshot = firestore.collection(FB_DEVICES_PATH).get().await()
            val devices = snapshot.toObjects<DeviceInfoEntity>()
            localDataSource.insertDeviceInfos(devices)
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing device infos: ${e.message}", e)
            // Optionally rethrow or handle as Result.Error if this method returned Result
        }
    }

    // --- Sensor Readings ---
    // Generic helper for sensor readings from RTDB (example)
    private inline fun <reified T : BaseReading, reified E : Any> getReadingsFlow(
        deviceId: String,
        readingTypePath: String, // e.g., "temperature"
        crossinline localQuery: (String) -> Flow<List<E>>,
        crossinline entityToDomain: (E) -> T
    ): Flow<Result<List<T>>> = callbackFlow<Result<List<T>>> {
        trySend(Result.Loading)
        val dbRef = firebaseRtdb.getReference("$FB_READINGS_PATH_PREFIX/$readingTypePath/$deviceId")
            .orderByChild("timestamp") // Assuming readings have a 'timestamp' field
            .limitToLast(100) // Example: Get last 100 readings

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val readings = snapshot.children.mapNotNull { it.getValue(T::class.java) }
                // GlobalScope.launch { localDataSource.insert... (readings.map { it.toEntity() }) } // Specific insert
                trySend(Result.Success(readings))
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(Result.Error(Exception("Error fetching $readingTypePath for $deviceId: ${error.message}", error.toException())))
            }
        }
        dbRef.addValueEventListener(listener)
        awaitClose { dbRef.removeEventListener(listener) }
    }.catch { e -> emit(Result.Error(Exception("Flow error in getReadingsFlow for $readingTypePath: ${e.message}", e))) }
    // This is a basic RTDB listener. For robust offline support, combine with local cache (networkBoundResource)

    // Example for Temperature
    override fun getTemperatureReadings(deviceId: String): Flow<Result<List<TemperatureReading>>> =
        getReadingsFlow(deviceId, "temperature", localDataSource::getTemperatureReadingsForDevice) { (it as TemperatureReadingEntity).toDomain() }
        // This needs refinement if localQuery is Flow<List<Entity>> and T is Domain.
        // The above getReadingsFlow is simplified. A proper NetworkBoundResource pattern is better.
        // For now, this is a placeholder for direct Firebase listening.

    // Placeholder for historical data from local cache
    override fun getTemperatureReadingsInRange(deviceId: String, startTime: Long, endTime: Long): Flow<Result<List<TemperatureReading>>> {
        return localDataSource.getTemperatureReadingsForDeviceInRange(deviceId, startTime, endTime)
            .map { entities -> Result.Success(entities.map { it.toDomain() }) }
            .catch { e -> emit(Result.Error(Exception("Error fetching temp from local: ${e.message}", e))) }
    }
     override suspend fun refreshTemperatureReadings(deviceId: String, fromRemote: Boolean) {
        if (fromRemote) {
            try {
                // Fetch from RTDB/Firestore and update localDataSource.insertTemperatureReadings(...)
                // Example: Fetch last N readings from RTDB for simplicity
                val snapshot = firebaseRtdb.getReference("$FB_READINGS_PATH_PREFIX/temperature/$deviceId")
                                .orderByChild("timestamp").limitToLast(200).get().await()
                val readings = snapshot.children.mapNotNull { it.getValue(TemperatureReading::class.java) }
                localDataSource.insertTemperatureReadings(readings.map { it.toEntity() })
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing temperature readings for $deviceId: ${e.message}", e)
            }
        }
    }

    // Implement other sensor reading methods (Humidity, FeedLevel, etc.) similarly...
    // For brevity, only Temperature is sketched out. Others would follow the same pattern.

    override fun getHumidityReadings(deviceId: String): Flow<Result<List<HumidityReading>>> =
        getReadingsFlow(deviceId, "humidity", localDataSource::getHumidityReadingsForDevice) { (it as com.example.rooster.feature.iot.data.local.HumidityReadingEntity).toDomain()}

    override fun getHumidityReadingsInRange(deviceId: String, startTime: Long, endTime: Long): Flow<Result<List<HumidityReading>>> =
        localDataSource.getHumidityReadingsForDeviceInRange(deviceId, startTime, endTime)
            .map { entities -> Result.Success(entities.map { it.toDomain() }) }
            .catch { e -> emit(Result.Error(Exception("Error fetching humidity from local: ${e.message}", e))) }

    override suspend fun refreshHumidityReadings(deviceId: String, fromRemote: Boolean) {
         if (fromRemote) {
            try {
                val snapshot = firebaseRtdb.getReference("$FB_READINGS_PATH_PREFIX/humidity/$deviceId")
                                .orderByChild("timestamp").limitToLast(200).get().await()
                val readings = snapshot.children.mapNotNull { it.getValue(HumidityReading::class.java) }
                localDataSource.insertHumidityReadings(readings.map { it.toEntity() })
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing humidity readings for $deviceId: ${e.message}", e)
            }
        }
    }


    override fun getFeedLevelReadings(deviceId: String): Flow<Result<List<FeedLevelReading>>> =
        getReadingsFlow(deviceId, "feed_level", localDataSource::getFeedLevelReadingsForDevice) { (it as com.example.rooster.feature.iot.data.local.FeedLevelReadingEntity).toDomain()}

    override fun getFeedLevelReadingsInRange(deviceId: String, startTime: Long, endTime: Long): Flow<Result<List<FeedLevelReading>>> =
        localDataSource.getFeedLevelReadingsForDeviceInRange(deviceId, startTime, endTime)
            .map { entities -> Result.Success(entities.map { it.toDomain() }) }
            .catch { e -> emit(Result.Error(Exception("Error fetching feed_level from local: ${e.message}", e))) }

    override suspend fun refreshFeedLevelReadings(deviceId: String, fromRemote: Boolean) {
        if (fromRemote) {
            try {
                val snapshot = firebaseRtdb.getReference("$FB_READINGS_PATH_PREFIX/feed_level/$deviceId")
                                .orderByChild("timestamp").limitToLast(200).get().await()
                val readings = snapshot.children.mapNotNull { it.getValue(FeedLevelReading::class.java) }
                localDataSource.insertFeedLevelReadings(readings.map { it.toEntity() })
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing feed_level readings for $deviceId: ${e.message}", e)
            }
        }
    }

    override fun getWaterConsumptionReadings(deviceId: String): Flow<Result<List<WaterConsumptionReading>>> =
        getReadingsFlow(deviceId, "water_consumption", localDataSource::getWaterConsumptionReadingsForDevice) { (it as com.example.rooster.feature.iot.data.local.WaterConsumptionReadingEntity).toDomain()}

    override fun getWaterConsumptionReadingsInRange(deviceId: String, startTime: Long, endTime: Long): Flow<Result<List<WaterConsumptionReading>>> =
        localDataSource.getWaterConsumptionReadingsForDeviceInRange(deviceId, startTime, endTime)
            .map { entities -> Result.Success(entities.map { it.toDomain() }) }
            .catch { e -> emit(Result.Error(Exception("Error fetching water_consumption from local: ${e.message}", e))) }

    override suspend fun refreshWaterConsumptionReadings(deviceId: String, fromRemote: Boolean) {
        if (fromRemote) {
            try {
                val snapshot = firebaseRtdb.getReference("$FB_READINGS_PATH_PREFIX/water_consumption/$deviceId")
                                .orderByChild("timestamp").limitToLast(200).get().await()
                val readings = snapshot.children.mapNotNull { it.getValue(WaterConsumptionReading::class.java) }
                localDataSource.insertWaterConsumptionReadings(readings.map { it.toEntity() })
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing water_consumption readings for $deviceId: ${e.message}", e)
            }
        }
    }

    override fun getLightLevelReadings(deviceId: String): Flow<Result<List<LightLevelReading>>> =
        getReadingsFlow(deviceId, "light_level", localDataSource::getLightLevelReadingsForDevice) { (it as com.example.rooster.feature.iot.data.local.LightLevelReadingEntity).toDomain()}

    override fun getLightLevelReadingsInRange(deviceId: String, startTime: Long, endTime: Long): Flow<Result<List<LightLevelReading>>> =
        localDataSource.getLightLevelReadingsForDeviceInRange(deviceId, startTime, endTime)
            .map { entities -> Result.Success(entities.map { it.toDomain() }) }
            .catch { e -> emit(Result.Error(Exception("Error fetching light_level from local: ${e.message}", e))) }

    override suspend fun refreshLightLevelReadings(deviceId: String, fromRemote: Boolean) {
        if (fromRemote) {
            try {
                val snapshot = firebaseRtdb.getReference("$FB_READINGS_PATH_PREFIX/light_level/$deviceId")
                                .orderByChild("timestamp").limitToLast(200).get().await()
                val readings = snapshot.children.mapNotNull { it.getValue(LightLevelReading::class.java) }
                localDataSource.insertLightLevelReadings(readings.map { it.toEntity() })
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing light_level readings for $deviceId: ${e.message}", e)
            }
        }
    }


    // --- Alerts ---
    override fun getAllAlerts(): Flow<Result<List<AlertInfo>>> = callbackFlow {
        trySend(Result.Loading)
        val listener = firestore.collection(FB_ALERTS_PATH).orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(Exception("Error fetching alerts: ${error.message}", error)))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val alerts = snapshot.toObjects<AlertInfo>()
                    // GlobalScope.launch { localDataSource.insertAlerts(alerts.map { it.toEntity() }) }
                    trySend(Result.Success(alerts))
                }
            }
        awaitClose { listener.remove() }
    }.catch { e -> emit(Result.Error(Exception("Flow error in getAllAlerts: ${e.message}", e))) }


    override fun getUnacknowledgedAlerts(): Flow<Result<List<AlertInfo>>> {
        // Example: Could be Firestore query `whereEqualTo("acknowledged", false)`
        // Or combine local and remote, or just use local if alerts are synced reliably.
        return localDataSource.getUnacknowledgedAlerts()
            .map { entities -> Result.Success(entities.map { it.toDomain() }) }
            .catch { e -> emit(Result.Error(Exception("Error fetching unacknowledged alerts from local: ${e.message}", e))) }
    }

    override suspend fun refreshAlerts(fromRemote: Boolean) {
        if(fromRemote) {
            try {
                val snapshot = firestore.collection(FB_ALERTS_PATH).get().await()
                val alerts = snapshot.toObjects<AlertInfoEntity>()
                localDataSource.insertAlerts(alerts)
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing alerts: ${e.message}", e)
            }
        }
    }

    override suspend fun acknowledgeAlert(alertId: String): Result<Unit> {
        return try {
            firestore.collection(FB_ALERTS_PATH).document(alertId)
                .update("acknowledged", true).await()
            // Also update local
            val localAlert = localDataSource.getAllAlerts().first().find { it.alertId == alertId } // This is inefficient
            localAlert?.let {
                localDataSource.updateAlert(it.copy(acknowledged = true))
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error acknowledging alert $alertId: ${e.message}", e)
            Result.Error(e)
        }
    }

    override suspend fun recordAlert(alertInfo: AlertInfo): Result<Unit> {
        return try {
            // Save to local first
            localDataSource.insertAlerts(listOf(alertInfo.toEntity()))
            // Then save to remote
            firestore.collection(FB_ALERTS_PATH).document(alertInfo.alertId)
                .set(alertInfo).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error recording alert ${alertInfo.alertId}: ${e.message}", e)
            Result.Error(e)
        }
    }


    // --- Device Configuration ---
    override fun getDeviceConfig(deviceId: String): Flow<Result<DeviceConfig?>> {
        // Example: Prioritize local, then fetch from remote if not found or stale.
        // For simplicity, just local:
        return localDataSource.getDeviceConfig(deviceId)
            .map { entity -> Result.Success(entity?.toDomain()) }
            .catch { e -> emit(Result.Error(Exception("Error fetching device config $deviceId from local: ${e.message}", e))) }
    }

    override suspend fun updateDeviceConfig(config: DeviceConfig): Result<Unit> {
        return try {
            val entity = config.copy(needsSync = true).toEntity()
            localDataSource.insertDeviceConfig(entity) // Insert or update local
            // If online, could attempt to sync immediately or rely on a background worker
            // For now, just mark as needsSync
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating device config ${config.deviceId}: ${e.message}", e)
            Result.Error(e)
        }
    }

    override suspend fun syncDeviceConfigs(): Result<Unit> {
        return try {
            val configsToSync = localDataSource.getDeviceConfigsToSync()
            for (configEntity in configsToSync) {
                firestore.collection(FB_DEVICE_CONFIGS_PATH).document(configEntity.deviceId)
                    .set(configEntity.toDomain().copy(needsSync = false)) // Send domain model without needsSync flag
                    .await()
                // Update local to mark as synced
                localDataSource.updateDeviceConfig(configEntity.copy(needsSync = false))
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing device configs: ${e.message}", e)
            Result.Error(e)
        }
    }

    // --- Advanced Analytics ---
    // Generic helper for fetching analytics data from Firestore
    private inline fun <reified T : Any> getAnalyticsDataFlow(
        farmId: String?,
        flockId: String?,
        collectionPath: String
    ): Flow<Result<List<T>>> = callbackFlow {
        trySend(Result.Loading)
        var query = firestore.collection(collectionPath)
        // Example filtering: This needs to be adjusted based on actual data structure
        if (flockId != null) {
            query = query.whereEqualTo("flockId", flockId)
        } else if (farmId != null) {
            query = query.whereEqualTo("farmId", farmId)
        }
        // Add .orderBy("dateField", Query.Direction.DESCENDING) if applicable

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.Error(Exception("Error fetching $collectionPath: ${error.message}", error)))
                return@addSnapshotListener
            }
            if (snapshot != null) {
                trySend(Result.Success(snapshot.toObjects()))
            } else {
                trySend(Result.Error(Exception("No data found for $collectionPath")))
            }
        }
        awaitClose { listener.remove() }
    }.catch { e -> emit(Result.Error(Exception("Flow error in getAnalyticsDataFlow for $collectionPath: ${e.message}", e))) }

    private suspend inline fun <reified T : Any> refreshAnalyticsData(
        farmId: String?,
        flockId: String?,
        collectionPath: String,
        // localInsert: suspend (List<T_Entity>) -> Unit // If caching locally
    ) {
        try {
            var query = firestore.collection(collectionPath)
            if (flockId != null) {
                query = query.whereEqualTo("flockId", flockId)
            } else if (farmId != null) {
                query = query.whereEqualTo("farmId", farmId)
            }
            val snapshot = query.get().await()
            // val data = snapshot.toObjects<T_Entity>() // If using entities for local cache
            // localInsert(data)
            Log.d(TAG, "Refreshed ${T::class.java.simpleName} for farm $farmId, flock $flockId")
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing $collectionPath: ${e.message}", e)
        }
    }

    override fun getProductionForecasts(farmId: String?, flockId: String?): Flow<Result<List<ProductionForecast>>> =
        getAnalyticsDataFlow(farmId, flockId, FB_PRODUCTION_FORECASTS_PATH)

    override suspend fun refreshProductionForecasts(farmId: String?, flockId: String?) =
        refreshAnalyticsData<ProductionForecast>(farmId, flockId, FB_PRODUCTION_FORECASTS_PATH)

    override fun getPerformancePredictions(farmId: String?, flockId: String?): Flow<Result<List<PerformancePrediction>>> =
        getAnalyticsDataFlow(farmId, flockId, FB_PERFORMANCE_PREDICTIONS_PATH)

    override suspend fun refreshPerformancePredictions(farmId: String?, flockId: String?) =
        refreshAnalyticsData<PerformancePrediction>(farmId, flockId, FB_PERFORMANCE_PREDICTIONS_PATH)

    override fun getFeedOptimizationRecommendations(farmId: String?, flockId: String?): Flow<Result<List<FeedOptimizationRecommendation>>> =
        getAnalyticsDataFlow(farmId, flockId, FB_FEED_RECOMMENDATIONS_PATH)

    override suspend fun refreshFeedOptimizationRecommendations(farmId: String?, flockId: String?) =
        refreshAnalyticsData<FeedOptimizationRecommendation>(farmId, flockId, FB_FEED_RECOMMENDATIONS_PATH)

    override fun getHealthTrends(farmId: String?, flockId: String?): Flow<Result<List<HealthTrend>>> =
        getAnalyticsDataFlow(farmId, flockId, FB_HEALTH_TRENDS_PATH)

    override suspend fun refreshHealthTrends(farmId: String?, flockId: String?) =
        refreshAnalyticsData<HealthTrend>(farmId, flockId, FB_HEALTH_TRENDS_PATH)

    // --- Smart Automation Settings ---

    override fun getFeedingSchedules(farmId: String?, flockId: String?): Flow<Result<List<FeedingSchedule>>> =
        getAnalyticsDataFlow(farmId, flockId, FB_FEEDING_SCHEDULES_PATH) // Reuse generic getter

    override suspend fun updateFeedingSchedule(schedule: FeedingSchedule): Result<Unit> {
        return try {
            // Assuming scheduleId is the document ID. If not, query to find the document.
            firestore.collection(FB_FEEDING_SCHEDULES_PATH).document(schedule.scheduleId)
                .set(schedule.copy(needsSync = false, lastUpdated = System.currentTimeMillis()), SetOptions.merge()) // Using merge to be safe
                .await()
            // No local cache for these settings in this example, direct Firestore update.
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating feeding schedule ${schedule.scheduleId}: ${e.message}", e)
            Result.Error(e)
        }
    }
    override suspend fun refreshFeedingSchedules(farmId: String?, flockId: String?) =
        refreshAnalyticsData<FeedingSchedule>(farmId, flockId, FB_FEEDING_SCHEDULES_PATH)


    override fun getClimateSettings(farmId: String?, shedId: String?): Flow<Result<List<ClimateSettings>>> =
        getAnalyticsDataFlow(farmId, shedId, FB_CLIMATE_SETTINGS_PATH) // farmId or shedId as primary key

    override suspend fun updateClimateSettings(settings: ClimateSettings): Result<Unit> {
        return try {
            firestore.collection(FB_CLIMATE_SETTINGS_PATH).document(settings.settingsId)
                .set(settings.copy(needsSync = false, lastUpdated = System.currentTimeMillis()), SetOptions.merge())
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating climate settings ${settings.settingsId}: ${e.message}", e)
            Result.Error(e)
        }
    }
     override suspend fun refreshClimateSettings(farmId: String?, shedId: String?) =
        refreshAnalyticsData<ClimateSettings>(farmId, shedId, FB_CLIMATE_SETTINGS_PATH)


    override fun getMaintenanceReminders(farmId: String?, deviceId: String?): Flow<Result<List<MaintenanceReminder>>> =
        getAnalyticsDataFlow(farmId, deviceId, FB_MAINTENANCE_REMINDERS_PATH)

    override suspend fun updateMaintenanceReminder(reminder: MaintenanceReminder): Result<Unit> {
        return try {
            firestore.collection(FB_MAINTENANCE_REMINDERS_PATH).document(reminder.reminderId)
                .set(reminder.copy(needsSync = false, lastUpdated = System.currentTimeMillis()), SetOptions.merge())
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating maintenance reminder ${reminder.reminderId}: ${e.message}", e)
            Result.Error(e)
        }
    }

    override suspend fun addMaintenanceReminder(reminder: MaintenanceReminder): Result<Unit> {
        return try {
            firestore.collection(FB_MAINTENANCE_REMINDERS_PATH).document(reminder.reminderId)
                .set(reminder.copy(needsSync = false, lastUpdated = System.currentTimeMillis())) // New item, no merge needed
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding maintenance reminder ${reminder.reminderId}: ${e.message}", e)
            Result.Error(e)
        }
    }
    override suspend fun refreshMaintenanceReminders(farmId: String?, deviceId: String?) =
        refreshAnalyticsData<MaintenanceReminder>(farmId, deviceId, FB_MAINTENANCE_REMINDERS_PATH)

}
