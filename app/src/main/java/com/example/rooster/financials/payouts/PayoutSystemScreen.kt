package com.example.rooster.financials.payouts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.flow.update
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// --- Data Classes ---
data class SellerBalance(
    val sellerId: String,
    var balance: Double
)

data class PayoutMethodDetails(
    val method: String, // "paypal", "bank_transfer", etc.
    val accountId: String
)

data class PayoutRequest(
    val requestId: String,
    val sellerId: String,
    val amountRequested: Double,
    val currency: String = "USD",
    val requestedAt: Date,
    val payoutMethodDetails: PayoutMethodDetails,
    var status: String, // pending_approval, approved, processing, completed, rejected
    var notes: String? = null,
    var approvedBy: String? = null,
    var approvedAt: Date? = null,
    var processedBy: String? = null,
    var processedAt: Date? = null,
    var completedAt: Date? = null,
    var transactionReference: String? = null
) {
    fun getFormattedDate(date: Date?): String =
        date?.let { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(it) } ?: "N/A"
}

// --- ViewModel ---
class PayoutSystemViewModel : ViewModel() {
    private val _sellerBalances = MutableStateFlow<List<SellerBalance>>(emptyList())
    val sellerBalances: StateFlow<List<SellerBalance>> = _sellerBalances

    private val _payoutRequests = MutableStateFlow<List<PayoutRequest>>(emptyList())
    val payoutRequests: StateFlow<List<PayoutRequest>> = _payoutRequests // Active requests

    private val _payoutHistory = MutableStateFlow<List<PayoutRequest>>(emptyList())
    val payoutHistory: StateFlow<List<PayoutRequest>> = _payoutHistory

    val pendingApprovalRequests: StateFlow<List<PayoutRequest>> = MutableStateFlow(emptyList())
    val approvedForProcessingRequests: StateFlow<List<PayoutRequest>> = MutableStateFlow(emptyList())


    init {
        loadMockData()
        updateDerivedRequestLists()
    }

    private fun loadMockData() {
        val sIds = (1..5).map { "seller${it.toString().padStart(3, '0')}" }
        _sellerBalances.value = sIds.map { SellerBalance(it, Random.nextDouble(50.0, 2000.0)) }

        val methods = listOf("paypal", "bank_transfer", "payoneer")
        val initialRequests = mutableListOf<PayoutRequest>()
        val initialHistory = mutableListOf<PayoutRequest>()

        for (i in 0..4) { // Pending requests
            val sellerId = sIds.random()
            val balance = _sellerBalances.value.first { it.sellerId == sellerId }.balance
            if (balance < 20) continue
            initialRequests.add(
                PayoutRequest(
                    requestId = "req_${Date().time}_$i",
                    sellerId = sellerId,
                    amountRequested = Random.nextDouble(20.0, minOf(balance, 500.0)),
                    requestedAt = Date(System.currentTimeMillis() - Random.nextLong(0, 5 * 86400000L)),
                    payoutMethodDetails = PayoutMethodDetails(methods.random(), "acc_${Random.nextInt(1000, 9999)}"),
                    status = "pending_approval"
                )
            )
        }
        _payoutRequests.value = initialRequests.sortedBy { it.requestedAt }

        for (i in 0..9) { // Historical payouts
             val sellerIdHist = sIds.random()
            initialHistory.add(
                PayoutRequest(
                    requestId = "hist_${Date().time - Random.nextLong(1,60) * 86400000L}_$i", // Make it distinct from requestID
                    sellerId = sellerIdHist,
                    amountRequested = Random.nextDouble(20.0, 500.0),
                    requestedAt = Date(System.currentTimeMillis() - Random.nextLong(60, 120) * 86400000L), // Older request date
                    payoutMethodDetails = PayoutMethodDetails(methods.random(), "acc_${Random.nextInt(1000, 9999)}"),
                    status = "completed",
                    completedAt = Date(System.currentTimeMillis() - Random.nextLong(1, 60) * 86400000L),
                    processedBy = "admin_auto",
                    transactionReference = "ref_${Random.nextInt(100000, 999999)}"
                ).apply { amountRequested = this.amountRequested } // Ensure amountPaid is set in a real scenario
            )
        }
        _payoutHistory.value = initialHistory.sortedByDescending { it.completedAt }
    }

