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
    // productCost: Double, // Will be replaced by token packages
) {
    // Placeholder for token packages - In a real app, this would come from a ViewModel
    data class TokenPackage(val id: String, val name: String, val tokenAmount: Int, val price: Double, val currency: String = "INR")
    val availablePackages by remember {
        mutableStateOf(
            listOf(
                TokenPackage(id = "pkg1", name = if (isTeluguMode) "5 టోకెన్లు" else "5 Tokens", tokenAmount = 5, price = 50.0),
                TokenPackage(id = "pkg2", name = if (isTeluguMode) "10 టోకెన్లు" else "10 Tokens", tokenAmount = 10, price = 90.0),
                TokenPackage(id = "pkg3", name = if (isTeluguMode) "25 టోకెన్లు" else "25 Tokens", tokenAmount = 25, price = 200.0)
            )
        )
    }
    var selectedPackage by remember { mutableStateOf<TokenPackage?>(null) }

    var balance by remember { mutableStateOf(0) }
    // var purchaseCount by remember { mutableStateOf("") } // No longer needed with packages
    // var tokenPercent by remember { mutableStateOf("5") } // No longer needed
    var isProcessing by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }


    // Load current balance
    LaunchedEffect(Unit) {
        TokenService.loadTokenBalance { balance = it }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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

            Text(
                text = if (isTeluguMode) "టోకెన్ ప్యాకేజీని ఎంచుకోండి:" else "Select a Token Package:",
                style = MaterialTheme.typography.titleSmall
            )

            availablePackages.forEach { pkg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPackage = pkg },
                    shape = MaterialTheme.shapes.medium,
                    border = if (selectedPackage == pkg) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(pkg.name, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "${pkg.tokenAmount} ${if (isTeluguMode) "టోకెన్లు" else "Tokens"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Text("₹${"%.2f".format(pkg.price)}", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            selectedPackage?.let {
                Text(
                    text = (if (isTeluguMode) "ఎంచుకున్న ప్యాకేజీ: " else "Selected: ") + "${it.name} (${it.tokenAmount} ${if (isTeluguMode) "టోకెన్లు" else "Tokens"} for ₹${"%.2f".format(it.price)})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = {
                    selectedPackage?.let { pkg ->
                        isProcessing = true
                        // TODO: Here you would initiate actual payment flow for pkg.price
                        // For now, we use the old TokenService.addTokens as a placeholder for successful purchase of pkg.tokenAmount
                        TokenService.addTokens(pkg.tokenAmount) { success ->
                            isProcessing = false
                            if (success) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (isTeluguMode) "${pkg.tokenAmount} టోకెన్లు విజయవంతంగా చేర్చబడ్డాయి" else "${pkg.tokenAmount} Tokens added successfully",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                                TokenService.loadTokenBalance { newBalance -> balance = newBalance }
                                selectedPackage = null // Reset selection
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (isTeluguMode) "కొనుగోలు విఫలమైంది" else "Purchase failed",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }
                    }
                },
                enabled = !isProcessing && selectedPackage != null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isProcessing) CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isTeluguMode) "కొనుగోలు చేయండి" else "Purchase Selected Package")
            }
        }
    }
}
