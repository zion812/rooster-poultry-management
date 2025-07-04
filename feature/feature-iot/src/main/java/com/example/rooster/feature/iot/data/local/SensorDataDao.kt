package com.example.rooster.feature.iot.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorDataDao {

    // DeviceInfo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceInfos(devices: List<DeviceInfoEntity>)

    @Query("SELECT * FROM iot_devices")
    fun getAllDeviceInfos(): Flow<List<DeviceInfoEntity>>

    @Query("SELECT * FROM iot_devices WHERE deviceId = :deviceId")
    fun getDeviceInfo(deviceId: String): Flow<DeviceInfoEntity?>

    // Temperature Readings
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemperatureReadings(readings: List<TemperatureReadingEntity>)

    @Query("SELECT * FROM temperature_readings WHERE deviceId = :deviceId ORDER BY timestamp DESC")
    fun getTemperatureReadingsForDevice(deviceId: String): Flow<List<TemperatureReadingEntity>>

    @Query("SELECT * FROM temperature_readings WHERE deviceId = :deviceId AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getTemperatureReadingsForDeviceInRange(deviceId: String, startTime: Long, endTime: Long): Flow<List<TemperatureReadingEntity>>

    @Query("DELETE FROM temperature_readings WHERE timestamp < :timestamp")
    suspend fun deleteOldTemperatureReadings(timestamp: Long)

    // Humidity Readings
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHumidityReadings(readings: List<HumidityReadingEntity>)

    @Query("SELECT * FROM humidity_readings WHERE deviceId = :deviceId ORDER BY timestamp DESC")
    fun getHumidityReadingsForDevice(deviceId: String): Flow<List<HumidityReadingEntity>>

    @Query("SELECT * FROM humidity_readings WHERE deviceId = :deviceId AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getHumidityReadingsForDeviceInRange(deviceId: String, startTime: Long, endTime: Long): Flow<List<HumidityReadingEntity>>

    @Query("DELETE FROM humidity_readings WHERE timestamp < :timestamp")
    suspend fun deleteOldHumidityReadings(timestamp: Long)

    // FeedLevel Readings
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedLevelReadings(readings: List<FeedLevelReadingEntity>)

    @Query("SELECT * FROM feed_level_readings WHERE deviceId = :deviceId ORDER BY timestamp DESC")
    fun getFeedLevelReadingsForDevice(deviceId: String): Flow<List<FeedLevelReadingEntity>>

    @Query("SELECT * FROM feed_level_readings WHERE deviceId = :deviceId AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getFeedLevelReadingsForDeviceInRange(deviceId: String, startTime: Long, endTime: Long): Flow<List<FeedLevelReadingEntity>>

    @Query("DELETE FROM feed_level_readings WHERE timestamp < :timestamp")
    suspend fun deleteOldFeedLevelReadings(timestamp: Long)

    // WaterConsumption Readings
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterConsumptionReadings(readings: List<WaterConsumptionReadingEntity>)

    @Query("SELECT * FROM water_consumption_readings WHERE deviceId = :deviceId ORDER BY timestamp DESC")
    fun getWaterConsumptionReadingsForDevice(deviceId: String): Flow<List<WaterConsumptionReadingEntity>>

    @Query("SELECT * FROM water_consumption_readings WHERE deviceId = :deviceId AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getWaterConsumptionReadingsForDeviceInRange(deviceId: String, startTime: Long, endTime: Long): Flow<List<WaterConsumptionReadingEntity>>

    @Query("DELETE FROM water_consumption_readings WHERE timestamp < :timestamp")
    suspend fun deleteOldWaterConsumptionReadings(timestamp: Long)

    // LightLevel Readings
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLightLevelReadings(readings: List<LightLevelReadingEntity>)

    @Query("SELECT * FROM light_level_readings WHERE deviceId = :deviceId ORDER BY timestamp DESC")
    fun getLightLevelReadingsForDevice(deviceId: String): Flow<List<LightLevelReadingEntity>>

    @Query("SELECT * FROM light_level_readings WHERE deviceId = :deviceId AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getLightLevelReadingsForDeviceInRange(deviceId: String, startTime: Long, endTime: Long): Flow<List<LightLevelReadingEntity>>

    @Query("DELETE FROM light_level_readings WHERE timestamp < :timestamp")
    suspend fun deleteOldLightLevelReadings(timestamp: Long)

    // Alerts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<AlertInfoEntity>)

    @Query("SELECT * FROM iot_alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<AlertInfoEntity>>

    @Query("SELECT * FROM iot_alerts WHERE acknowledged = 0 ORDER BY timestamp DESC")
    fun getUnacknowledgedAlerts(): Flow<List<AlertInfoEntity>>

    @Update
    suspend fun updateAlert(alert: AlertInfoEntity)

    @Query("DELETE FROM iot_alerts WHERE timestamp < :timestamp")
    suspend fun deleteOldAlerts(timestamp: Long)

    // Device Configs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceConfig(config: DeviceConfigEntity)

    @Query("SELECT * FROM iot_device_configs WHERE deviceId = :deviceId")
    fun getDeviceConfig(deviceId: String): Flow<DeviceConfigEntity?>

    @Query("SELECT * FROM iot_device_configs WHERE needsSync = 1")
    suspend fun getDeviceConfigsToSync(): List<DeviceConfigEntity>

    @Update
    suspend fun updateDeviceConfig(config: DeviceConfigEntity)
}
