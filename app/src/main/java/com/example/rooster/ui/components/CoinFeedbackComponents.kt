package com.example.rooster.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Component to show coin deduction status and user feedback
 */
@Composable
fun CoinDeductionStatusCard(
    isVisible: Boolean,
    coinAmount: Int?,
    actionLabel: String?,
    isPending: Boolean = false,
    isSuccess: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    onDismiss: () -> Unit = {},
) {
    if (!isVisible) return

    val (backgroundColor, contentColor, icon, statusText) =
        when {
            isError ->
                Quad(
                    MaterialTheme.colorScheme.errorContainer,
                    MaterialTheme.colorScheme.onErrorContainer,
                    Icons.Default.Error,
                    "Coin deduction failed",
                )

            isPending ->
                Quad(
                    MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.colorScheme.onSecondaryContainer,
                    Icons.Default.Pending,
                    "Coin deduction pending",
                )

            isSuccess ->
                Quad(
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    Icons.Default.CheckCircle,
                    "Coins deducted successfully",
                )

            else ->
                Quad(
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    Icons.Default.Info,
                    "Processing",
                )
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = backgroundColor,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = statusText,
                tint = contentColor,
                modifier = Modifier.size(24.dp),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.titleSmall,
                    color = contentColor,
                    fontWeight = FontWeight.Medium,
                )

                if (coinAmount != null && actionLabel != null) {
                    Text(
                        text = "$coinAmount coin(s) for ${actionLabel.replace("_", " ")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                    )
                }

                if (isError && !errorMessage.isNullOrBlank()) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor,
                        fontSize = 12.sp,
                    )
                }

                if (isPending) {
                    Text(
                        text = "Your coins will be deducted when the network is available",
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                    )
                }
            }

            if (isSuccess || isError) {
                TextButton(
                    onClick = onDismiss,
                    colors =
                        ButtonDefaults.textButtonColors(
                            contentColor = contentColor,
                        ),
                ) {
                    Text("Dismiss")
                }
            }
        }
    }
}

/**
 * Snackbar host state extension for coin deduction messages
 */
@Composable
fun CoinDeductionSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier,
    ) { snackbarData ->
        val message = snackbarData.visuals.message
        val isError =
            message.contains("failed", ignoreCase = true) ||
                message.contains("error", ignoreCase = true)
        val isPending =
            message.contains("shortly", ignoreCase = true) ||
                message.contains("pending", ignoreCase = true)

        val backgroundColor =
            when {
                isError -> MaterialTheme.colorScheme.errorContainer
                isPending -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.primaryContainer
            }

        val contentColor =
            when {
                isError -> MaterialTheme.colorScheme.onErrorContainer
                isPending -> MaterialTheme.colorScheme.onSecondaryContainer
                else -> MaterialTheme.colorScheme.onPrimaryContainer
            }

        Snackbar(
            modifier = Modifier.padding(12.dp),
            shape = RoundedCornerShape(8.dp),
            containerColor = backgroundColor,
            contentColor = contentColor,
            action =
                snackbarData.visuals.actionLabel?.let { actionLabel ->
                    {
                        TextButton(
                            onClick = { snackbarData.performAction() },
                            colors =
                                ButtonDefaults.textButtonColors(
                                    contentColor = contentColor,
                                ),
                        ) {
                            Text(actionLabel)
                        }
                    }
                },
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

/**
 * Coin balance display component
 */
@Composable
fun CoinBalanceDisplay(
    coinBalance: Int,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "🪙",
                fontSize = 18.sp,
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = coinBalance.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
            )

            if (showLabel) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "coins",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                )
            }
        }
    }
}

/**
 * Helper data class for quad values
 */
private data class Quad<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
)
