package com.example.rooster.feature.marketplace.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object MarketplaceDatabaseMigrations {

    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add new columns to product_listings table
            db.execSQL("ALTER TABLE product_listings ADD COLUMN syncAttempts INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE product_listings ADD COLUMN lastSyncAttemptTimestamp INTEGER NOT NULL DEFAULT 0")

            // Add new columns to orders table
            db.execSQL("ALTER TABLE orders ADD COLUMN syncAttempts INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE orders ADD COLUMN lastSyncAttemptTimestamp INTEGER NOT NULL DEFAULT 0")
        }
    }

    // Future migrations can be added here
}
