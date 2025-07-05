package com.example.rooster.feature.iot.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        DeviceInfoEntity::class,
        TemperatureReadingEntity::class,
        HumidityReadingEntity::class,
        FeedLevelReadingEntity::class,
        WaterConsumptionReadingEntity::class,
        LightLevelReadingEntity::class,
        AlertInfoEntity::class,
        DeviceConfigEntity::class
    ],
    version = 1,
    exportSchema = false // Set to true in production and provide schemas
)
@TypeConverters(MapConverter::class)
abstract class IoTDatabase : RoomDatabase() {
    abstract fun sensorDataDao(): SensorDataDao
}
