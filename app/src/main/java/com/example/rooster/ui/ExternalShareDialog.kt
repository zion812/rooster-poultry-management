package com.example.rooster.ui

import android.content.Intent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun ExternalShareDialog(
    shareText: String,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    val sendIntent: Intent =
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
    val shareIntent = Intent.createChooser(sendIntent, null)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share via") },
        text = { Text("Share this item with your friends!") },
        confirmButton = {
            TextButton(onClick = {
                context.startActivity(shareIntent)
                onDismiss()
            }) { Text("Share") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
