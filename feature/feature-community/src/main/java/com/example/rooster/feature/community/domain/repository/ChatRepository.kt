package com.example.rooster.feature.community.domain.repository

import com.example.rooster.core.common.Result
import com.example.rooster.feature.community.data.remote.ChatRemoteDataSource // Using the specific interface
import com.example.rooster.feature.community.domain.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface ChatRepository {
    suspend fun sendMessage(chatRoomId: String, message: ChatMessage): Result<Unit>
    fun getMessagesStream(chatRoomId: String, limit: Int = 50): Flow<Result<List<ChatMessage>>>
}

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val remoteDataSource: ChatRemoteDataSource
) : ChatRepository {

    override suspend fun sendMessage(chatRoomId: String, message: ChatMessage): Result<Unit> =
        withContext(Dispatchers.IO) {
            remoteDataSource.sendMessage(chatRoomId, message)
        }

    override fun getMessagesStream(chatRoomId: String, limit: Int): Flow<Result<List<ChatMessage>>> {
        return remoteDataSource.getMessagesStream(chatRoomId, limit)
            .flowOn(Dispatchers.IO) // Ensure upstream flow runs on IO dispatcher
    }
}
