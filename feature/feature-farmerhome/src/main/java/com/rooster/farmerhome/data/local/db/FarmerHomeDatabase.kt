package com.rooster.farmerhome.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rooster.farmerhome.data.local.dao.FarmBasicInfoDao
import com.rooster.farmerhome.data.local.dao.FarmHealthAlertDao
import com.rooster.farmerhome.data.local.dao.ProductionSummaryDao
import com.rooster.farmerhome.data.local.dao.WeatherDao
import com.rooster.farmerhome.data.local.model.FarmBasicInfoEntity
import com.rooster.farmerhome.data.local.model.FarmHealthAlertEntity
import com.rooster.farmerhome.data.local.model.ProductionMetricItemEntity
import com.rooster.farmerhome.data.local.model.ProductionSummaryEntity
import com.rooster.farmerhome.data.local.model.WeatherDataEntity
import com.rooster.farmerhome.data.local.utils.FarmerHomeTypeConverters


@Database(
    entities = [
        WeatherDataEntity::class,
        FarmBasicInfoEntity::class,
        ProductionSummaryEntity::class,
        ProductionMetricItemEntity::class,
        FarmHealthAlertEntity::class
    ],
    version = 1, // Initial version
    exportSchema = true // Recommended to export schema for migrations
)
@TypeConverters(FarmerHomeTypeConverters::class) // For MetricTrend and AlertSeverity enums
abstract class FarmerHomeDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun farmBasicInfoDao(): FarmBasicInfoDao
    abstract fun productionSummaryDao(): ProductionSummaryDao
    abstract fun farmHealthAlertDao(): FarmHealthAlertDao

    companion object {
        const val DATABASE_NAME = "farmer_home_db"
    }
}
