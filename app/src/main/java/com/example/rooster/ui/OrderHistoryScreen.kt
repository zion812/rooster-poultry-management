package com.example.rooster.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rooster.models.UserOrderStatus
import com.example.rooster.viewmodel.OrderViewModel

@Composable
fun OrderHistoryScreen(
    onOrderClick: (String) -> Unit,
    onChatClick: (String) -> Unit,
    onReorderClick: (String) -> Unit,
    vm: OrderViewModel = viewModel(),
) {
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        vm.loadOrders()
        isLoading = false
    }
    val orders by vm.ordersList.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "My Orders") }) },
    ) { padding ->
        if (isLoading) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                contentPadding = PaddingValues(16.dp),
            ) {
                items(orders) { order ->
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { onOrderClick(order.id) },
                    ) {
                        Text(order.productName, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        // Status progression tracker
                        val all = UserOrderStatus.values().toList()
                        val current = all.indexOf(order.status)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            all.forEachIndexed { idx, st ->
                                Text(
                                    text = st.name,
                                    style =
                                        if (idx == current) {
                                            MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                        } else {
                                            MaterialTheme.typography.bodySmall
                                        },
                                    color =
                                        if (idx <= current) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                )
                                if (idx < all.lastIndex) Text(" • ")
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        // Actions: Chat & Reorder
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            TextButton(onClick = { onChatClick(order.id) }) {
                                Text("Chat")
                            }
                            Spacer(Modifier.width(8.dp))
                            TextButton(onClick = { onReorderClick(order.id) }) {
                                Text("Reorder")
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
