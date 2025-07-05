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
import com.rooster.vethome.domain.model.VetAlertSeverity
import com.rooster.vethome.domain.model.VetHealthAlert
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun VetHealthAlertCard(alert: VetHealthAlert) {
    val dateFormat = SimpleDateFormat("dd MMM yy HH:mm", Locale.getDefault())
    val severityColor = when (alert.severity) {
        VetAlertSeverity.CRITICAL -> MaterialTheme.colorScheme.error
        VetAlertSeverity.URGENT -> Color(0xFFFFA000) // Amber
        VetAlertSeverity.WARNING -> MaterialTheme.colorScheme.tertiary
        VetAlertSeverity.INFO -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alert.isAcknowledged) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(alert.title, style = MaterialTheme.typography.titleMedium, color = severityColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(stringResource(R.string.vet_alert_farm_prefix) + " ${alert.farmName}", style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.vet_alert_farm_prefix
            Text(alert.description, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(stringResource(R.string.vet_alert_severity_prefix) + " ", style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.vet_alert_severity_prefix
                Text(
                    alert.severity.name, // Enum name
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = severityColor
                )
            }
            Text(
                stringResource(R.string.vet_alert_reported_prefix) + " ${dateFormat.format(alert.timestamp)}", // TODO: Define R.string.vet_alert_reported_prefix
                style = MaterialTheme.typography.bodySmall
            )
            alert.suggestedActionsForVet?.let { actions ->
                if (actions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(stringResource(R.string.vet_alert_suggested_actions_title), style = MaterialTheme.typography.labelMedium) // TODO: Define R.string.vet_alert_suggested_actions_title
                    actions.forEach { action ->
                        Text("- $action", style = MaterialTheme.typography.bodySmall) // Dynamic action text
                    }
                }
            }
            if (!alert.isAcknowledged) {
                // TODO: Add "Acknowledge" button or similar action
            }
        }
    }
}
