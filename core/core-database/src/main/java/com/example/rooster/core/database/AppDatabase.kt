package com.example.rooster.core.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rooster.core.database.dao.*
import com.example.rooster.core.database.entity.*
import com.example.rooster.core.database.util.CommonTypeConverters
import com.example.rooster.core.database.util.OrderItemListConverter

/**
 * Main Room database for the Rooster Poultry Management System.
 *
 * This database serves as the offline-first storage solution with support for:
 * - Complete poultry management data (flocks, birds, health records)
 * - Production tracking and analytics
 * - IoT sensor data storage
 * - Traceability records for supply chain management
 * - Comprehensive sync status tracking for online/offline operations
 *
 * The database is designed to work seamlessly with Firebase and Back4App backends,
 * providing robust offline capabilities for rural areas with poor connectivity.
 */
@Database(
    entities = [
        FlockEntity::class,
        BirdEntity::class,
        HealthRecordEntity::class,
        ProductionRecordEntity::class,
        SensorDataEntity::class,
        TraceabilityRecordEntity::class
    ],
    version = 1,
    exportSchema = true
)
@androidx.room.TypeConverters(
    CommonTypeConverters::class,
    OrderItemListConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    // DAO abstract methods
    abstract fun flockDao(): FlockDao
    abstract fun birdDao(): BirdDao
    abstract fun healthRecordDao(): HealthRecordDao
    abstract fun productionRecordDao(): ProductionRecordDao
    abstract fun sensorDataDao(): SensorDataDao
    abstract fun traceabilityDao(): TraceabilityDao

    companion object {
        const val DATABASE_NAME = "rooster_poultry_database"
        const val DATABASE_VERSION = 1

        /**
         * Migration from version 1 to 2 (placeholder for future migrations)
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Example migration - add new columns, tables, or modify existing ones
                // database.execSQL("ALTER TABLE flocks ADD COLUMN new_column TEXT")
            }
        }

        /**
         * Creates and configures the Room database instance.
         *
         * @param context Application context
         * @param databaseName Optional custom database name
         * @return Configured AppDatabase instance
         */
        fun create(
            context: android.content.Context,
            databaseName: String = DATABASE_NAME
        ): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                databaseName
            )
                .addMigrations(MIGRATION_1_2)
                .addCallback(DatabaseCallback())
                .build()
        }

        /**
         * Creates an in-memory database for testing purposes
         */
        fun createInMemory(context: android.content.Context): AppDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                AppDatabase::class.java
            )
                .addCallback(DatabaseCallback())
                .build()
        }
    }

    /**
     * Database callback for handling database creation and opening events
     */
    private class DatabaseCallback : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Database created for the first time
            // Optional: Pre-populate with default data
            setupInitialData(db)
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            // Database opened
            // Optional: Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON")

            // Optional: Setup database optimization
            setupDatabaseOptimization(db)
        }

        private fun setupInitialData(db: SupportSQLiteDatabase) {
            // Optional: Insert default/seed data
            // Example: Default sensor types, standard breeds, etc.
        }

        private fun setupDatabaseOptimization(db: SupportSQLiteDatabase) {
            // Create indexes for frequently queried columns
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_flocks_farm_id ON flocks(farmId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_flocks_sync_status ON flocks(syncStatus)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_birds_flock_id ON birds(flockId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_birds_sync_status ON birds(syncStatus)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_health_records_bird_id ON health_records(birdId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_health_records_date ON health_records(date)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_production_records_bird_id ON production_records(birdId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_production_records_date ON production_records(date)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_sensor_data_sensor_id ON sensor_data(sensorId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_sensor_data_timestamp ON sensor_data(timestamp)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_traceability_bird_id ON traceability_records(birdId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_traceability_timestamp ON traceability_records(timestamp)")
        }
    }
}

/**
 * Database utilities for common operations
 */
object DatabaseUtils {

    /**
     * Performs database cleanup operations
     * - Removes old sensor data beyond retention period
     * - Cleans up orphaned records
     * - Optimizes database performance
     */
    suspend fun performDatabaseCleanup(database: AppDatabase) {
        val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)

        // Clean up old sensor data (keep only last 30 days)
        database.sensorDataDao().deleteOldSensorData(thirtyDaysAgo)

        // Additional cleanup operations can be added here
        // Example: Remove soft-deleted records, compress old data, etc.
    }

    /**
     * Gets sync statistics for monitoring offline/online operations
     */
    suspend fun getSyncStatistics(database: AppDatabase): SyncStatistics {
        val flockDao = database.flockDao()
        val birdDao = database.birdDao()
        val healthRecordDao = database.healthRecordDao()
        val productionRecordDao = database.productionRecordDao()

        return SyncStatistics(
            pendingFlocks = flockDao.getFlocksBySyncStatus(SyncStatus.PENDING).size,
            pendingBirds = birdDao.getBirdsBySyncStatus(SyncStatus.PENDING).size,
            pendingHealthRecords = healthRecordDao.getHealthRecordsBySyncStatus(SyncStatus.PENDING).size,
            pendingProductionRecords = productionRecordDao.getProductionRecordsBySyncStatus(
                SyncStatus.PENDING
            ).size,
            failedSyncs = listOf(
                flockDao.getFlocksBySyncStatus(SyncStatus.FAILED).size,
                birdDao.getBirdsBySyncStatus(SyncStatus.FAILED).size,
                healthRecordDao.getHealthRecordsBySyncStatus(SyncStatus.FAILED).size,
                productionRecordDao.getProductionRecordsBySyncStatus(SyncStatus.FAILED).size
            ).sum()
        )
    }
}

/**
 * Data class for sync statistics monitoring
 */
data class SyncStatistics(
    val pendingFlocks: Int,
    val pendingBirds: Int,
    val pendingHealthRecords: Int,
    val pendingProductionRecords: Int,
    val failedSyncs: Int
) {
    val totalPendingItems: Int
        get() = pendingFlocks + pendingBirds + pendingHealthRecords + pendingProductionRecords

    val hasFailedSyncs: Boolean
        get() = failedSyncs > 0

    val hasPendingSync: Boolean
        get() = totalPendingItems > 0
}
