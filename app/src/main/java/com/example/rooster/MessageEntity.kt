package com.example.rooster

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val messageId: String,
    val chatRoomId: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: String,
    val isSynced: Boolean = false,
)
