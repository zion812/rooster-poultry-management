package com.example.rooster.data.repositories

import com.example.rooster.data.entities.Chat
import com.example.rooster.data.entities.Message
import com.example.rooster.data.entities.MessageType
import com.example.rooster.domain.repository.ChatRepository
import com.example.rooster.models.ChatParse
import com.example.rooster.models.MessageParse
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ChatRepository
 */
@Singleton
class ChatRepositoryImpl
    @Inject
    constructor() : ChatRepository {
        override suspend fun createMessage(
            chatId: String,
            senderId: String,
            content: String,
            mediaUrl: String?,
        ): Message =
            withContext(Dispatchers.IO) {
                try {
                    val messageParse =
                        MessageParse().apply {
                            this.chatId = chatId
                            this.senderId = senderId
                            this.content = content
                            this.mediaUrl = mediaUrl
                            this.messageType = if (mediaUrl != null) MessageType.IMAGE.name else MessageType.TEXT.name
                            this.isRead = false
                            this.isDelivered = false
                        }
                    messageParse.saveInBackground().await()
                    messageParse.toMessage()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    throw e
                }
            }

        override suspend fun getMessages(chatId: String): List<Message> =
            withContext(Dispatchers.IO) {
                try {
                    val query = ParseQuery.getQuery(MessageParse::class.java)
                    query.whereEqualTo("chatId", chatId)
                    query.addAscendingOrder("createdAt")
                    val parseMessages = query.findInBackground().await()
                    parseMessages.map { (it as MessageParse).toMessage() }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    emptyList()
                }
            }

        override suspend fun getChats(userId: String): List<Chat> =
            withContext(Dispatchers.IO) {
                try {
                    val query = ParseQuery.getQuery(ChatParse::class.java)
                    query.whereContainsAll("participants", listOf(userId))
                    query.addDescendingOrder("updatedAt")
                    val parseChats = query.findInBackground().await()
                    parseChats.map { (it as ChatParse).toChat() }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    emptyList()
                }
            }

        override suspend fun getChat(chatId: String): Chat? =
            withContext(Dispatchers.IO) {
                try {
                    val query = ParseQuery.getQuery(ChatParse::class.java)
                    val parseChat = query.getInBackground(chatId).await()
                    (parseChat as ChatParse).toChat()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    null
                }
            }

        override suspend fun markMessagesAsRead(chatId: String) =
            withContext(Dispatchers.IO) {
                try {
                    val query = ParseQuery.getQuery(MessageParse::class.java)
                    query.whereEqualTo("chatId", chatId)
                    query.whereEqualTo("isRead", false)
                    val unreadMessages = query.findInBackground().await()
                    unreadMessages.forEach { message ->
                        (message as MessageParse).isRead = true
                        message.saveInBackground().await()
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }

        override suspend fun updateChatLastMessage(
            chatId: String,
            lastMessage: String,
            lastMessageTime: Long,
        ) = withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery(ChatParse::class.java)
                val chat = query.getInBackground(chatId).await() as ChatParse
                chat.lastMessage = lastMessage
                chat.lastMessageTime = java.util.Date(lastMessageTime)
                chat.saveInBackground().await()
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        override suspend fun updateChatUnreadCount(
            chatId: String,
            unreadCount: Int,
        ) = withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery(ChatParse::class.java)
                val chat = query.getInBackground(chatId).await() as ChatParse
                chat.unreadCount = unreadCount
                chat.saveInBackground().await()
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        override suspend fun deleteMessage(messageId: String) =
            withContext(Dispatchers.IO) {
                try {
                    val query = ParseQuery.getQuery(MessageParse::class.java)
                    val message = query.getInBackground(messageId).await()
                    message.deleteInBackground().await()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }

        override suspend fun createChat(
            userId1: String,
            userId2: String,
        ): Chat =
            withContext(Dispatchers.IO) {
                try {
                    val chatParse =
                        ChatParse().apply {
                            participants = listOf(userId1, userId2)
                            isGroup = false
                            unreadCount = 0
                        }
                    chatParse.saveInBackground().await()
                    chatParse.toChat()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    throw e
                }
            }

        override suspend fun searchChats(
            userId: String,
            query: String,
        ): List<Chat> =
            withContext(Dispatchers.IO) {
                try {
                    val parseQuery = ParseQuery.getQuery(ChatParse::class.java)
                    parseQuery.whereContainsAll("participants", listOf(userId))

                    val lastMessageQuery = ParseQuery.getQuery(ChatParse::class.java)
                    lastMessageQuery.whereContains("lastMessage", query)

                    val groupNameQuery = ParseQuery.getQuery(ChatParse::class.java)
                    groupNameQuery.whereContains("groupName", query)

                    val mainQuery = ParseQuery.or(listOf(lastMessageQuery, groupNameQuery))
                    mainQuery.whereContainsAll("participants", listOf(userId))

                    val parseChats = mainQuery.findInBackground().await()
                    parseChats.map { (it as ChatParse).toChat() }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    emptyList()
                }
            }
    }
