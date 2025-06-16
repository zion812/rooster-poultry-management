package com.example.rooster.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.CoinCosts
import com.example.rooster.CoinManager
import com.example.rooster.WorkManagerHelper
import com.example.rooster.data.RoosterRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for rooster listing with safe coin spending
 */
class RoosterListingViewModel(
    private val roosterRepository: RoosterRepository,
    private val coinManager: CoinManager,
    private val context: Context,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RoosterListingUiState())
    val uiState: StateFlow<RoosterListingUiState> = _uiState.asStateFlow()

    private val _coinBalance = MutableStateFlow(0)
    val coinBalance: StateFlow<Int> = _coinBalance.asStateFlow()

    private val currentUserId: String
        get() = com.parse.ParseUser.getCurrentUser()?.objectId ?: ""

    init {
        loadCoinBalance()
    }

    /**
     * List a rooster with safe coin deduction pattern
     */
    fun listRooster(
        roosterData: RoosterData,
        onComplete: (Boolean, String?) -> Unit,
    ) {
        if (currentUserId.isEmpty()) {
            onComplete(false, "User not logged in")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // Check if user has enough coins before proceeding
                val currentBalance = coinManager.getUserCoinBalance(currentUserId)
                if (currentBalance < CoinCosts.LIST_ROOSTER) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = "Insufficient coins. You need ${CoinCosts.LIST_ROOSTER} coin(s) but have $currentBalance.",
                        )
                    onComplete(false, "Insufficient coins")
                    return@launch
                }

                // 1. First, perform all validations and upload the listing to Parse
                FirebaseCrashlytics.getInstance()
                    .log("RoosterListingViewModel: Creating rooster listing")

                val parseResult = roosterRepository.createRooster(roosterData)
                if (!parseResult.success) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = parseResult.errorMessage ?: "Failed to create listing",
                        )
                    onComplete(false, parseResult.errorMessage)
                    return@launch
                }

                val roosterObjectId = parseResult.objectId ?: ""
                if (roosterObjectId.isEmpty()) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to get listing ID",
                        )
                    onComplete(false, "Failed to get listing ID")
                    return@launch
                }

                FirebaseCrashlytics.getInstance()
                    .log("RoosterListingViewModel: Rooster created with ID: $roosterObjectId")

                // 2. Only after successful backend save, attempt to deduct coins
                val coinSuccess =
                    coinManager.spendCoins(
                        userId = currentUserId,
                        actionLabel = "list_rooster",
                        amount = CoinCosts.LIST_ROOSTER,
                        linkedObjectId = roosterObjectId,
                    )

                if (coinSuccess) {
                    // 3. Mark the rooster record as "coinDeducted = true"
                    roosterRepository.markCoinDeducted(roosterObjectId)

                    FirebaseCrashlytics.getInstance()
                        .log("RoosterListingViewModel: Coin deduction successful")

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            lastListingId = roosterObjectId,
                            success = "Rooster listed successfully! 1 coin deducted.",
                        )

                    // Update coin balance
                    loadCoinBalance()
                    onComplete(true, null)
                } else {
                    // 4. If coin deduction fails, schedule a retry via WorkManager
                    FirebaseCrashlytics.getInstance()
                        .log("RoosterListingViewModel: Coin deduction failed, scheduling retry")

                    WorkManagerHelper.scheduleCoinRetry(
                        context = context,
                        userId = currentUserId,
                        actionLabel = "list_rooster",
                        amount = CoinCosts.LIST_ROOSTER,
                        linkedObjectId = roosterObjectId,
                    )

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            lastListingId = roosterObjectId,
                            success = "Rooster listed successfully! Your coin will be deducted shortly.",
                            pendingCoinDeduction = true,
                        )

                    // The listing succeeded even if coin deduction is pending
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
     * List a premium rooster (costs more coins)
     */
    fun listPremiumRooster(
        roosterData: RoosterData,
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
                if (currentBalance < CoinCosts.PREMIUM_LISTING) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = "Insufficient coins. You need ${CoinCosts.PREMIUM_LISTING} coin(s) but have $currentBalance.",
                        )
                    onComplete(false, "Insufficient coins")
                    return@launch
                }

                // Mark as premium listing
                val premiumRoosterData = roosterData.copy(isPremium = true)

                val parseResult = roosterRepository.createRooster(premiumRoosterData)
                if (!parseResult.success) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = parseResult.errorMessage ?: "Failed to create premium listing",
                        )
                    onComplete(false, parseResult.errorMessage)
                    return@launch
                }

                val roosterObjectId = parseResult.objectId ?: ""

                val coinSuccess =
                    coinManager.spendCoins(
                        userId = currentUserId,
                        actionLabel = "list_premium_rooster",
                        amount = CoinCosts.PREMIUM_LISTING,
                        linkedObjectId = roosterObjectId,
                    )

                if (coinSuccess) {
                    roosterRepository.markCoinDeducted(roosterObjectId)

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            lastListingId = roosterObjectId,
                            success = "Premium rooster listed successfully! ${CoinCosts.PREMIUM_LISTING} coins deducted.",
                        )

                    loadCoinBalance()
                    onComplete(true, null)
                } else {
                    WorkManagerHelper.scheduleCoinRetry(
                        context = context,
                        userId = currentUserId,
                        actionLabel = "list_premium_rooster",
                        amount = CoinCosts.PREMIUM_LISTING,
                        linkedObjectId = roosterObjectId,
                    )

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            lastListingId = roosterObjectId,
                            success = "Premium rooster listed successfully! Your ${CoinCosts.PREMIUM_LISTING} coins will be deducted shortly.",
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
 * UI state for rooster listing
 */
data class RoosterListingUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null,
    val lastListingId: String? = null,
    val pendingCoinDeduction: Boolean = false,
)

/**
 * Data class for rooster information
 */
data class RoosterData(
    val name: String,
    val breed: String,
    val age: String,
    val color: String,
    val price: Double,
    val description: String,
    val imageUrls: List<String> = emptyList(),
    val isPremium: Boolean = false,
    val location: String? = null,
)

/**
 * Result from repository operation
 */
data class RepositoryResult(
    val success: Boolean,
    val objectId: String? = null,
    val errorMessage: String? = null,
)
