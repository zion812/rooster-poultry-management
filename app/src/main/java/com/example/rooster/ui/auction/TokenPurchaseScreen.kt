package com.example.rooster.ui.auction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rooster.services.TokenService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenPurchaseScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    productCost: Double,
) {
    var balance by remember { mutableStateOf(0) }
    var purchaseCount by remember { mutableStateOf("") }
    var tokenPercent by remember { mutableStateOf("5") } // percent of product cost per token
    var isProcessing by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    // Load current balance
    LaunchedEffect(Unit) {
        TokenService.loadTokenBalance { balance = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isTeluguMode) "టోకెన్ కొనుగోలు" else "Purchase Tokens") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = (if (isTeluguMode) "ప్రస్తుత టోకెన్ బ్యాలెన్స్:" else "Current Token Balance:") + " $balance",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text =
                    if (isTeluguMode) {
                        "ప్రోడక్ట్ ఖర్చు: ₹${"%.2f".format(productCost)}"
                    } else {
                        "Product Cost: ₹${
                            "%.2f".format(
                                productCost,
                            )
                        }"
                    },
                style = MaterialTheme.typography.bodyMedium,
            )
            // Token percent input
            OutlinedTextField(
                value = tokenPercent,
                onValueChange = { tokenPercent = it.filter { ch -> ch.isDigit() } },
                label = { Text(if (isTeluguMode) "టోకెన్ శాతం (5-25)%" else "Token Percent (5-25)%") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Calculate unit and total price
            val percentVal = tokenPercent.toDoubleOrNull()?.coerceIn(5.0, 25.0) ?: 5.0
            val unitPrice = productCost * (percentVal / 100.0)
            val count = purchaseCount.toIntOrNull() ?: 0
            val totalPrice = unitPrice * count
            Text(
                text =
                    if (isTeluguMode) {
                        "ప్రతి టోకెన్ ధర: ₹${"%.2f".format(unitPrice)}"
                    } else {
                        "Per Token Price: ₹${
                            "%.2f".format(
                                unitPrice,
                            )
                        }"
                    },
                style = MaterialTheme.typography.bodyMedium,
            )
            if (count > 0) {
                Text(
                    text =
                        if (isTeluguMode) {
                            "మొత్తం చెల్లింపు: ₹${"%.2f".format(totalPrice)}"
                        } else {
                            "Total Payment: ₹${
                                "%.2f".format(
                                    totalPrice,
                                )
                            }"
                        },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            OutlinedTextField(
                value = purchaseCount,
                onValueChange = { purchaseCount = it.filter { ch -> ch.isDigit() } },
                label = { Text(if (isTeluguMode) "కొనుగోలు సంఖ్య" else "Tokens to Purchase") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = {
                    val count = purchaseCount.toIntOrNull() ?: 0
                    if (count > 0) {
                        isProcessing = true
                        TokenService.addTokens(count) { success ->
                            isProcessing = false
                            if (success) {
                                message =
                                    if (isTeluguMode) "టోకెన్లు విజయవంతంగా చేర్చబడ్డాయి" else "Tokens added successfully"
                                // Reload balance
                                TokenService.loadTokenBalance { balance = it }
                            } else {
                                message =
                                    if (isTeluguMode) "కామర్స్‌లో పొరపాటు" else "Purchase failed"
                            }
                        }
                    }
                },
                enabled = !isProcessing && purchaseCount.toIntOrNull() ?: 0 > 0,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isProcessing) CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isTeluguMode) "కొనుగోలు చేయండి" else "Purchase")
            }
            message?.let {
                Text(text = it, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
