package com.example.rooster.feature.community.data.remote

import com.example.rooster.core.common.Result
import com.example.rooster.feature.community.domain.model.ChatMessage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface ChatRemoteDataSource { // Specific interface for chat
    suspend fun sendMessage(chatRoomId: String, message: ChatMessage): Result<Unit>
    fun getMessagesStream(chatRoomId: String, limit: Int = 50): Flow<Result<List<ChatMessage>>>
    // May need methods to create/get chat rooms, manage read receipts etc.
}

@Singleton
class FirebaseRtdbSocialDataSource @Inject constructor(
    private val rtdb: DatabaseReference // Inject DatabaseReference from FirebaseDatabase.getInstance().reference
) : ChatRemoteDataSource {

    companion object {
        private const val PATH_CHATS = "chats"
    }

    override suspend fun sendMessage(chatRoomId: String, message: ChatMessage): Result<Unit> {
        return try {
            val messageId = rtdb.child(PATH_CHATS).child(chatRoomId).push().key
                        ?: throw Exception("Failed to generate message ID")

            val messageToSend = message.copy(messageId = messageId) // Ensure message has the ID

            rtdb.child(PATH_CHATS).child(chatRoomId).child(messageId).setValue(messageToSend).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error sending chat message to room $chatRoomId")
            Result.Error(e)
        }
    }

    override fun getMessagesStream(chatRoomId: String, limit: Int): Flow<Result<List<ChatMessage>>> = callbackFlow {
        val messagesRef = rtdb.child(PATH_CHATS).child(chatRoomId)
            .orderByChild("timestamp") // Assuming messages are ordered by timestamp
            .limitToLast(limit)       // Get the last N messages

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<ChatMessage>()
                for (childSnapshot in snapshot.children) {
                    try {
                        // Firebase RTDB KTX getValue<ChatMessage>() should work if ChatMessage is a data class
                        // with default constructor values for all properties, or if all properties are present in DB.
                        // Or map manually if there are issues.
                        val msg = childSnapshot.getValue(ChatMessage::class.java)
                        msg?.let { messages.add(it.copy(messageId = childSnapshot.key ?: it.messageId)) } // Ensure ID is set from snapshot key
                    } catch (e: Exception) {
                        Timber.e(e, "Error deserializing chat message: ${childSnapshot.key}")
                        // Optionally trySend an error for this specific message or skip it
                    }
                }
                // RTDB limitToLast sorts ascending, then takes last N. If you want them descending in list:
                // messages.reverse()
                trySend(Result.Success(messages))
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error listening to chat messages for room $chatRoomId")
                trySend(Result.Error(error.toException())).isFailure
                channel.close(error.toException())
            }
        }
        messagesRef.addValueEventListener(listener)
        awaitClose { messagesRef.removeEventListener(listener) }
    }
}
