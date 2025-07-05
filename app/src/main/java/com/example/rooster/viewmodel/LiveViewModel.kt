package com.example.rooster.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

/**
 * ViewModel for managing live broadcast sessions and notifications.
 */
class LiveViewModel : ViewModel() {
    /** Start a live session (video or audio). */
    override fun onCleared() {
        super.onCleared()
        // Clear state flows to prevent memory leaks
    }

    fun startBroadcast(
        sessionId: String,
        isVideo: Boolean,
    ) {
        // TODO: integrate with live SDK (Agora/Jitsi)
    }

    /** Stop the current live session. */
    fun stopBroadcast() {
        // TODO: stop live SDK session
    }

    /**
     * Send a "Live Now" notification via FCM or Parse.
     * In production, use a server API or admin SDK to push to subscribed users.
     */
    fun notifyLiveNow(
        context: Context,
        message: String,
    ) {
        viewModelScope.launch {
            try {
                // Example: subscribe users to topic and send message (stub)
                FirebaseMessaging.getInstance().subscribeToTopic("live_updates")
                // TODO: trigger server push to topic "live_updates" with 'message'
            } catch (e: Exception) {
                // TODO: handle errors
            }
        }
    }

    /** Invite a specific user or role to the live session. */
    fun inviteUser(userIdOrRole: String) {
        viewModelScope.launch {
            // TODO: send invite via Parse or FCM to userIdOrRole
        }
    }
}