    private fun updateDerivedRequestLists() {
        (pendingApprovalRequests as MutableStateFlow).value = _payoutRequests.value.filter { it.status == "pending_approval" }
        (approvedForProcessingRequests as MutableStateFlow).value = _payoutRequests.value.filter { it.status == "approved" }
    }

    fun approvePayoutRequest(requestId: String, adminId: String) {
        _payoutRequests.update { requests ->
            requests.map { req ->
                if (req.requestId == requestId && req.status == "pending_approval") {
                    val sellerBalance = _sellerBalances.value.firstOrNull { it.sellerId == req.sellerId }?.balance ?: 0.0
                    if (sellerBalance >= req.amountRequested) {
                        req.copy(status = "approved", approvedBy = adminId, approvedAt = Date())
                    } else {
                        req.copy(status = "rejected", notes = "Insufficient balance", processedBy = adminId, processedAt = Date())
                    }
                } else req
            }
        }
        updateDerivedRequestLists()
    }

    fun processPayout(requestId: String, adminId: String, txnReference: String) {
        var processedRequest: PayoutRequest? = null
        _payoutRequests.update { requests ->
            requests.mapNotNull { req ->
                if (req.requestId == requestId && req.status == "approved") {
                    processedRequest = req.copy(
                        status = "completed", // Simulate immediate completion
                        processedBy = adminId,
                        processedAt = Date(),
                        completedAt = Date(),
                        transactionReference = txnReference
                    )
                    // Deduct balance
                    _sellerBalances.update { balances ->
                        balances.map { if (it.sellerId == processedRequest!!.sellerId) it.copy(balance = it.balance - processedRequest!!.amountRequested) else it }
                    }
                    null // Remove from active requests
                } else req
            }
        }
        processedRequest?.let {
            _payoutHistory.update { history -> (listOf(it) + history).sortedByDescending { h -> h.completedAt } }
        }
        updateDerivedRequestLists()
    }

