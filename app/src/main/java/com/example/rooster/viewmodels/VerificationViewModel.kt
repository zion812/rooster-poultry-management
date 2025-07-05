package com.example.rooster.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.CoinCosts
import com.example.rooster.CoinManager
import com.example.rooster.WorkManagerHelper
import com.example.rooster.data.VerificationRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for bird verification with safe coin spending
 */
class VerificationViewModel(
    private val verificationRepository: VerificationRepository,
    private val coinManager: CoinManager,
    private val context: Context,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VerificationUiState())
    val uiState: StateFlow<VerificationUiState> = _uiState.asStateFlow()

    private val _coinBalance = MutableStateFlow(0)
    val coinBalance: StateFlow<Int> = _coinBalance.asStateFlow()

    private val currentUserId: String
        get() = com.parse.ParseUser.getCurrentUser()?.objectId ?: ""

    init {
        loadCoinBalance()
    }

    /**
     * Perform 15-week verification with safe coin deduction
     */
    fun verifyBird15Week(
        birdId: String,
        verificationData: VerificationData,
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
                if (currentBalance < CoinCosts.VERIFY_15_WEEK) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = "Insufficient coins. You need ${CoinCosts.VERIFY_15_WEEK} coin(s) but have $currentBalance.",
                        )
                    onComplete(false, "Insufficient coins")
                    return@launch
                }

                // 1. Perform the verification backend operation first
                FirebaseCrashlytics.getInstance()
                    .log("VerificationViewModel: Starting 15-week verification for bird $birdId")

                val verificationResult =
                    verificationRepository.perform15WeekVerification(birdId, verificationData)
                if (!verificationResult.success) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = verificationResult.errorMessage ?: "Verification failed",
                        )
                    onComplete(false, verificationResult.errorMessage)
                    return@launch
                }

                val verificationRecordId = verificationResult.objectId ?: ""
                FirebaseCrashlytics.getInstance()
                    .log("VerificationViewModel: Verification completed with ID: $verificationRecordId")

                // 2. Only after successful verification, attempt coin deduction
                val coinSuccess =
                    coinManager.spendCoins(
                        userId = currentUserId,
                        actionLabel = "15_week_verification",
                        amount = CoinCosts.VERIFY_15_WEEK,
                        linkedObjectId = verificationRecordId,
                    )

                if (coinSuccess) {
                    // 3. Mark the verification record as coinDeducted = true
                    verificationRepository.markCoinDeducted(verificationRecordId)

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            lastVerificationId = verificationRecordId,
                            success = "15-week verification completed successfully! 1 coin deducted.",
                        )

                    loadCoinBalance()
                    onComplete(true, null)
                } else {
                    // 4. Schedule retry if coin deduction fails
                    WorkManagerHelper.scheduleCoinRetry(
                        context = context,
                        userId = currentUserId,
                        actionLabel = "15_week_verification",
                        amount = CoinCosts.VERIFY_15_WEEK,
                        linkedObjectId = verificationRecordId,
                    )

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            lastVerificationId = verificationRecordId,
                            success = "15-week verification completed successfully! Your coin will be deducted shortly.",
                            pendingCoinDeduction = true,
                        )

                    onComplete(true, null)
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = "An unexpected error occurred: ${e.message}",
                    )
                onComplete(false, e.message)
            }
        }
    }

    /**
     * Perform 40-week re-verification (costs more coins)
     */
    fun verifyBird40Week(
        birdId: String,
        verificationData: VerificationData,
        onComplete: (Boolean, String?) -> Unit,
    ) {
        if (currentUserId.isEmpty()) {
            onComplete(false, "User not logged in")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val currentBalance = coinManager.getUserCoinBalance(currentUserId)
                if (currentBalance < CoinCosts.VERIFY_40_WEEK) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = "Insufficient coins. You need ${CoinCosts.VERIFY_40_WEEK} coin(s) but have $currentBalance.",
                        )
                    onComplete(false, "Insufficient coins")
                    return@launch
                }

                val verificationResult =
                    verificationRepository.perform40WeekVerification(birdId, verificationData)
                if (!verificationResult.success) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = verificationResult.errorMessage ?: "Re-verification failed",
                        )
                    onComplete(false, verificationResult.errorMessage)
                    return@launch
                }

                val verificationRecordId = verificationResult.objectId ?: ""

                val coinSuccess =
                    coinManager.spendCoins(
                        userId = currentUserId,
                        actionLabel = "40_week_verification",
                        amount = CoinCosts.VERIFY_40_WEEK,
                        linkedObjectId = verificationRecordId,
                    )

                if (coinSuccess) {
                    verificationRepository.markCoinDeducted(verificationRecordId)

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            lastVerificationId = verificationRecordId,
                            success = "40-week re-verification completed successfully! ${CoinCosts.VERIFY_40_WEEK} coins deducted.",
                        )

                    loadCoinBalance()
                    onComplete(true, null)
                } else {
                    WorkManagerHelper.scheduleCoinRetry(
                        context = context,
                        userId = currentUserId,
                        actionLabel = "40_week_verification",
                        amount = CoinCosts.VERIFY_40_WEEK,
                        linkedObjectId = verificationRecordId,
                    )

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            lastVerificationId = verificationRecordId,
                            success = "40-week re-verification completed successfully! Your ${CoinCosts.VERIFY_40_WEEK} coins will be deducted shortly.",
                            pendingCoinDeduction = true,
                        )

                    onComplete(true, null)
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = "An unexpected error occurred: ${e.message}",
                    )
                onComplete(false, e.message)
            }
        }
    }

    /**
     * Process annual maintenance charge
     */
    fun processAnnualMaintenance(
        fowlIds: List<String>,
        onComplete: (Boolean, String?) -> Unit,
    ) {
        if (currentUserId.isEmpty()) {
            onComplete(false, "User not logged in")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val totalCost = fowlIds.size * CoinCosts.ANNUAL_MAINTENANCE
                val currentBalance = coinManager.getUserCoinBalance(currentUserId)

                if (currentBalance < totalCost) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = "Insufficient coins. You need $totalCost coin(s) but have $currentBalance.",
                        )
                    onComplete(false, "Insufficient coins")
                    return@launch
                }

                // Process annual maintenance for all fowl
                val maintenanceResult = verificationRepository.processAnnualMaintenance(fowlIds)
                if (!maintenanceResult.success) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error =
                                maintenanceResult.errorMessage
                                    ?: "Annual maintenance processing failed",
                        )
                    onComplete(false, maintenanceResult.errorMessage)
                    return@launch
                }

                val maintenanceRecordId = maintenanceResult.objectId ?: ""

                val coinSuccess =
                    coinManager.spendCoins(
                        userId = currentUserId,
                        actionLabel = "annual_maintenance",
                        amount = totalCost,
                        linkedObjectId = maintenanceRecordId,
                    )

                if (coinSuccess) {
                    verificationRepository.markCoinDeducted(maintenanceRecordId)

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            lastVerificationId = maintenanceRecordId,
                            success = "Annual maintenance processed for ${fowlIds.size} fowl. $totalCost coins deducted.",
                        )

                    loadCoinBalance()
                    onComplete(true, null)
                } else {
                    WorkManagerHelper.scheduleCoinRetry(
                        context = context,
                        userId = currentUserId,
                        actionLabel = "annual_maintenance",
                        amount = totalCost,
                        linkedObjectId = maintenanceRecordId,
                    )

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            lastVerificationId = maintenanceRecordId,
                            success =
                                "Annual maintenance processed for ${fowlIds.size} fowl. " +
                                    "Your $totalCost coins will be deducted shortly.",
                            pendingCoinDeduction = true,
                        )

                    onComplete(true, null)
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = "An unexpected error occurred: ${e.message}",
                    )
                onComplete(false, e.message)
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

    fun clearMessages() {
        _uiState.value =
            _uiState.value.copy(error = null, success = null, pendingCoinDeduction = false)
    }
}

/**
 * UI state for verification operations
 */
data class VerificationUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null,
    val lastVerificationId: String? = null,
    val pendingCoinDeduction: Boolean = false,
)

/**
 * Data for verification operations
 */
data class VerificationData(
    val weight: Double,
    val height: Double,
    val color: String,
    val healthStatus: String,
    val photos: List<String> = emptyList(),
    val notes: String? = null,
    val location: String? = null,
)
