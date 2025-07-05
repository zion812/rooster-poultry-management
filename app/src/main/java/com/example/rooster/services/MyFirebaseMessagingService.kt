package com.yourapp.services

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)
        try {
            val title = msg.notification?.title
            val body = msg.notification?.body
            Log.d("FCM", "Received push: $title / $body")
        } catch (t: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(t)
        }
    }
}
