package com.example.rooster.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun CODConfirmationScreen(
    orderId: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(text = "Confirm Cash on Delivery?") },
        text = { Text(text = "You will pay when your farmer delivers the product.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = "Cancel")
            }
        },
    )
}
