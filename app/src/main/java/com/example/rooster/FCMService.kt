package com.example.rooster

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: send token to app server or store locally
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle different notification types
        val notificationType = remoteMessage.data["type"] ?: "general"
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Rooster"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""

        when (notificationType) {
            "dispute_update" -> handleDisputeNotification(title, body, remoteMessage.data)
            "auction_bid" -> handleAuctionNotification(title, body, remoteMessage.data)
            "transfer_request" -> handleTransferNotification(title, body, remoteMessage.data)
            "health_reminder" -> handleHealthNotification(title, body, remoteMessage.data)
            "market_update" -> handleMarketNotification(title, body, remoteMessage.data)
            else -> sendNotification(title, body)
        }
    }

    private fun handleDisputeNotification(
        title: String,
        body: String,
        data: Map<String, String>,
    ) {
        val disputeId = data["disputeId"]
        val intent =
            Intent(this, Class.forName("com.example.rooster.MainActivity")).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("navigate_to", "dispute_status")
                putExtra("disputeId", disputeId)
            }
        sendNotificationWithIntent(
            title,
            body,
            intent,
            "dispute_channel",
            NotificationCompat.PRIORITY_HIGH,
        )
    }

    private fun handleAuctionNotification(
        title: String,
        body: String,
        data: Map<String, String>,
    ) {
        val productId = data["productId"]
        val intent =
            Intent(this, Class.forName("com.example.rooster.MainActivity")).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("navigate_to", "auction")
                putExtra("productId", productId)
            }
        sendNotificationWithIntent(
            title,
            body,
            intent,
            "auction_channel",
            NotificationCompat.PRIORITY_DEFAULT,
        )
    }

    private fun handleTransferNotification(
        title: String,
        body: String,
        data: Map<String, String>,
    ) {
        val transferId = data["transferId"]
        val intent =
            Intent(this, Class.forName("com.example.rooster.MainActivity")).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("navigate_to", "transfers")
                putExtra("transferId", transferId)
            }
        sendNotificationWithIntent(
            title,
            body,
            intent,
            "transfer_channel",
            NotificationCompat.PRIORITY_HIGH,
        )
    }

    private fun handleHealthNotification(
        title: String,
        body: String,
        data: Map<String, String>,
    ) {
        val fowlId = data["fowlId"]
        val intent =
            Intent(this, Class.forName("com.example.rooster.MainActivity")).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("navigate_to", "health")
                putExtra("fowlId", fowlId)
            }
        sendNotificationWithIntent(
            title,
            body,
            intent,
            "health_channel",
            NotificationCompat.PRIORITY_DEFAULT,
        )
    }

    private fun handleMarketNotification(
        title: String,
        body: String,
        data: Map<String, String>,
    ) {
        val marketId = data["marketId"]
        val intent =
            Intent(this, Class.forName("com.example.rooster.MainActivity")).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("navigate_to", "marketplace")
                putExtra("marketId", marketId)
            }
        sendNotificationWithIntent(
            title,
            body,
            intent,
            "market_channel",
            NotificationCompat.PRIORITY_DEFAULT,
        )
    }

    private fun sendNotificationWithIntent(
        title: String,
        body: String,
        intent: Intent,
        channelId: String,
        priority: Int,
    ) {
        createNotificationChannel(channelId)

        // Check notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                // Permission not granted, skip notification
                return
            }
        }

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE,
            )

        val builder =
            NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(priority)

        NotificationManagerCompat.from(this).notify(
            System.currentTimeMillis().toInt(),
            builder.build(),
        )
    }

    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName =
                when (channelId) {
                    "dispute_channel" -> "Dispute Updates"
                    "auction_channel" -> "Auction Notifications"
                    "transfer_channel" -> "Transfer Requests"
                    "health_channel" -> "Health Reminders"
                    "market_channel" -> "Market Updates"
                    else -> "Rooster Notifications"
                }

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(
        title: String?,
        body: String?,
    ) {
        val channelId = "rooster_notifications"
        val notificationId = System.currentTimeMillis().toInt()
        // Create notification channel for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    channelId,
                    "Rooster Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        // Check notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                // Permission not granted, skip notification
                return
            }
        }
        // Create intent to open MainActivity
        val intent =
            Intent(this, Class.forName("com.example.rooster.MainActivity")).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE,
            )
        // Build and display notification
        val builder =
            NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        NotificationManagerCompat.from(this).notify(notificationId, builder.build())
    }
}
