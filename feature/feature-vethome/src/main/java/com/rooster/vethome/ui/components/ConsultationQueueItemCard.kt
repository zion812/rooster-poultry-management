package com.rooster.vethome.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rooster.core.R
import com.rooster.vethome.domain.model.ConsultationQueueItem
import com.rooster.vethome.domain.model.ConsultationRequestStatus
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ConsultationQueueItemCard(item: ConsultationQueueItem) {
    val dateFormat = SimpleDateFormat("dd MMM yy HH:mm", Locale.getDefault())
    val statusColor = when (item.status) {
        ConsultationRequestStatus.PENDING -> MaterialTheme.colorScheme.secondary
        ConsultationRequestStatus.ACCEPTED -> MaterialTheme.colorScheme.primary
        ConsultationRequestStatus.IN_PROGRESS -> MaterialTheme.colorScheme.tertiary
        ConsultationRequestStatus.COMPLETED -> Color.Gray
        ConsultationRequestStatus.CANCELLED -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "${item.farmerName} - ${item.farmLocation} (${item.flockType})", // Dynamic data
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(stringResource(R.string.consultation_issue_prefix) + " ${item.issueSummary}", style = MaterialTheme.typography.bodyMedium, maxLines = 2) // TODO: Define R.string.consultation_issue_prefix
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(stringResource(R.string.consultation_status_prefix) + " ", style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.consultation_status_prefix
                Text(
                    item.status.name, // Enum name, consider localizing if user-facing and not just internal state
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(stringResource(R.string.consultation_priority_prefix) + " ${item.priority}", style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.consultation_priority_prefix
            }
            Text(
                stringResource(R.string.consultation_requested_prefix) + " ${dateFormat.format(item.requestTime)}", // TODO: Define R.string.consultation_requested_prefix
                style = MaterialTheme.typography.bodySmall
            )
            // TODO: Add actions like "Accept", "View Details"
        }
    }
}
