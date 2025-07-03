package com.example.rooster.core.common.model

import java.math.BigDecimal
import java.time.LocalDateTime

// Data models that correspond to Parse objects
data class Chat(
    val id: String,
    val participants: List<String>,
    val lastMessage: String,
    val lastMessageTime: Long,
    val isGroup: Boolean,
    val unreadCount: Int
)

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val content: String,
    val mediaUrl: String?,
    val messageType: String,
    val timestamp: Long,
    val isRead: Boolean,
    val isDelivered: Boolean
)

data class CommunityGroup(
    val id: String,
    val name: String,
    val description: String,
    val memberCount: Int,
    val category: String,
    val region: String,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

data class SafeMarketplaceListing(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val sellerId: String,
    val isAvailable: Boolean
)

data class TraceabilityEntry(
    val id: String = "",
    val birdId: String = "",
    val eventType: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val location: String = "",
    val performedBy: String = "",
    val additionalData: Map<String, String> = emptyMap()
)
