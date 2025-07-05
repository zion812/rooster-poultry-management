package com.example.rooster.financials.commission

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Data Classes ---
data class CommissionRate(
    val key: String, // e.g., "default", "electronics_category"
    var rate: Double, // e.g., 0.05 for 5%
    var description: String,
    var updatedAt: Date? = null,
    var updatedBy: String? = null
) {
    val displayRate: String get() = "${(rate * 100).format(2)}%"
    fun formattedUpdatedAt(): String? = updatedAt?.let {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(it)
    }
}

data class CommissionedTransaction(
    val transactionId: String,
    val sellerId: String,
    val saleAmount: Double,
    val category: String,
    val transactionDate: Date,
    val commissionRateKey: String,
    val commissionRateApplied: Double,
    val commissionAmount: Double,
    val payoutStatus: String // "pending", "paid_out"
) {
    fun formattedTransactionDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(transactionDate)
    val displayCommissionRateApplied: String get() = "${(commissionRateApplied * 100).format(1)}%"
}

// Extension to format double to specific decimal places
fun Double.format(digits: Int) = "%.${digits}f".format(this)


// --- ViewModel ---
class CommissionManagementViewModel : ViewModel() {
    private val _commissionRates = MutableStateFlow<List<CommissionRate>>(emptyList())
    val commissionRates: StateFlow<List<CommissionRate>> = _commissionRates

    private val _commissionedTransactions = MutableStateFlow<List<CommissionedTransaction>>(emptyList())
    val commissionedTransactions: StateFlow<List<CommissionedTransaction>> = _commissionedTransactions

    // For displaying pending payouts for a specific seller (example)
    private val _pendingSellerPayouts = MutableStateFlow<List<CommissionedTransaction>>(emptyList())
    val pendingSellerPayouts: StateFlow<List<CommissionedTransaction>> = _pendingSellerPayouts
    var displayedSellerIdForPayouts by mutableStateOf("seller002") // Default or can be changed

    init {
        loadMockData()
        filterPendingPayoutsForSeller(displayedSellerIdForPayouts)
    }

    private fun loadMockData() {
        val now = Date()
        _commissionRates.value = listOf(
            CommissionRate("default", 0.05, "Standard commission rate", now, "system"),
            CommissionRate("electronics_category", 0.08, "Commission for electronics", now, "system"),
            CommissionRate("veterinary_services", 0.10, "Commission for vet services", Date(now.time - 86400000L), "admin_jane"),
            CommissionRate("premium_seller_tier", 0.03, "Reduced commission for premium sellers", Date(now.time - 2*86400000L), "admin_john")
        ).sortedBy { it.key }

        val transactions = mutableListOf<CommissionedTransaction>()
        val sellerIds = (1..5).map { "seller${it.toString().padStart(3, '0')}" }
        val categories = listOf("general", "electronics_category", "veterinary_services", "books")
        for (i in 0..19) {
            val category = categories.random()
            val rateDetails = _commissionRates.value.find { it.key == category } ?: _commissionRates.value.first { it.key == "default" }
            val saleAmount = (20..300).random().toDouble()
            transactions.add(
                CommissionedTransaction(
                    transactionId = "sale_txn_${now.time - (0..30).random() * 86400000L}_${i.toString().padStart(3, '0')}",
                    sellerId = sellerIds.random(),
                    saleAmount = saleAmount,
                    category = category,
                    transactionDate = Date(now.time - (0..30).random() * 86400000L),
                    commissionRateKey = rateDetails.key,
                    commissionRateApplied = rateDetails.rate,
                    commissionAmount = saleAmount * rateDetails.rate,
                    payoutStatus = if (Math.random() > 0.5) "pending" else "paid_out"
                )
            )
        }
        _commissionedTransactions.value = transactions.sortedByDescending { it.transactionDate }
    }

    fun setCommissionRate(key: String, newRate: Double, newDescription: String, adminId: String) {
        if (newRate < 0 || newRate > 1) {
            println("Error: Rate must be between 0 and 1.")
            return
        }
        _commissionRates.update { currentRates ->
            val existingRate = currentRates.find { it.key == key }
            if (existingRate != null) {
                currentRates.map {
                    if (it.key == key) it.copy(rate = newRate, description = newDescription, updatedAt = Date(), updatedBy = adminId)
                    else it
                }
            } else {
                currentRates + CommissionRate(key, newRate, newDescription, Date(), adminId)
            }.sortedBy { it.key }
        }
    }

