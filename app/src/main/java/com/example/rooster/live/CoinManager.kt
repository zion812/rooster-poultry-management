package com.example.rooster.live

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Coin management system for in-app purchases and rewards
 * Handles coin balance, transactions, and monetization features
 */
object CoinManager {
    // Current user's coin balance
    private val _coinBalance = MutableStateFlow(0)
    val coinBalance: StateFlow<Int> = _coinBalance.asStateFlow()

    // Coin to currency conversion rate (1 coin = ₹5)
    private const val COIN_TO_RUPEE_RATE = 5

    /**
     * Initialize coin balance for current user
     */
    suspend fun initializeCoinBalance() {
        try {
            val currentUser = ParseUser.getCurrentUser()
            if (currentUser != null) {
                val coins = currentUser.getInt("coins")
                _coinBalance.value = coins
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            _coinBalance.value = 0
        }
    }

    /**
     * Check if user has enough coins for a transaction
     */
    fun hasCoins(amount: Int): Boolean {
        return _coinBalance.value >= amount
    }

    /**
     * Deduct coins from user balance
     * Returns true if successful, false if insufficient balance
     */
    suspend fun deductCoins(
        amount: Int,
        reason: String = "Purchase",
    ): Boolean {
        return try {
            if (!hasCoins(amount)) {
                return false
            }

            val currentUser = ParseUser.getCurrentUser() ?: return false
            val newBalance = _coinBalance.value - amount

            // Update Parse backend
            currentUser.put("coins", newBalance)
            currentUser.save()

            // Update local state
            _coinBalance.value = newBalance

            // Log transaction
            logTransaction(
                type = TransactionType.DEBIT,
                amount = amount,
                reason = reason,
                balanceAfter = newBalance,
            )

            true
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            false
        }
    }

    /**
     * Add coins to user balance (rewards, purchases)
     */
    suspend fun addCoins(
        amount: Int,
        reason: String = "Reward",
    ): Boolean {
        return try {
            val currentUser = ParseUser.getCurrentUser() ?: return false
            val newBalance = _coinBalance.value + amount

            // Update Parse backend
            currentUser.put("coins", newBalance)
            currentUser.save()

            // Update local state
            _coinBalance.value = newBalance

            // Log transaction
            logTransaction(
                type = TransactionType.CREDIT,
                amount = amount,
                reason = reason,
                balanceAfter = newBalance,
            )

            true
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            false
        }
    }

    /**
     * Get coin balance in rupees
     */
    fun getBalanceInRupees(): Int {
        return _coinBalance.value * COIN_TO_RUPEE_RATE
    }

    /**
     * Convert rupees to coins
     */
    fun rupeesToCoins(rupees: Int): Int {
        return rupees / COIN_TO_RUPEE_RATE
    }

    /**
     * Convert coins to rupees
     */
    fun coinsToRupees(coins: Int): Int {
        return coins * COIN_TO_RUPEE_RATE
    }

    /**
     * Log transaction for audit trail
     */
    private suspend fun logTransaction(
        type: TransactionType,
        amount: Int,
        reason: String,
        balanceAfter: Int,
    ) {
        try {
            val transaction =
                ParseObject("CoinTransaction").apply {
                    put("userId", ParseUser.getCurrentUser()?.objectId ?: "unknown")
                    put("type", type.name)
                    put("amount", amount)
                    put("reason", reason)
                    put("balanceAfter", balanceAfter)
                    put("timestamp", System.currentTimeMillis())
                }
            transaction.save()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    /**
     * Get transaction history for current user
     */
    suspend fun getTransactionHistory(limit: Int = 50): List<CoinTransaction> {
        return try {
            val currentUser = ParseUser.getCurrentUser() ?: return emptyList()

            val query = ParseQuery.getQuery<ParseObject>("CoinTransaction")
            query.whereEqualTo("userId", currentUser.objectId)
            query.orderByDescending("timestamp")
            query.limit = limit

            val results = query.find()
            results.map { parseObject ->
                CoinTransaction(
                    id = parseObject.objectId,
                    type = TransactionType.valueOf(parseObject.getString("type") ?: "DEBIT"),
                    amount = parseObject.getInt("amount"),
                    reason = parseObject.getString("reason") ?: "Unknown",
                    balanceAfter = parseObject.getInt("balanceAfter"),
                    timestamp = parseObject.getLong("timestamp"),
                )
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            emptyList()
        }
    }
}

/**
 * Transaction type enumeration
 */
enum class TransactionType {
    CREDIT, // Adding coins
    DEBIT, // Spending coins
}

/**
 * Coin transaction data class
 */
data class CoinTransaction(
    val id: String,
    val type: TransactionType,
    val amount: Int,
    val reason: String,
    val balanceAfter: Int,
    val timestamp: Long,
)

/**
 * Coin pricing tiers for purchases
 */
object CoinPricing {
    val STARTER_PACK =
        CoinPackage("Starter Pack", 20, 100, "Best for beginners") // 20 coins for ₹100
    val VALUE_PACK =
        CoinPackage("Value Pack", 50, 200, "Most popular") // 50 coins for ₹200 (25% bonus)
    val PREMIUM_PACK =
        CoinPackage("Premium Pack", 120, 400, "Best value") // 120 coins for ₹400 (50% bonus)
    val MEGA_PACK =
        CoinPackage("Mega Pack", 250, 750, "For power users") // 250 coins for ₹750 (67% bonus)

    val ALL_PACKAGES = listOf(STARTER_PACK, VALUE_PACK, PREMIUM_PACK, MEGA_PACK)
}

/**
 * Coin package data class
 */
data class CoinPackage(
    val name: String,
    val coins: Int,
    val priceInRupees: Int,
    val description: String,
) {
    val bonusPercentage: Int
        get() {
            val baseValue = coins * 5 // Standard rate: 1 coin = ₹5
            return if (baseValue > priceInRupees) {
                ((baseValue - priceInRupees) * 100 / priceInRupees)
            } else {
                0
            }
        }
}
