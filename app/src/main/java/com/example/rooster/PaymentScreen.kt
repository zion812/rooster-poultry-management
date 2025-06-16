@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.rooster

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rooster.data.OrderRepository
import com.example.rooster.models.UserOrderStatus
import com.example.rooster.payment.PaymentActivity
import com.example.rooster.payment.PaymentOutcome
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    val context = LocalContext.current
    val activity =
        remember(context) {
            context as? Activity ?: throw IllegalStateException("Context is not an Activity")
        }

    // Get current user details (may be null)
    val currentUser = remember { ParseUser.getCurrentUser() }
    val userEmail = currentUser?.email ?: ""
    val userPhone = currentUser?.getString("phone") ?: ""

    val paymentLauncher =
        rememberLauncherForActivityResult(StartActivityForResult()) { result ->
            isProcessing = false
            if (result.resultCode == Activity.RESULT_OK) {
                val paymentId = result.data?.getStringExtra("paymentId") ?: "N/A"
                paymentStatus = PaymentOutcome.Success(paymentId)
                FirebaseCrashlytics.getInstance()
                    .log("Razorpay Payment Success: ID $paymentId for order ${listing.id}")
            } else {
                paymentStatus = PaymentOutcome.Error(0, "Payment Cancelled or Failed")
                FirebaseCrashlytics.getInstance()
                    .log("Razorpay Payment Failed/Cancelled for order ${listing.id}")
            }
        }

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

            // Payment Form
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    if (selectedMethod == "Razorpay") {
                        Text(
                            text = if (isTeluguMode) "కార్డ్ వివరాలు" else "Card Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text(if (isTeluguMode) "కార్డ్ నంబర్" else "Card Number") },
                            placeholder = { Text("1234 5678 9012 3456") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                label = { Text(if (isTeluguMode) "గడువు తేదీ" else "Expiry Date") },
                                placeholder = { Text("MM/YY") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                label = { Text("CVV") },
                                placeholder = { Text("123") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                            )
                        }
                    } else {
                        Text(
                            text = if (isTeluguMode) "డెలివరీ చిరునామా" else "Delivery Address",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = codAddress,
                            onValueChange = { codAddress = it },
                            label = { Text(if (isTeluguMode) "పూర్తి చిరునామా" else "Complete Address") },
                            placeholder = {
                                Text(if (isTeluguMode) "మీ పూర్తి చిరునామాను నమోదు చేయండి" else "Enter your complete address")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5,
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = codMobile,
                            onValueChange = { codMobile = it },
                            label = { Text(if (isTeluguMode) "మొబైల్ నంబర్" else "Mobile Number") },
                            placeholder = { Text("9xxxxxxxxx") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                ),
                        ) {
                            Text(
                                text =
                                    if (isTeluguMode) {
                                        "గమనిక: డెలివరీ సమయంలో మీరు ₹${listing.price} చెల్లించాలి. దయచేసి ఖచ్చితమైన మొత్తం సిద్ధంగా ఉంచండి."
                                    } else {
                                        "Note: You will pay ₹${listing.price} upon delivery. Please have the exact amount ready."
                                    },
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                    }
                }
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(if (isTeluguMode) "మార్కెట్‌ప్లేస్‌కు వెనక్కి" else "Back to Marketplace")
                        }
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

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text =
                                    if (isTeluguMode) {
                                        "చెల్లింపులో సమస్య ఉంది. దయచేసి మళ్లీ ప్రయత్నించండి."
                                    } else {
                                        "There was an issue with the payment. Please try again."
                                    },
                                style = MaterialTheme.typography.bodyMedium,
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
                                text = if (isTeluguMode) "చెల్లింపు రద్దు చేయబడింది ❌" else "Payment Cancelled ❌",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text =
                                    if (isTeluguMode) {
                                        "చెల్లింపు రద్దు చేయబడింది. దయచేసి మళ్లీ ప్రయత్నించండి."
                                    } else {
                                        "Payment was cancelled. Please try again."
                                    },
                                style = MaterialTheme.typography.bodyMedium,
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
                        val canProceed =
                            if (selectedMethod == "Razorpay") {
                                true
                            } else {
                                codAddress.isNotBlank() && codMobile.isNotBlank() && codMobile.length == 10
                            }

                        Button(
                            onClick = {
                                isProcessing = true
                                if (selectedMethod == "Razorpay") {
                                    val intent =
                                        Intent(activity, PaymentActivity::class.java).apply {
                                            putExtra(PaymentActivity.EXTRA_ORDER_ID, listing.id)
                                            putExtra(
                                                PaymentActivity.EXTRA_AMOUNT_PAISE,
                                                (listing.price * 100).toInt(),
                                            )
                                            putExtra(
                                                PaymentActivity.EXTRA_EMAIL,
                                                if (userEmail.isNotBlank()) userEmail else "test@example.com",
                                            )
                                            putExtra(
                                                PaymentActivity.EXTRA_PHONE,
                                                if (userPhone.isNotBlank()) userPhone else codMobile.ifBlank { "9876543210" },
                                            )
                                            // putExtra(PaymentActivity.EXTRA_TEST_MODE_SUCCESS, true) // For testing without real payment
                                        }
                                    paymentLauncher.launch(intent)
                                } else {
                                    // Simulate COD processing
                                    CoroutineScope(Dispatchers.Main).launch {
                                        try {
                                            val user = ParseUser.getCurrentUser()
                                            val newOrder =
                                                OrderRepository.createOrder(
                                                    productName = listing.id,
                                                    method = "COD",
                                                    advance = false,
                                                    status = UserOrderStatus.PENDING,
                                                    buyerId = user?.objectId ?: "guest",
                                                )
                                            // Log address/phone as extra (mock impl)
                                            FirebaseCrashlytics.getInstance().log(
                                                "COD Order ${newOrder.id} address=$codAddress phone=$codMobile",
                                            )
                                            paymentStatus = PaymentOutcome.Success("COD_CONFIRMED")
                                        } catch (e: Exception) {
                                            FirebaseCrashlytics.getInstance().recordException(e)
                                            paymentStatus =
                                                PaymentOutcome.Error(0, e.message ?: "Error")
                                        } finally {
                                            isProcessing = false
                                        }
                                    }
                                }
                            },
                            enabled = canProceed,
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
