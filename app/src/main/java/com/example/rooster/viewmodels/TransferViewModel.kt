package com.example.rooster.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.CoinCosts
import com.example.rooster.CoinManager
import com.example.rooster.WorkManagerHelper
import com.example.rooster.data.TransferRepository
import com.example.rooster.util.AppEventBus
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseFile
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

// Corrected and consolidated imports
import com.example.rooster.models.ChickenRecord
import com.example.rooster.models.TransferRequest as TransferRequestModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for secure bird ownership transfers with lineage tracking
 */
class TransferViewModel(
    private val transferRepository: TransferRepository,
    private val coinManager: CoinManager,
    private val context: Context,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    private val _coinBalance = MutableStateFlow(0)
    val coinBalance: StateFlow<Int> = _coinBalance.asStateFlow()

    private val _outgoingTransfers = MutableStateFlow<List<TransferRequestModel>>(emptyList())
    val outgoingTransfers: StateFlow<List<TransferRequestModel>> = _outgoingTransfers.asStateFlow()

    private val _incomingTransfers = MutableStateFlow<List<TransferRequestModel>>(emptyList())
    val incomingTransfers: StateFlow<List<TransferRequestModel>> = _incomingTransfers.asStateFlow()

    private val _userAssets = MutableStateFlow<List<ChickenRecord>>(emptyList())
    val userAssets: StateFlow<List<ChickenRecord>> = _userAssets.asStateFlow()

    private val currentUserId: String
        get() = ParseUser.getCurrentUser()?.objectId ?: ""

    init {
        loadCoinBalance()
        loadUserAssets()
        loadTransfers()
    }

    /**
     * Initiate a transfer (sender side)
     */
    fun initiateTransfer(
        fowlId: String,
        recipientUsername: String,
        proofPhotos: List<ParseFile> = emptyList(),
        location: String? = null,
        notes: String? = null,
        onComplete: (Boolean, String?) -> Unit,
    ) {
        if (currentUserId.isEmpty()) {
            onComplete(false, "User not logged in")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // 1. Validate recipient exists
                val recipientId = findUserByUsername(recipientUsername)
                if (recipientId == null) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = "User '$recipientUsername' not found",
                        )
                    onComplete(false, "User not found")
                    return@launch
                }

                // 2. Verify ownership of the asset
                val asset = getAssetById(fowlId)
                if (asset?.currentOwner != currentUserId) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = "You don't own this asset",
                        )
                    onComplete(false, "Asset not owned")
                    return@launch
                }

                // 3. Create transfer request (no coin deduction yet)
                val transferResult =
                    transferRepository.createTransferRequest(
                        fowlId = fowlId,
                        fromOwnerId = currentUserId,
                        toOwnerId = recipientId,
                        proofPhotos = proofPhotos,
                        location = location,
                        notes = notes,
                    )

                if (!transferResult.success) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = transferResult.errorMessage ?: "Failed to create transfer",
                        )
                    onComplete(false, transferResult.errorMessage)
                    return@launch
                }

                FirebaseCrashlytics.getInstance()
                    .log("TransferViewModel: Transfer initiated with ID: ${transferResult.objectId}")

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        success = "Transfer request sent to $recipientUsername successfully!",
                    )

                loadTransfers()
                onComplete(true, null)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to initiate transfer: ${e.message}",
                    )
                onComplete(false, e.message)
            }
        }
    }

    /**
     * Accept an incoming transfer (recipient side) - This triggers coin deduction
     */
    fun acceptTransfer(
        transferId: String,
        onComplete: (Boolean, String?) -> Unit,
    ) {
        if (currentUserId.isEmpty()) {
            onComplete(false, "User not logged in")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // Check coin balance first
                val currentBalance = coinManager.getUserCoinBalance(currentUserId)
                if (currentBalance < CoinCosts.TRANSFER_OWNERSHIP) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = "Insufficient coins. You need ${CoinCosts.TRANSFER_OWNERSHIP} coin(s) but have $currentBalance.",
                        )
                    onComplete(false, "Insufficient coins")
                    return@launch
                }

                // 1. First, process the transfer (update ownership, lineage, etc.)
                FirebaseCrashlytics.getInstance()
                    .log("TransferViewModel: Processing transfer acceptance for ID: $transferId")

                val transferResult = transferRepository.acceptTransfer(transferId)
                if (!transferResult.success) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = transferResult.errorMessage ?: "Failed to accept transfer",
                        )
                    onComplete(false, transferResult.errorMessage)
                    return@launch
                }

                // 2. Only after successful transfer, attempt coin deduction
                val coinSuccess =
                    coinManager.spendCoins(
                        userId = currentUserId,
                        actionLabel = "transfer_ownership",
                        amount = CoinCosts.TRANSFER_OWNERSHIP,
                        linkedObjectId = transferId,
                    )

                if (coinSuccess) {
                    // 3. Mark the transfer record as coinDeducted = true
                    transferRepository.markCoinDeducted(transferId)

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            success = "Transfer accepted successfully! Asset ownership updated. ${CoinCosts.TRANSFER_OWNERSHIP} coin deducted.",
                        )

                    // Notify user of coin deduction
                    AppEventBus.postSnackbar("✅ ${CoinCosts.TRANSFER_OWNERSHIP} coins deducted for transfer acceptance.")

                    loadCoinBalance()
                    loadUserAssets()
                    loadTransfers()
                    onComplete(true, null)
                } else {
                    // 4. Schedule retry if coin deduction fails
                    WorkManagerHelper.scheduleCoinRetry(
                        context = context,
                        userId = currentUserId,
                        actionLabel = "transfer_ownership",
                        amount = CoinCosts.TRANSFER_OWNERSHIP,
                        linkedObjectId = transferId,
                    )

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            success = "Transfer accepted successfully! Asset ownership updated. Your coin will be deducted shortly.",
                            pendingCoinDeduction = true,
                        )

                    // Notify user of pending coin deduction
                    AppEventBus.postSnackbar("✅ ${CoinCosts.TRANSFER_OWNERSHIP} coins will be deducted shortly.")

                    loadUserAssets()
                    loadTransfers()
                    onComplete(true, null)
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to accept transfer: ${e.message}",
                    )
                onComplete(false, e.message)
            }
        }
    }

    /**
     * Reject an incoming transfer
     */
    fun rejectTransfer(
        transferId: String,
        reason: String? = null,
        onComplete: (Boolean, String?) -> Unit,
    ) {
        if (currentUserId.isEmpty()) {
            onComplete(false, "User not logged in")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val transferResult = transferRepository.rejectTransfer(transferId, reason)
                if (!transferResult.success) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = transferResult.errorMessage ?: "Failed to reject transfer",
                        )
                    onComplete(false, transferResult.errorMessage)
                    return@launch
                }

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        success = "Transfer rejected successfully.",
                    )

                loadTransfers()
                onComplete(true, null)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to reject transfer: ${e.message}",
                    )
                onComplete(false, e.message)
            }
        }
    }

    /**
     * Cancel an outgoing transfer (sender side)
     */
    fun cancelTransfer(
        transferId: String,
        onComplete: (Boolean, String?) -> Unit,
    ) {
        if (currentUserId.isEmpty()) {
            onComplete(false, "User not logged in")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val transferResult = transferRepository.cancelTransfer(transferId)
                if (!transferResult.success) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = transferResult.errorMessage ?: "Failed to cancel transfer",
                        )
                    onComplete(false, transferResult.errorMessage)
                    return@launch
                }

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        success = "Transfer cancelled successfully.",
                    )

                loadTransfers()
                onComplete(true, null)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to cancel transfer: ${e.message}",
                    )
                onComplete(false, e.message)
            }
        }
    }

    /**
     * Get asset lineage history
     */
    fun getAssetLineage(
        fowlId: String,
        onComplete: (List<Map<String, Any?>>?, String?) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val lineage = transferRepository.getAssetLineage(fowlId)
                onComplete(lineage, null)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                onComplete(null, e.message)
            }
        }
    }

    /**
     * Load user's transferable assets
     */
    private fun loadUserAssets() {
        viewModelScope.launch {
            try {
                val assets = getUserAssets(currentUserId)
                _userAssets.value = assets
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    /**
     * Load incoming and outgoing transfers
     */
    private fun loadTransfers() {
        viewModelScope.launch {
            try {
                val outgoing = getOutgoingTransfers(currentUserId)
                val incoming = getIncomingTransfers(currentUserId)

                _outgoingTransfers.value = outgoing
                _incomingTransfers.value = incoming
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    private fun loadCoinBalance() {
        viewModelScope.launch {
            try {
                val balance = coinManager.getUserCoinBalance(currentUserId)
                _coinBalance.value = balance
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    // Helper functions
    private suspend fun findUserByUsername(username: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseUser>("_User")
                query.whereEqualTo("username", username)
                val user = query.first
                return@withContext user?.objectId
            } catch (e: Exception) {
                return@withContext null
            }
        }

    private suspend fun getAssetById(fowlId: String): ChickenRecord? =
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery(ChickenRecord::class.java)
                return@withContext query.get(fowlId)
            } catch (e: Exception) {
                return@withContext null
            }
        }

    private suspend fun getUserAssets(userId: String): List<ChickenRecord> =
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery(ChickenRecord::class.java)
                query.whereEqualTo("currentOwner", userId)
                query.whereEqualTo("status", "Active")
                return@withContext query.find()
            } catch (e: Exception) {
                return@withContext emptyList()
            }
        }

    private suspend fun getOutgoingTransfers(userId: String): List<TransferRequestModel> =
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery(TransferRequestModel::class.java)
                query.whereEqualTo("fromOwnerId", userId)
                query.whereNotEqualTo("status", "COMPLETED")
                query.whereNotEqualTo("status", "CANCELLED")
                query.orderByDescending("createdAt")
                return@withContext query.find()
            } catch (e: Exception) {
                return@withContext emptyList()
            }
        }

    private suspend fun getIncomingTransfers(userId: String): List<TransferRequestModel> =
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery(TransferRequestModel::class.java)
                query.whereEqualTo("toOwnerId", userId)
                query.whereEqualTo("status", "PENDING")
                query.orderByDescending("createdAt")
                return@withContext query.find()
            } catch (e: Exception) {
                return@withContext emptyList()
            }
        }

    fun clearMessages() {
        _uiState.value =
            _uiState.value.copy(
                error = null,
                success = null,
                pendingCoinDeduction = false,
            )
    }

    fun refreshData() {
        loadCoinBalance()
        loadUserAssets()
        loadTransfers()
    }
}

/**
 * UI state for transfer operations
 */
data class TransferUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null,
    val pendingCoinDeduction: Boolean = false,
)

/**
 * Data class for transfer initiation
 */
data class TransferInitiationData(
    val fowlId: String,
    val recipientUsername: String,
    val proofPhotos: List<ParseFile> = emptyList(),
    val location: String? = null,
    val notes: String? = null,
)
