package com.example.rooster.data.entities

/**
 * Message data entity
 */
data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val content: String,
    val mediaUrl: String? = null,
    val messageType: MessageType = MessageType.TEXT,
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isDelivered: Boolean = false,
)

/**
 * Message type enumeration
 */
enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    DOCUMENT,
}
