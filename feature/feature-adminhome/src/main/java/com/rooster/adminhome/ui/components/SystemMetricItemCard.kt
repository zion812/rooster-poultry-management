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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rooster.adminhome.domain.model.SystemMetric
import com.rooster.adminhome.domain.model.SystemStatus
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SystemMetricItemCard(metric: SystemMetric) {
    val statusColor = when (metric.status) {
        SystemStatus.OPERATIONAL -> MaterialTheme.colorScheme.primary
        SystemStatus.DEGRADED -> Color(0xFFFFA000) // Amber
        SystemStatus.OFFLINE -> MaterialTheme.colorScheme.error
        SystemStatus.MAINTENANCE -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

import androidx.compose.ui.res.stringResource
import com.rooster.core.R as CoreR

@Composable
fun SystemMetricItemCard(metric: SystemMetric) {
    val statusColor = when (metric.status) {
        SystemStatus.OPERATIONAL -> MaterialTheme.colorScheme.primary
        SystemStatus.DEGRADED -> Color(0xFFFFA000) // Amber
        SystemStatus.OFFLINE -> MaterialTheme.colorScheme.error
        SystemStatus.MAINTENANCE -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(metric.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(CoreR.string.admin_metric_value_prefix) + " ${metric.value}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    metric.status.name, // Assuming direct display of enum name is acceptable for admin
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                stringResource(CoreR.string.admin_metric_last_updated_prefix) + " ${dateFormat.format(metric.lastUpdated)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
