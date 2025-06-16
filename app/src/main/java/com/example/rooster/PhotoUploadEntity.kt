package com.example.rooster

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_uploads")
data class PhotoUploadEntity(
    @PrimaryKey
    val id: String,
    val uriString: String,
    val fileName: String,
    val targetObjectId: String?,
    val targetClassName: String?,
    val targetField: String?,
    val statusName: String,
    val progress: Int,
    val retryCount: Int,
    val errorMessage: String?,
    val parseFileUrl: String?,
    val createdAt: Long,
    val updatedAt: Long,
)
