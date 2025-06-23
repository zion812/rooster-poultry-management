package com.example.rooster.data.repositories

import com.example.rooster.data.entities.Chat
import com.example.rooster.data.entities.Message
import com.example.rooster.data.entities.MessageType
import com.example.rooster.domain.repository.ChatRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ChatRepository
 */
@Singleton
class ChatRepositoryImpl @Inject constructor() : ChatRepository {

    // Mock data for development
    private val messages = mutableListOf<Message>()
    private val chats = mutableListOf<Chat>()

    override suspend fun createMessage(
        chatId: String,
        senderId: String,
        content: String,
        mediaUrl: String?
    ): Message {
        val message = Message(
            id = "msg_${System.currentTimeMillis()}",
            chatId = chatId,
            senderId = senderId,
            content = content,
            mediaUrl = mediaUrl,
            messageType = if (mediaUrl != null) MessageType.IMAGE else MessageType.TEXT
        )
        messages.add(message)
        return message
    }

    override suspend fun getMessages(chatId: String): List<Message> {
        return messages.filter { it.chatId == chatId }
    }

    override suspend fun getChats(userId: String): List<Chat> {
        return chats.filter { it.participants.contains(userId) }
    }

    override suspend fun getChat(chatId: String): Chat? {
        return chats.find { it.id == chatId }
    }

    override suspend fun markMessagesAsRead(chatId: String) {
        // Mock implementation
    }

    override suspend fun updateChatLastMessage(
        chatId: String,
        lastMessage: String,
        lastMessageTime: Long
    ) {
        val chatIndex = chats.indexOfFirst { it.id == chatId }
        if (chatIndex != -1) {
            chats[chatIndex] = chats[chatIndex].copy(
                lastMessage = lastMessage,
                lastMessageTime = lastMessageTime
            )
        }
    }

    override suspend fun updateChatUnreadCount(chatId: String, unreadCount: Int) {
        val chatIndex = chats.indexOfFirst { it.id == chatId }
        if (chatIndex != -1) {
            chats[chatIndex] = chats[chatIndex].copy(unreadCount = unreadCount)
        }
    }

    override suspend fun deleteMessage(messageId: String) {
        messages.removeAll { it.id == messageId }
    }

    override suspend fun createChat(userId1: String, userId2: String): Chat {
        val chat = Chat(
            id = "chat_${System.currentTimeMillis()}",
            participants = listOf(userId1, userId2)
        )
        chats.add(chat)
        return chat
    }

    override suspend fun searchChats(userId: String, query: String): List<Chat> {
        return getChats(userId).filter {
            it.lastMessage?.contains(query, ignoreCase = true) == true ||
                    it.groupName?.contains(query, ignoreCase = true) == true
        }
    }
}