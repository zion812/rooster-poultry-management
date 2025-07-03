package com.example.rooster

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RoomDatabase

@Entity(tableName = "photo_uploads")
data class PhotoUploadEntity(
    @PrimaryKey val id: String,
    val filePath: String,
    val uploadStatus: String,
    val timestamp: Long
)

@Dao
interface PhotoUploadDao {
    @Query("SELECT * FROM photo_uploads")
    suspend fun getAllUploads(): List<PhotoUploadEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpload(upload: PhotoUploadEntity)

    @Query("DELETE FROM photo_uploads WHERE id = :id")
    suspend fun deleteUpload(id: String)
}

@Database(
    entities = [PhotoUploadEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PhotoUploadDatabase : RoomDatabase() {
    abstract fun photoUploadDao(): PhotoUploadDao
}