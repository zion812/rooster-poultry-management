package com.example.rooster

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Notification Permission Handler for Android 13+
 * Implements the permission request flow outlined in the FCM messaging guide
 * Optimized for rural farmers with clear explanations
 */
object NotificationPermissionHandler {
    /**
     * Check if notification permission is granted
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permissions automatically granted on older versions
        }
    }

    /**
     * Check if notification permission rationale should be shown
     */
    fun shouldShowRationale(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            false
        }
    }
}

/**
 * Composable for handling notification permission requests
 * Shows educational dialog and handles permission flow
 */
@Composable
fun NotificationPermissionRequest(onPermissionResult: (Boolean) -> Unit = {}) {
    val context = LocalContext.current
    var showRationaleDialog by remember { mutableStateOf(false) }
    var permissionRequested by remember { mutableStateOf(false) }

    // Permission launcher
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            onPermissionResult(isGranted)
            if (!isGranted && !permissionRequested) {
                // Show educational message if permission was denied
                showRationaleDialog = true
            }
            permissionRequested = true
        }

    // Check if we need to request permission
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                NotificationPermissionHandler.hasNotificationPermission(context) -> {
                    // Already granted
                    onPermissionResult(true)
                }

                NotificationPermissionHandler.shouldShowRationale(context as Activity) -> {
                    // Show educational UI
                    showRationaleDialog = true
                }

                else -> {
                    // Request permission directly
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Automatically granted on older versions
            onPermissionResult(true)
        }
    }

    // Educational rationale dialog
    if (showRationaleDialog) {
        NotificationRationaleDialog(
            onAllow = {
                showRationaleDialog = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            onDeny = {
                showRationaleDialog = false
                onPermissionResult(false)
            },
        )
    }
}

/**
 * Educational dialog explaining notification benefits for rural farmers
 */
@Composable
private fun NotificationRationaleDialog(
    onAllow: () -> Unit,
    onDeny: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDeny,
        title = {
            Text("Enable Notifications")
        },
        text = {
            Text(
                "Enable notifications to receive:\n\n" +
                    "• Messages from buyers and sellers\n" +
                    "• Health reminders for your fowl\n" +
                    "• Market price updates\n" +
                    "• Cultural event announcements\n" +
                    "• Transfer verification alerts\n\n" +
                    "Stay connected with the farming community!",
            )
        },
        confirmButton = {
            Button(
                onClick = onAllow,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
            ) {
                Text("Allow Notifications", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDeny) {
                Text("No Thanks", color = Color.Gray)
            }
        },
    )
}

/**
 * Simplified notification permission checker for quick checks
 */
@Composable
fun rememberNotificationPermission(): Boolean {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        hasPermission = NotificationPermissionHandler.hasNotificationPermission(context)
    }

    return hasPermission
}

/**
 * Quick notification settings reminder for users who disabled notifications
 */
@Composable
fun NotificationDisabledReminder(onDismiss: () -> Unit = {}) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Notifications Disabled")
        },
        text = {
            Text(
                "You have disabled notifications for Rooster.\n\n" +
                    "You may miss important messages and updates from the farming community.\n\n" +
                    "To enable notifications, go to Settings > Apps > Rooster > Notifications.",
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
            ) {
                Text("OK", color = Color.White)
            }
        },
    )
}
