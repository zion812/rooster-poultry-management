@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.rooster

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// PaymentOutcome sealed class for MVP
sealed class PaymentOutcome {
    data class Success(val paymentId: String) : PaymentOutcome()
    data class Error(val code: Int, val message: String) : PaymentOutcome()
    object Cancelled : PaymentOutcome()
}

data class DummyListing(
    val id: String,
    val title: String,
    val price: Double,
    val location: String,
    val photos: List<String> = emptyList(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    listing: DummyListing,
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
) {
    var selectedMethod by remember { mutableStateOf("Razorpay") }
    var isProcessing by remember { mutableStateOf(false) }
    var paymentStatus by remember { mutableStateOf<PaymentOutcome?>(null) }
    var codAddress by remember { mutableStateOf("") }
    var codMobile by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isTeluguMode) "చెల్లింపు" else "Payment",
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onLanguageToggle) {
                        Text(
                            text = if (isTeluguMode) "EN" else "తె",
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Payment Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = if (isTeluguMode) "చెల్లింపు వివరాలు" else "Payment Details",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isTeluguMode) "వస్తువు: ${listing.title}" else "Item: ${listing.title}",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = if (isTeluguMode) "ధర: ₹${listing.price}" else "Price: ₹${listing.price}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = if (isTeluguMode) "స్థానం: ${listing.location}" else "Location: ${listing.location}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Payment Method Selection
            Text(
                text = if (isTeluguMode) "చెల్లింపు పద్ధతిని ఎంచుకోండి" else "Select Payment Method",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FilterChip(
                    onClick = { selectedMethod = "Razorpay" },
                    label = { Text(if (isTeluguMode) "రేజర్‌పే" else "Razorpay") },
                    selected = selectedMethod == "Razorpay",
                    modifier = Modifier.weight(1f),
                )
                FilterChip(
                    onClick = { selectedMethod = "COD" },
                    label = { Text(if (isTeluguMode) "డెలివరీ వద్ద చెల్లింపు" else "Cash on Delivery") },
                    selected = selectedMethod == "COD",
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Payment Status or Button
            when (paymentStatus) {
                is PaymentOutcome.Success -> {
                    Card(
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = if (isTeluguMode) "చెల్లింపు విజయవంతమైంది! ✅" else "Payment Successful! ✅",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text =
                                    if (isTeluguMode) {
                                        "మీ ఆర్డర్ విజయవంతంగా ప్లేస్ చేయబడింది. త్వరలో మీకు కాంటాక్ట్ చేస్తాము."
                                    } else {
                                        "Your order has been placed successfully. You will be contacted soon."
                                    },
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(if (isTeluguMode) "మార్కెట్‌ప్లేస్‌కు వెనక్కి" else "Back to Marketplace")
                    }
                }

                is PaymentOutcome.Error -> {
                    Card(
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                            ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = if (isTeluguMode) "చెల్లింపు విఫలమైంది ❌" else "Payment Failed ❌",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = {
                                paymentStatus = null
                                selectedMethod = "Razorpay"
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(if (isTeluguMode) "మళ్లీ ప్రయత్నించండి" else "Try Again")
                        }
                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(if (isTeluguMode) "మార్కెట్‌ప్లేస్‌కు వెనక్కి" else "Back to Marketplace")
                        }
                    }
                }

                is PaymentOutcome.Cancelled -> {
                    Card(
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                            ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = if (isTeluguMode) "చెల్లింపు రద్దు చేయబడింది" else "Payment Cancelled",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            paymentStatus = null
                            selectedMethod = "Razorpay"
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(if (isTeluguMode) "మళ్లీ ప్రయత్నించండి" else "Try Again")
                    }
                }

                null -> {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isTeluguMode) "చెల్లింపు ప్రక్రియలో ఉంది..." else "Processing Payment...",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    } else {
                        Button(
                            onClick = {
                                isProcessing = true
                                // Simulate payment processing - MVP mock
                                paymentStatus = PaymentOutcome.Success("MVP_${listing.id}")
                                isProcessing = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = if (isTeluguMode) "₹${listing.price} చెల్లించండి" else "Pay ₹${listing.price}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
