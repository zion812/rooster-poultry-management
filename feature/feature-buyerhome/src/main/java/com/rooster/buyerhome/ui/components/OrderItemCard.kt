package com.rooster.buyerhome.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rooster.buyerhome.domain.model.OrderItem
import com.rooster.buyerhome.domain.model.OrderStatus
import com.rooster.core.R
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OrderItemCard(order: OrderItem) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "${order.productName} (${order.quantity})", // Product name and quantity might not need separate string resources if always combined
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(stringResource(R.string.order_seller_prefix) + " ${order.sellerName}", style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.order_seller_prefix
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(stringResource(R.string.order_status_prefix) + " ", style = MaterialTheme.typography.bodyMedium) // TODO: Define R.string.order_status_prefix
                Text(
                    order.status.name, // Enum name usually doesn't need localization unless you want custom display names
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (order.status) {
                        OrderStatus.DELIVERED -> MaterialTheme.colorScheme.primary
                        OrderStatus.CANCELLED -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            Text(stringResource(R.string.order_total_prefix) + " ${order.totalPrice}", style = MaterialTheme.typography.bodyMedium) // TODO: Define R.string.order_total_prefix
            Spacer(modifier = Modifier.height(4.dp))
            Text(stringResource(R.string.order_ordered_on_prefix) + " ${dateFormat.format(order.orderDate)}", style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.order_ordered_on_prefix
            order.expectedDeliveryDate?.let {
                Text(stringResource(R.string.order_expected_by_prefix) + " ${dateFormat.format(it)}", style = MaterialTheme.typography.bodySmall) // TODO: Define R.string.order_expected_by_prefix
            }
        }
    }
}
