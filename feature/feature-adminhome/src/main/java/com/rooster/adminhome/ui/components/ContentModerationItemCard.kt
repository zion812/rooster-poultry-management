package com.rooster.adminhome.ui.components

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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.rooster.adminhome.domain.model.ContentModerationItem
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ContentModerationItemCard(item: ContentModerationItem) {
    val dateFormat = SimpleDateFormat("dd MMM yy HH:mm", Locale.getDefault())
import androidx.compose.ui.res.stringResource
import com.rooster.core.R as CoreR

@Composable
fun ContentModerationItemCard(item: ContentModerationItem) {
    val dateFormat = SimpleDateFormat("dd MMM yy HH:mm", Locale.getDefault())
    val itemTypeString = item.contentType.name.lowercase(Locale.getDefault()).replaceFirstChar { it.titlecase(Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                stringResource(CoreR.string.admin_moderation_reported_item_title, itemTypeString),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "\"${item.contentSnippet}...\"",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                maxLines = 2
            )
            item.reasonForFlag?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(CoreR.string.admin_moderation_reason_prefix) + " $it", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(stringResource(CoreR.string.admin_moderation_status_prefix) + " ${item.status.name}", style = MaterialTheme.typography.bodySmall) // Assuming direct status name display
                Spacer(modifier = Modifier.weight(1f))
                Text(stringResource(CoreR.string.admin_moderation_date_prefix) + " ${dateFormat.format(item.submissionDate)}", style = MaterialTheme.typography.bodySmall)
            }
            // TODO: Add actions e.g., "View Details", "Approve", "Reject"
        }
    }
}
