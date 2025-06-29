package com.example.rooster.feature.auctions.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions // Was missing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Corrected import
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment // Added
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
// import com.example.rooster.services.TokenService // Will be replaced by TokenRepository
import com.example.rooster.core.common.domain.repository.TokenRepository // Import interface
import com.example.rooster.core.network.repository.ParseTokenRepositoryImpl // Placeholder for injection, ideally via ViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenPurchaseScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    // TODO: Inject via Hilt ViewModel which holds the repository
    tokenRepository: TokenRepository = remember { ParseTokenRepositoryImpl() } // Placeholder DI
) {
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
    var isProcessing by remember { mutableStateOf(false) }
    // var message by remember { mutableStateOf<String?>(null) } // Replaced by snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch { // Use coroutine scope for suspend function
            tokenRepository.loadTokenBalance { newBalance -> balance = newBalance }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isTeluguMode) "టోకెన్ కొనుగోలు" else "Purchase Tokens") }, // TODO: Localize
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") // TODO: Localize
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
                text = (if (isTeluguMode) "ప్రస్తుత టోకెన్ బ్యాలెన్స్:" else "Current Token Balance:") + " $balance", // TODO: Localize
                style = MaterialTheme.typography.titleMedium,
            )

            Text(
                text = if (isTeluguMode) "టోకెన్ ప్యాకేజీని ఎంచుకోండి:" else "Select a Token Package:", // TODO: Localize
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
                            Text(pkg.name, style = MaterialTheme.typography.bodyLarge) // Name is already localized in data class
                            Text(
                                "${pkg.tokenAmount} ${if (isTeluguMode) "టోకెన్లు" else "Tokens"}", // TODO: Localize "Tokens" part
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Text("₹${"%.2f".format(pkg.price)}", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            selectedPackage?.let {
                Text(
                    text = (if (isTeluguMode) "ఎంచుకున్న ప్యాకేజీ: " else "Selected: ") + "${it.name} (${it.tokenAmount} ${if (isTeluguMode) "టోకెన్లు" else "Tokens"} for ₹${"%.2f".format(it.price)})", // TODO: Localize
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = {
                    selectedPackage?.let { pkg ->
                        isProcessing = true
                        // TODO: Here you would initiate actual payment flow for pkg.price via PaymentRepository
                        // For now, using TokenRepository.addTokens as placeholder for successful purchase of pkg.tokenAmount
                        tokenRepository.addTokens(pkg.tokenAmount) { success ->
                            isProcessing = false
                            if (success) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = if (isTeluguMode) "${pkg.tokenAmount} టోకెన్లు విజయవంతంగా చేర్చబడ్డాయి" else "${pkg.tokenAmount} Tokens added successfully",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                                // Re-load balance after adding tokens
                                scope.launch { tokenRepository.loadTokenBalance { newBalance -> balance = newBalance } }
                                selectedPackage = null
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = if (isTeluguMode) "కొనుగోలు విఫలమైంది" else "Purchase failed",
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
                if (isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isTeluguMode) "కొనుగోలు చేయండి" else "Purchase Selected Package") // TODO: Localize
            }
        }
    }
}
