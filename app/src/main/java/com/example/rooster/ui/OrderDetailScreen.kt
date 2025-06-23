package com.example.rooster.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rooster.models.UserOrderStatus
import com.example.rooster.viewmodel.OrderViewModel

@Composable
fun OrderDetailScreen(
    orderId: String,
    onBack: () -> Unit,
    onCODConfirm: () -> Unit,
    onFeedback: () -> Unit,
    vm: OrderViewModel = viewModel(),
) {
    LaunchedEffect(orderId) {
        vm.selectOrder(orderId)
    }
    val order by vm.selectedOrder.collectAsState()

    if (order == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order #${order!!.id}") },
                navigationIcon = {
                    IconButton(onClick = onBack) { 
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back") 
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Product: ${order!!.productName}", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Status: ${order!!.status.name}")
            Spacer(Modifier.height(16.dp))

            when (order!!.status) {
                UserOrderStatus.PENDING -> Button(onClick = onCODConfirm) { Text("Confirm Cash on Delivery") }
                UserOrderStatus.DELIVERED -> Button(onClick = onFeedback) { Text("Leave Feedback") }
                else -> {}
            }
        }
    }
}
