package com.example.rooster

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatRoomId = :chatRoomId ORDER BY timestamp ASC")
    fun getMessagesForChatRoom(chatRoomId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE chatRoomId = :chatRoomId ORDER BY timestamp ASC")
    suspend fun getMessagesForChatRoomSync(chatRoomId: String): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE isSynced = 0")
    suspend fun getUnsyncedMessages(): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Query("UPDATE messages SET isSynced = 1 WHERE messageId = :messageId")
    suspend fun markMessageAsSynced(messageId: String)

    @Query("DELETE FROM messages WHERE chatRoomId = :chatRoomId")
    suspend fun deleteMessagesForChatRoom(chatRoomId: String)

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}
