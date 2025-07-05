package com.rooster.adminhome.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rooster.adminhome.domain.model.FinancialAnalyticHighlight

@Composable
fun FinancialHighlightItemCard(highlight: FinancialAnalyticHighlight) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(highlight.title, style = MaterialTheme.typography.titleMedium)
            Text("(${highlight.period})", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(highlight.value, style = MaterialTheme.typography.headlineSmall)
                highlight.trendPercentage?.let { trend ->
                    Spacer(modifier = Modifier.padding(start = 8.dp))
import androidx.compose.ui.res.stringResource
import com.rooster.core.R as CoreR

@Composable
fun FinancialHighlightItemCard(highlight: FinancialAnalyticHighlight) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(highlight.title, style = MaterialTheme.typography.titleMedium)
            Text("(${highlight.period})", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(highlight.value, style = MaterialTheme.typography.headlineSmall)
                highlight.trendPercentage?.let { trend ->
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    val trendColor = if (trend >= 0) Color(0xFF4CAF50) /* Green */ else MaterialTheme.colorScheme.error
                    val trendIcon = if (trend >= 0) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward
                    val trendDesc = if (trend >= 0) stringResource(CoreR.string.admin_financial_trend_up_desc) else stringResource(CoreR.string.admin_financial_trend_down_desc)
                    Icon(
                        imageVector = trendIcon,
                        contentDescription = trendDesc,
                        tint = trendColor,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        String.format("%.1f%%", trend),
                        style = MaterialTheme.typography.bodyMedium,
                        color = trendColor
                    )
                }
            }
        }
    }
}
