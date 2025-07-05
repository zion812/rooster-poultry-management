package com.rooster.buyerhome.ui.components

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rooster.buyerhome.domain.model.PriceComparisonProduct
import com.rooster.core.R

@Composable
fun PriceComparisonItemCard(item: PriceComparisonProduct) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.productName, style = MaterialTheme.typography.titleMedium) // Product name likely dynamic
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.price_comparison_avg_market_prefix) + " ${item.averageMarketPrice}", style = MaterialTheme.typography.bodyMedium) // TODO: Define R.string.price_comparison_avg_market_prefix
            item.yourLastPaidPrice?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.price_comparison_last_paid_prefix) + " $it", style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.price_comparison_last_paid_prefix
            }
            item.bestAvailablePrice?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.price_comparison_best_offer_prefix) + " $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) // TODO: Define R.string.price_comparison_best_offer_prefix
            }
            // TODO: Could add a small chart or trend indicator here if data supports it
        }
    }
}
