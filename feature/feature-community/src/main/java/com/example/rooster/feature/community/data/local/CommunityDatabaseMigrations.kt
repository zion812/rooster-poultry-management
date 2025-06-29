package com.example.rooster.feature.community.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object CommunityDatabaseMigrations {

    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add new columns to community_user_profiles table
            db.execSQL("ALTER TABLE community_user_profiles ADD COLUMN syncAttempts INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE community_user_profiles ADD COLUMN lastSyncAttemptTimestamp INTEGER NOT NULL DEFAULT 0")

            // Add new columns to community_posts table
            db.execSQL("ALTER TABLE community_posts ADD COLUMN syncAttempts INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE community_posts ADD COLUMN lastSyncAttemptTimestamp INTEGER NOT NULL DEFAULT 0")

            // Add new columns to community_comments table
            db.execSQL("ALTER TABLE community_comments ADD COLUMN syncAttempts INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE community_comments ADD COLUMN lastSyncAttemptTimestamp INTEGER NOT NULL DEFAULT 0")
        }
    }
    // Future migrations can be added here
}
