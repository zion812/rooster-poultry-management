package com.example.rooster.data.entities

/**
 * Chat data entity
 */
data class Chat(
    val id: String,
    val participants: List<String>,
    val lastMessage: String? = null,
    val lastMessageTime: Long = 0,
    val unreadCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isGroup: Boolean = false,
    val groupName: String? = null,
    val groupImageUrl: String? = null
)