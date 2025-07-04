package com.example.rooster.financials.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// --- Data Classes ---
data class ManualReviewFlag(
    val reason: String,
    val adminId: String,
    val timestamp: Date
) {
    fun getFormattedTimestamp(): String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(timestamp)
}

data class Transaction(
    val transactionId: String,
    val timestamp: Date,
    val userId: String,
    val amount: Double,
    val currency: String = "USD",
    val paymentMethod: String,
    val status: String, // completed, pending, failed, refunded, disputed
    val transactionType: String,
    val description: String,
    val fraudFlags: List<String>,
    val ipAddress: String,
    val originalTransactionId: String? = null,
    var manualReviewFlags: MutableList<ManualReviewFlag> = mutableListOf()
) {
    fun getFormattedTimestamp(): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(timestamp)
}

// --- Filter State ---
data class TransactionFilters(
    val status: String? = null,
    val userId: String? = null,
    val minAmount: Double? = null,
    val hasFraudFlags: Boolean? = null // null = any, true = has flags, false = no flags
)

// --- ViewModel ---
class TransactionMonitoringViewModel : ViewModel() {
    private val _allTransactions = MutableStateFlow<List<Transaction>>(emptyList())

    private val _filters = MutableStateFlow(TransactionFilters())
    val filters: StateFlow<TransactionFilters> = _filters

    val filteredTransactions: StateFlow<List<Transaction>> = combine(_allTransactions, _filters) { transactions, filters ->
        transactions.filter { tx ->
            (filters.status == null || tx.status.equals(filters.status, ignoreCase = true)) &&
            (filters.userId == null || tx.userId.equals(filters.userId, ignoreCase = true)) &&
            (filters.minAmount == null || tx.amount >= filters.minAmount) &&
            (filters.hasFraudFlags == null || (filters.hasFraudFlags == true && tx.fraudFlags.isNotEmpty()) || (filters.hasFraudFlags == false && tx.fraudFlags.isEmpty()))
        }.sortedByDescending { it.timestamp }
    }.stateIn(kotlinx.coroutines.MainScope(), kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())


    init {
        loadMockTransactions()
    }

    private fun loadMockTransactions(count: Int = 50) {
        val paymentMethods = listOf("credit_card", "paypal", "bank_transfer", "crypto")
        val statuses = listOf("completed", "pending", "failed", "refunded", "disputed")
        val txTypes = listOf("consultation_fee", "marketplace_sale", "subscription", "payout_fee", "refund")
        val userIds = (1..5).map { "user${it.toString().padStart(3, '0')}" }
        val productIds = (1..3).map { "prod${it.toString().padStart(3, '0')}" }
        val now = System.currentTimeMillis()

        val tempTransactions = mutableListOf<Transaction>()
        for (i in 0 until count) {
            val amount = Random.nextDouble(5.0, 500.0)
            val status = statuses.random()
            val fraudFlags = mutableListOf<String>()
            if (amount > 400 && Random.nextBoolean()) fraudFlags.add("high_value_transaction")
            if (status == "failed" && Random.nextDouble() < 0.2) fraudFlags.add("multiple_failed_attempts_suspected")
            if (Random.nextDouble() < 0.05) fraudFlags.add("unusual_location_match")

            tempTransactions.add(
                Transaction(
                    transactionId = "txn_${now - Random.nextLong(0, 30 * 86400000L * 24 * 60)}_$i",
                    timestamp = Date(now - Random.nextLong(0, 30 * 86400000L)),
                    userId = userIds.random(),
                    amount = amount,
                    paymentMethod = paymentMethods.random(),
                    status = status,
                    transactionType = txTypes.random(),
                    description = "${txTypes.random().replace('_', ' ').titlecase()} for ${if (txTypes.last().contains("sale")) productIds.random() else userIds.random()}",
                    fraudFlags = fraudFlags,
                    ipAddress = "192.168.1.${Random.nextInt(1, 254)}",
                    originalTransactionId = if (status == "refunded") "txn_orig_${Random.nextInt(1000)}" else null
                )
            )
        }
        _allTransactions.value = tempTransactions
    }

    fun updateFilters(newFilters: TransactionFilters) {
        _filters.value = newFilters
    }

    fun flagTransactionForReview(transactionId: String, reason: String, adminId: String) {
        _allTransactions.update { transactions ->
            transactions.map {
                if (it.transactionId == transactionId) {
                    it.copy(manualReviewFlags = (it.manualReviewFlags + ManualReviewFlag(reason, adminId, Date())).toMutableList())
                } else it
            }
        }
    }
}
// Helper for title case if needed elsewhere
fun String.titlecase(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }


// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionMonitoringScreen(viewModel: TransactionMonitoringViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val transactions by viewModel.filteredTransactions.collectAsState()
    val currentFilters by viewModel.filters.collectAsState()

    var showFilterDialog by remember { mutableStateOf(false) }
    var showFlagDialog by remember { mutableStateOf(false) }
    var transactionToFlag by remember { mutableStateOf<Transaction?>(null) }
    var flagReason by remember { mutableStateOf("") }

    if (showFilterDialog) {
        FilterDialog(
            currentFilters = currentFilters,
            onDismiss = { showFilterDialog = false },
            onApplyFilters = { newFilters ->
                viewModel.updateFilters(newFilters)
                showFilterDialog = false
            }
        )
    }

    if (showFlagDialog && transactionToFlag != null) {
        FlagTransactionDialog(
            transaction = transactionToFlag!!,
            reason = flagReason,
            onReasonChange = { flagReason = it },
            onDismiss = { showFlagDialog = false; transactionToFlag = null; flagReason = "" },
            onConfirm = {
                viewModel.flagTransactionForReview(transactionToFlag!!.transactionId, flagReason, "admin_compose")
                showFlagDialog = false
                transactionToFlag = null
                flagReason = ""
            }
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Monitoring") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF78909C)), // Blue Grey
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Filled.Search, contentDescription = "Filter Transactions")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp, vertical = 8.dp)) {
            if (transactions.isEmpty()){
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No transactions match current filters.")
                    }
                }
            } else {
                items(transactions, key = { it.transactionId }) { tx ->
                    TransactionCard(tx) {
                        transactionToFlag = tx
                        showFlagDialog = true
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionCard(tx: Transaction, onFlagClick: () -> Unit) {
    val df = remember { DecimalFormat("$ #,##0.00") }
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Text(tx.transactionId, style = MaterialTheme.typography.titleSmall)
                IconButton(onClick = onFlagClick, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Filled.Flag, "Flag for Review", tint = if (tx.manualReviewFlags.isNotEmpty()) Color.Red else Color.Gray)
                }
            }
            Text("User: ${tx.userId}, Amount: ${df.format(tx.amount)} ${tx.currency}")
            Text("Type: ${tx.transactionType.titlecase()}, Method: ${tx.paymentMethod.titlecase()}")
            Text("Status: ${tx.status.titlecase()}", color = when(tx.status){
                "completed" -> Color.Green
                "pending" -> Color.Blue
                "failed" -> Color.Red
                "refunded" -> Color.Magenta
                "disputed" -> Color.Yellow // Choose appropriate color
                else -> Color.DarkGray
            })
            Text("Time: ${tx.getFormattedTimestamp()}, IP: ${tx.ipAddress}")
            if (tx.fraudFlags.isNotEmpty()) {
                Text("Fraud Flags: ${tx.fraudFlags.joinToString()}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }
            tx.manualReviewFlags.lastOrNull()?.let {
                 Text("Manual Review: ${it.reason} by ${it.adminId} at ${it.getFormattedTimestamp()}", color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun FilterDialog(
    currentFilters: TransactionFilters,
    onDismiss: () -> Unit,
    onApplyFilters: (TransactionFilters) -> Unit
) {
    var status by remember { mutableStateOf(currentFilters.status ?: "") }
    var userId by remember { mutableStateOf(currentFilters.userId ?: "") }
    var minAmount by remember { mutableStateOf(currentFilters.minAmount?.toString() ?: "") }
    var hasFraudFlags by remember { mutableStateOf(currentFilters.hasFraudFlags) } // null, true, false

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Transactions") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Status (e.g., completed)") })
                OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("User ID") })
                OutlinedTextField(value = minAmount, onValueChange = { minAmount = it.filter{c -> c.isDigit() || c == '.'} }, label = { Text("Min Amount") })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Fraud Flags: ")
                    Spacer(Modifier.width(8.dp))
                    RadioButton(selected = hasFraudFlags == true, onClick = { hasFraudFlags = true })
                    Text("Yes")
                    Spacer(Modifier.width(8.dp))
                    RadioButton(selected = hasFraudFlags == false, onClick = { hasFraudFlags = false })
                    Text("No")
                     Spacer(Modifier.width(8.dp))
                    RadioButton(selected = hasFraudFlags == null, onClick = { hasFraudFlags = null })
                    Text("Any")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onApplyFilters(TransactionFilters(
                    status = status.ifBlank { null },
                    userId = userId.ifBlank { null },
                    minAmount = minAmount.toDoubleOrNull(),
                    hasFraudFlags = hasFraudFlags
                ))
            }) { Text("Apply") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun FlagTransactionDialog(
    transaction: Transaction,
    reason: String,
    onReasonChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Flag Transaction: ${transaction.transactionId}") },
        text = {
            OutlinedTextField(
                value = reason,
                onValueChange = onReasonChange,
                label = { Text("Reason for flagging") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = { Button(onClick = onConfirm, enabled = reason.isNotBlank()) { Text("Flag") } },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTransactionMonitoringScreen() {
    MaterialTheme {
        TransactionMonitoringScreen(viewModel = TransactionMonitoringViewModel())
    }
}
