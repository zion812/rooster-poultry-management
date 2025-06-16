package com.example.rooster

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PhotoUploadEntity::class, MessageEntity::class],
    version = 2,
    exportSchema = false, // Recommended for libraries, true for apps for schema history
)
abstract class PhotoUploadDatabase : RoomDatabase() {
    abstract fun photoUploadDao(): PhotoUploadDao

    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var instance: PhotoUploadDatabase? = null

        fun getInstance(context: Context): PhotoUploadDatabase {
            return instance ?: synchronized(this) {
                val newInstance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        PhotoUploadDatabase::class.java,
                        "rooster_photo_uploads.db", // More descriptive name
                    )
                        .fallbackToDestructiveMigration() // Handle migrations simply for now
                        .build()
                instance = newInstance
                newInstance
            }
        }
    }
}
