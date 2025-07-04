package com.example.rooster.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rooster.core.database.util.CommonTypeConverters
import com.example.rooster.core.database.util.ListConverter

/**
 * Base abstract Room database class for the Rooster application.
 *
 * This class serves as a potential common superclass for feature-specific databases
 * or a holder for universally shared database configurations like TypeConverters.
 * Currently, it does not define any entities or DAOs itself, as these are expected
 * to be feature-specific.
 *
 * The database version is initialized to 1. Feature-specific databases that might
 * inherit from or use this should manage their own versioning independently if they
 * define their own schemas. Schema export is enabled by default for good practice,
 * allowing for schema history tracking.
 *
 * Registered TypeConverters:
 * - [CommonTypeConverters]: For common type conversions (e.g., List<String>).
 * - [OrderItemListConverter]: For converting lists of [com.example.rooster.core.common.model.OrderItem].
 */
@Database(
    entities = [], // No common entities are defined directly within this base database.
    version = 1,   // Initial version for this base definition.
    exportSchema = true // Recommended to keep schema history.
)
@androidx.room.TypeConverters(
    CommonTypeConverters::class,
    OrderItemListConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Companion object for [AppDatabase].
     *
     * Could hold constants like database name if this class were to represent a concrete,
     * singularly provided database instance (which is not the current design).
     */
    companion object {
        // Example: const val SHARED_DATABASE_NAME = "rooster_shared_db"
    }
}
