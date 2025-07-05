package com.example.rooster.feature.farm.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rooster.feature.farm.data.local.db.dao.FarmDao
import com.example.rooster.feature.farm.data.local.db.entity.FarmEntity

@Database(
    entities = [FarmEntity::class],
    version = 1, // Initial version
    exportSchema = true // Recommended to export schema for migrations
)
abstract class FarmDatabase : RoomDatabase() {
    abstract fun farmDao(): FarmDao

    // Companion object for Singleton pattern or Hilt provision
    companion object {
        const val DATABASE_NAME = "rooster_farm_feature_db"
    }
}
