package com.example.rooster.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.MessagingManager
import com.example.rooster.data.entities.Chat
import com.example.rooster.data.entities.Message
import com.example.rooster.domain.repository.ChatRepository
import com.example.rooster.domain.repository.UserRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel
    @Inject
    constructor(
        application: Application,
        private val chatRepository: ChatRepository,
        private val userRepository: UserRepository,
    ) : AndroidViewModel(application) {
        private val _isSending = MutableStateFlow(false)
        val isSending: StateFlow<Boolean> = _isSending

        private val _selectedChat = MutableStateFlow<Chat?>(null)
        val selectedChat: StateFlow<Chat?> = _selectedChat

        private val _messages = MutableStateFlow<List<Message>>(emptyList())
        val messages: StateFlow<List<Message>> = _messages

        private val _chats = MutableStateFlow<List<Chat>>(emptyList())
        val chats: StateFlow<List<Chat>> = _chats

        // Add properties for different message types
        private val _personalMessages =
            MutableStateFlow<List<MessagingManager.PersonalMessage>>(emptyList())
        val personalMessages: StateFlow<List<MessagingManager.PersonalMessage>> = _personalMessages

        private val _groupMessages = MutableStateFlow<List<MessagingManager.GroupMessage>>(emptyList())
        val groupMessages: StateFlow<List<MessagingManager.GroupMessage>> = _groupMessages

        private val _communityMessages =
            MutableStateFlow<List<MessagingManager.CommunityMessage>>(emptyList())
        val communityMessages: StateFlow<List<MessagingManager.CommunityMessage>> = _communityMessages

    private suspend fun getCurrentUserId(): String? {
        return userRepository.getCurrentUser()
    }

    private suspend fun getCurrentUserProfile(): UserProfile? {
        return try {
            val userId = getCurrentUserId()
            if (userId != null) {
                UserProfile(
                    userId = userId,
                    username = "User$userId",
                    profilePicture = null
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    data class UserProfile(
        val userId: String,
        val username: String,
        val profilePicture: String?
    )

    init {
            loadChats()
        }

        fun sendMessage(
            chatId: String,
            content: String,
            mediaUrl: String? = null,
        ) {
            viewModelScope.launch {
                _isSending.value = true
                try {
                    val currentUser = getCurrentUserProfile()
                    val message =
                        chatRepository.createMessage(
                            chatId = chatId,
                            senderId = currentUser?.userId ?: "",
                            content = content,
                            mediaUrl = mediaUrl,
                        )
                    updateChatLastMessage(chatId, message)
                    loadMessages(chatId)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                } finally {
                    _isSending.value = false
                }
            }
        }

        fun loadMessages(chatId: String) {
            viewModelScope.launch {
                try {
                    _messages.value = chatRepository.getMessages(chatId)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }

        fun loadChats() {
            viewModelScope.launch {
                try {
                    val currentUser = getCurrentUserProfile()
                    _chats.value = chatRepository.getChats(currentUser?.userId ?: "")
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }

        fun loadChat(chatId: String) {
            viewModelScope.launch {
                try {
                    val chat = chatRepository.getChat(chatId)
                    _selectedChat.value = chat
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }

        fun markMessagesAsRead(chatId: String) {
            viewModelScope.launch {
                try {
                    chatRepository.markMessagesAsRead(chatId)
                    updateChatUnreadCount(chatId, 0)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }

        private suspend fun updateChatLastMessage(
            chatId: String,
            message: Message,
        ) {
            chatRepository.updateChatLastMessage(
                chatId = chatId,
                lastMessage = message.content,
                lastMessageTime = message.createdAt,
            )
        }

        private suspend fun updateChatUnreadCount(
            chatId: String,
            unreadCount: Int,
        ) {
            chatRepository.updateChatUnreadCount(chatId, unreadCount)
        }

        fun deleteMessage(messageId: String) {
            viewModelScope.launch {
                try {
                    chatRepository.deleteMessage(messageId)
                    loadMessages(_selectedChat.value?.id ?: "")
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }

        fun createChat(
            userId1: String,
            userId2: String,
        ) {
            viewModelScope.launch {
                try {
                    chatRepository.createChat(userId1, userId2)
                    loadChats()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }

        fun searchChats(query: String) {
            viewModelScope.launch {
                try {
                    val currentUser = getCurrentUserProfile()
                    _chats.value = chatRepository.searchChats(currentUser?.userId ?: "", query)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }
    }
