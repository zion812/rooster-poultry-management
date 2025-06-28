package com.example.rooster.feature.farm.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object FarmDatabaseMigrations {

    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add 'needsSync' column to existing tables if it was added in version 2
            // Assuming default value for existing rows should be 'false' (0) or 'true' (1)
            // If they were previously synced or if they should all be synced now.
            // Defaulting to true (1) meaning existing records should be checked by sync worker.
            db.execSQL("ALTER TABLE flocks ADD COLUMN needsSync INTEGER NOT NULL DEFAULT 1")
            db.execSQL("ALTER TABLE mortality_records ADD COLUMN needsSync INTEGER NOT NULL DEFAULT 1")
            db.execSQL("ALTER TABLE vaccination_records ADD COLUMN needsSync INTEGER NOT NULL DEFAULT 1")
            db.execSQL("ALTER TABLE sensor_data ADD COLUMN needsSync INTEGER NOT NULL DEFAULT 1")
            db.execSQL("ALTER TABLE update_records ADD COLUMN needsSync INTEGER NOT NULL DEFAULT 1")
        }
    }

    val MIGRATION_2_3: Migration = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create the new lineage_links table
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `lineage_links` (
                    `childFlockId` TEXT NOT NULL,
                    `parentFlockId` TEXT NOT NULL,
                    `relationshipType` TEXT NOT NULL,
                    `needsSync` INTEGER NOT NULL DEFAULT 1,
                    PRIMARY KEY(`childFlockId`, `parentFlockId`, `relationshipType`),
                    FOREIGN KEY(`childFlockId`) REFERENCES `flocks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE ,
                    FOREIGN KEY(`parentFlockId`) REFERENCES `flocks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
            """.trimIndent())
            // Create indices for lineage_links table
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_lineage_links_childFlockId` ON `lineage_links` (`childFlockId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_lineage_links_parentFlockId` ON `lineage_links` (`parentFlockId`)")
        }
    }

    // Add more migrations here as needed, e.g., MIGRATION_3_4, etc.
}
