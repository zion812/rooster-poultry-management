package com.example.rooster

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhotoUploadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PhotoUploadEntity)

    @Query("SELECT * FROM photo_uploads WHERE id = :id")
    suspend fun getById(id: String): PhotoUploadEntity?

    @Query("UPDATE photo_uploads SET statusName = :status, errorMessage = :errorMessage WHERE id = :id")
    suspend fun updateStatus(
        id: String,
        status: String,
        errorMessage: String? = null,
    )

    @Query("UPDATE photo_uploads SET progress = :progress WHERE id = :id")
    suspend fun updateProgress(
        id: String,
        progress: Int,
    )

    @Query("UPDATE photo_uploads SET parseFileUrl = :url WHERE id = :id")
    suspend fun updateWithParseFileUrl(
        id: String,
        url: String,
    )

    @Query("UPDATE photo_uploads SET retryCount = retryCount + 1 WHERE id = :id")
    suspend fun incrementRetryCount(id: String)

    @Delete
    suspend fun delete(entity: PhotoUploadEntity)

    @Query("SELECT * FROM photo_uploads WHERE statusName IN ('PENDING', 'RETRYING')")
    suspend fun getPendingRequests(): List<PhotoUploadEntity>

    @Query("SELECT * FROM photo_uploads WHERE statusName = 'PENDING' ORDER BY createdAt ASC LIMIT :limit")
    suspend fun getFreshPendingRequests(limit: Int): List<PhotoUploadEntity>

    @Query("SELECT COUNT(*) FROM photo_uploads WHERE statusName = 'UPLOADING'")
    suspend fun getActiveUploadCount(): Int

    @Query("DELETE FROM photo_uploads WHERE statusName IN ('COMPLETED', 'FAILED', 'CANCELLED')")
    suspend fun clearCompleted()
}