    fun filterPendingPayoutsForSeller(sellerId: String) {
        displayedSellerIdForPayouts = sellerId
        _pendingSellerPayouts.value = _commissionedTransactions.value.filter {
            it.sellerId == sellerId && it.payoutStatus == "pending"
        }
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommissionManagementScreen(viewModel: CommissionManagementViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val rates by viewModel.commissionRates.collectAsState()
    val transactions by viewModel.commissionedTransactions.collectAsState()
    val pendingPayouts by viewModel.pendingSellerPayouts.collectAsState()
    val displayedSellerId by remember { derivedStateOf { viewModel.displayedSellerIdForPayouts } }


    var showEditRateDialog by remember { mutableStateOf(false) }
    var editingRate by remember { mutableStateOf<CommissionRate?>(null) }

    if (showEditRateDialog && editingRate != null) {
        EditCommissionRateDialog(
            rate = editingRate!!,
            onDismiss = { showEditRateDialog = false; editingRate = null },
            onSave = { key, newRate, newDesc ->
                viewModel.setCommissionRate(key, newRate, newDesc, "admin_compose")
                showEditRateDialog = false
                editingRate = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Commission Management") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFA726)) // Orange
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // For simplicity, editing an existing or adding a new one could use same dialog.
                // Here, let's make it trigger an add for "new_category_rate"
                editingRate = CommissionRate("new_category_rate", 0.12, "New custom rate", null, null)
                showEditRateDialog = true
            }) {
                Icon(Icons.Filled.Add, "Add new commission rate")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            item {
                Text("Current Commission Rates", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 8.dp))
            }
            items(rates, key = {it.key}) { rate ->
                CommissionRateCard(rate) {
                    editingRate = it
                    showEditRateDialog = true
                }
            }

            item {
                Text("Recent Commissioned Transactions (Top 5)", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            }
            items(transactions.take(5)) { tx ->
                CommissionedTransactionCard(tx)
            }

            item {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text("Pending Payouts for Seller: $displayedSellerId", style = MaterialTheme.typography.headlineSmall)
                     // Basic filter input - in real app more robust
                    var sellerInputId by remember { mutableStateOf(displayedSellerId) }
                    OutlinedTextField(
                        value = sellerInputId,
                        onValueChange = { sellerInputId = it },
                        label = { Text("Enter Seller ID to Filter")},
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        trailingIcon = { Button(onClick = { viewModel.filterPendingPayoutsForSeller(sellerInputId.trim())}) { Text("Filter") } }
                    )
                }
            }
            if(pendingPayouts.isEmpty()){
                item { Text("No pending payouts for $displayedSellerId") }
            } else {
                items(pendingPayouts) {tx ->
                     CommissionedTransactionCard(tx, isPayoutView = true)
                }
                item {
                    val totalPending = pendingPayouts.sumOf { it.commissionAmount }
                    Text("Total Pending for $displayedSellerId: $${totalPending.format(2)}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top=8.dp))
                }
            }
        }
    }
}

@Composable
fun CommissionRateCard(rate: CommissionRate, onEditClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(rate.key, style = MaterialTheme.typography.titleMedium)
                Text("${rate.description} - Rate: ${rate.displayRate}")
                rate.formattedUpdatedAt()?.let {
                    Text("Updated: $it by ${rate.updatedBy ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = onEditClick) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Rate")
            }
        }
    }
}

@Composable
fun CommissionedTransactionCard(tx: CommissionedTransaction, isPayoutView: Boolean = false) {
    val decimalFormat = DecimalFormat("#,##0.00")
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text("TXN ID: ${tx.transactionId}", style = MaterialTheme.typography.titleSmall)
            if(!isPayoutView) Text("Seller: ${tx.sellerId}")
            Text("Sale: $${decimalFormat.format(tx.saleAmount)} (${tx.category}) on ${tx.formattedTransactionDate()}")
            Text("Commission: $${decimalFormat.format(tx.commissionAmount)} (${tx.displayCommissionRateApplied} of ${tx.commissionRateKey})", color = MaterialTheme.colorScheme.primary)
            if(!isPayoutView) Text("Payout Status: ${tx.payoutStatus}", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun EditCommissionRateDialog(rate: CommissionRate, onDismiss: () -> Unit, onSave: (String, Double, String) -> Unit) {
    var currentRate by remember { mutableStateOf((rate.rate * 100).format(2)) } // Store as percentage string
    var currentDesc by remember { mutableStateOf(rate.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Commission Rate: ${rate.key}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = currentRate,
                    onValueChange = { currentRate = it.filter { c -> c.isDigit() || c == '.'} },
                    label = { Text("Rate (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = currentDesc,
                    onValueChange = { currentDesc = it },
                    label = { Text("Description") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val rateValue = currentRate.toDoubleOrNull()
                if (rateValue != null && rateValue >= 0 && rateValue <= 100) {
                    onSave(rate.key, rateValue / 100.0, currentDesc)
                } else {
                    // TODO: Show error to user about invalid rate input
                }
            }) { Text("Save") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCommissionManagementScreen() {
    MaterialTheme {
        CommissionManagementScreen(viewModel = CommissionManagementViewModel())
    }
}