    fun rejectPayoutRequest(requestId: String, reason: String, adminId: String) {
        _payoutRequests.update { requests ->
            requests.map { req ->
                if (req.requestId == requestId && (req.status == "pending_approval" || req.status == "approved")) {
                    req.copy(status = "rejected", notes = reason, processedBy = adminId, processedAt = Date())
                } else req
            }
        }
        // Rejected requests could be moved to history or a separate list. Here they remain in _payoutRequests with 'rejected' status.
        updateDerivedRequestLists()
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayoutSystemScreen(viewModel: PayoutSystemViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val sellerBalances by viewModel.sellerBalances.collectAsState()
    val pendingApproval by viewModel.pendingApprovalRequests.collectAsState()
    val approvedForProcessing by viewModel.approvedForProcessingRequests.collectAsState()
    val history by viewModel.payoutHistory.collectAsState()

    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectReason by remember { mutableStateOf("") }
    var requestToReject by remember { mutableStateOf<PayoutRequest?>(null) }

    if (showRejectDialog && requestToReject != null) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Reject Payout Request") },
            text = {
                Column {
                    Text("Request ID: ${requestToReject!!.requestId}")
                    Text("Amount: $${requestToReject!!.amountRequested.format(2)}")
                    OutlinedTextField(value = rejectReason, onValueChange = { rejectReason = it}, label = { Text("Reason for Rejection")})
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.rejectPayoutRequest(requestToReject!!.requestId, rejectReason, "admin_compose")
                    showRejectDialog = false
                    rejectReason = ""
                }) { Text("Confirm Rejection") }
            },
            dismissButton = { Button(onClick = { showRejectDialog = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Payout System") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF66BB6A))) // Green
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            item { SectionTitle("Seller Balances (Top 3)") }
            items(sellerBalances.take(3)) { balance -> SellerBalanceCard(balance) }

            item { SectionTitle("Pending Approval (${pendingApproval.size})") }
            if (pendingApproval.isEmpty()) item { EmptyState("No requests pending approval.") }
            items(pendingApproval) { req ->
                PayoutRequestCard(req,
                    onApprove = { viewModel.approvePayoutRequest(req.requestId, "admin_compose") },
                    onReject = { requestToReject = req; showRejectDialog = true; }
                )
            }

            item { SectionTitle("Approved - Ready to Process (${approvedForProcessing.size})") }
             if (approvedForProcessing.isEmpty()) item { EmptyState("No requests ready for processing.") }
            items(approvedForProcessing) { req ->
                PayoutRequestCard(req,
                    onProcess = { viewModel.processPayout(req.requestId, "admin_compose", "mock_txn_${Random.nextInt(10000)}") },
                    onReject = { requestToReject = req; showRejectDialog = true; }
                )
            }

            item { SectionTitle("Payout History (Last 5)") }
            if (history.isEmpty()) item { EmptyState("No payout history.") }
            items(history.take(5)) { hist -> PayoutHistoryCard(hist) }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
}

@Composable
fun EmptyState(message: String) {
    Text(message, modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
fun SellerBalanceCard(balance: SellerBalance) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Seller ID: ${balance.sellerId}", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.weight(1f))
            Text("Balance: $${balance.balance.format(2)}", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PayoutRequestCard(
    req: PayoutRequest,
    onApprove: (() -> Unit)? = null,
    onProcess: (() -> Unit)? = null,
    onReject: (() -> Unit)? = null
) {
    val df = DecimalFormat("$ #,##0.00")
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text("Req ID: ${req.requestId}", style = MaterialTheme.typography.titleSmall)
            Text("Seller: ${req.sellerId} - Amount: ${df.format(req.amountRequested)} ${req.currency}")
            Text("Method: ${req.payoutMethodDetails.method} (${req.payoutMethodDetails.accountId})")
            Text("Requested: ${req.getFormattedDate(req.requestedAt)}")
            Text("Status: ${req.status}", color = when(req.status){
                "pending_approval" -> Color.Blue
                "approved" -> Color(0xFFFFA000) // Amber
                "rejected" -> Color.Red
                else -> Color.DarkGray
            })
            if (req.status == "rejected" && req.notes != null) Text("Notes: ${req.notes}", color = Color.Red)

            Row(Modifier.padding(top = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                onApprove?.let { Button(onClick = it) { Text("Approve") } }
                onProcess?.let { Button(onClick = it, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) { Text("Process") } }
                Spacer(Modifier.width(8.dp))
                onReject?.let { Button(onClick = it, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Reject") } }
            }
        }
    }
}

@Composable
fun PayoutHistoryCard(payout: PayoutRequest) {
     val df = DecimalFormat("$ #,##0.00")
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text("ID: ${payout.transactionReference ?: payout.requestId}", style = MaterialTheme.typography.titleSmall)
            Text("Seller: ${payout.sellerId} - Amount: ${df.format(payout.amountRequested)} ${payout.currency}")
            Text("Status: ${payout.status}", fontWeight = FontWeight.Bold)
            Text("Method: ${payout.payoutMethodDetails.method}")
            Text("Completed: ${payout.getFormattedDate(payout.completedAt)}")
            payout.transactionReference?.let { Text("Reference: $it") }
        }
    }
}

// Double.format extension from CommissionManagementScreen, ensure it's accessible or redefine.
// For simplicity, assuming it's available or this file is part of the same module/scope.
// If not, uncomment and use this:
// fun Double.format(digits: Int) = "%.${digits}f".format(this)

@Preview(showBackground = true)
@Composable
fun PreviewPayoutSystemScreen() {
    MaterialTheme {
        PayoutSystemScreen(viewModel = PayoutSystemViewModel())
    }
}
