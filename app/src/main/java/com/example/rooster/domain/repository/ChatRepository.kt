package com.example.rooster.domain.repository

import com.example.rooster.data.entities.Chat
import com.example.rooster.data.entities.Message

/**
 * Domain interface for chat operations
 */
interface ChatRepository {
    suspend fun createMessage(
        chatId: String,
        senderId: String,
        content: String,
        mediaUrl: String? = null,
    ): Message

    suspend fun getMessages(chatId: String): List<Message>

    suspend fun getChats(userId: String): List<Chat>

    suspend fun getChat(chatId: String): Chat?

    suspend fun markMessagesAsRead(chatId: String)

    suspend fun updateChatLastMessage(
        chatId: String,
        lastMessage: String,
        lastMessageTime: Long,
    )

    suspend fun updateChatUnreadCount(
        chatId: String,
        unreadCount: Int,
    )

    suspend fun deleteMessage(messageId: String)

    suspend fun createChat(
        userId1: String,
        userId2: String,
    ): Chat

    suspend fun searchChats(
        userId: String,
        query: String,
    ): List<Chat>
}
