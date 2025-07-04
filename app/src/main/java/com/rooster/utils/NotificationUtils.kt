package com.rooster.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.rooster.R // Adjusted R package
import com.example.rooster.MainActivity // Adjusted MainActivity package

object NotificationUtils {

    // Aligned with FCMService.kt health_channel
    const val HEALTH_CHANNEL_ID = "health_channel"
    const val HEALTH_CHANNEL_NAME = "Health Reminders" // Name from FCMService
    const val HEALTH_CHANNEL_DESC = "Notifications for farm health and animal well-being"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Health Alerts Channel (aligns with FCMService's "health_channel")
            val healthChannel = NotificationChannel(
                HEALTH_CHANNEL_ID,
                HEALTH_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT // FCMService uses IMPORTANCE_DEFAULT for health_channel
            ).apply {
                description = HEALTH_CHANNEL_DESC
                // Configure other channel properties like lights, vibration, sound if needed
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(healthChannel)

            // Note: FCMService also creates channels dynamically.
            // Calling this at app startup ensures channels are created upfront.
            // Or, this can be removed if FCMService's dynamic creation is preferred.

            // TODO: Create other app-specific notification channels if NotificationUtils will manage them
        }
    }

    /**
     * Shows a local notification for a farm health alert.
     * This is useful for alerts generated within the app, not from FCM.
     * For FCM-originated health alerts, FCMService.handleHealthNotification will be used.
     */
    fun showFarmHealthAlertNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = System.currentTimeMillis().toInt(), // Unique ID for each notification
        alertId: String? = null // Optional: to pass to the target activity
    ) {
        // Intent to launch when notification is tapped
        // This should ideally navigate to a relevant screen in the app, e.g., a health alert details screen
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Example: Navigate to a specific part of MainActivity or a dedicated screen
            putExtra("navigate_to", "health_alert_details")
            alertId?.let { putExtra("alertId", it) }
            // TODO: Define how MainActivity or target activity handles these extras
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, notificationId /* request code needs to be unique for pending intent updates */,
            intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, HEALTH_CHANNEL_ID) // Use aligned channel ID
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Using launcher foreground from FCMService for consistency
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Dismiss notification when tapped
            // .setStyle(NotificationCompat.BigTextStyle().bigText(message)) // For longer messages
            // TODO: Add actions, custom sound, etc. if needed

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            try {
                notify(notificationId, builder.build())
            } catch (e: SecurityException) {
                // This can happen if POST_NOTIFICATIONS permission is not granted on Android 13+
                // TODO: Handle this case, e.g., by logging or informing the user to grant permission
                // For now, just print a log. In a real app, you'd request permission.
                println("SecurityException: Missing POST_NOTIFICATIONS permission? ${e.message}")
            }
        }
    }

    // TODO: Add functions for other types of notifications if needed
}
