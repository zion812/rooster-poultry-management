package com.example.rooster.ui.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rooster.viewmodel.PaymentViewModel

@Composable
fun PaymentScreen(
    listingId: String,
    orderViewModel: PaymentViewModel = viewModel(),
    onSuccess: (orderId: String) -> Unit,
) {
    var useAdvance by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("COD") }
    val isProcessing by orderViewModel.isProcessing.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Choose Payment Method", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = paymentMethod == "COD",
                onClick = { paymentMethod = "COD" },
            )
            Text("Cash on Delivery")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = paymentMethod == "Online",
                onClick = { paymentMethod = "Online" },
            )
            Text("Online Payment")
        }
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = useAdvance,
                onCheckedChange = { useAdvance = it },
            )
            Text("Pay Advance")
        }
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                orderViewModel.placeOrder(
                    listingId,
                    paymentMethod,
                    useAdvance,
                ) { newOrderId ->
                    onSuccess(newOrderId)
                }
            },
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isProcessing) {
                CircularProgressIndicator(Modifier.size(20.dp))
            } else {
                Text("Place Order")
            }
        }
    }
}
