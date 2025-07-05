package com.example.rooster

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * CoinManager handles all coin-related operations with safe spending patterns.
 * Ensures coins are only deducted after successful backend operations.
 */
class CoinManager {
    companion object {
        private const val WALLET_CLASS = "Wallet"
        private const val TRANSACTION_CLASS = "CoinTransaction"
        private const val FIELD_USER_ID = "userId"
        private const val FIELD_COINS = "coins"
        private const val FIELD_ACTION_LABEL = "actionLabel"
        private const val FIELD_AMOUNT = "amount"
        private const val FIELD_LINKED_OBJECT_ID = "linkedObjectId"
        private const val FIELD_TIMESTAMP = "timestamp"
        private const val FIELD_STATUS = "status"

        // Transaction statuses
        private const val STATUS_SUCCESS = "SUCCESS"
        private const val STATUS_FAILED = "FAILED"
        private const val STATUS_PENDING = "PENDING"
    }

    /**
     * Safely spend coins for a user action.
     * Only deducts coins after validating the action was successful.
     *
     * @param userId The Parse User objectId
     * @param actionLabel Descriptive label for the action (e.g., "list_rooster", "verify_bird")
     * @param amount Number of coins to spend
     * @param linkedObjectId Optional objectId of the entity that triggered this spend
     * @return true if coin deduction was successful, false otherwise
     */
    suspend fun spendCoins(
        userId: String,
        actionLabel: String,
        amount: Int,
        linkedObjectId: String? = null,
    ): Boolean =
        withContext(Dispatchers.IO) {
            try {
                // Log the attempt
                FirebaseCrashlytics.getInstance()
                    .log("CoinManager: Attempting to spend $amount coins for $actionLabel")

                // 1. Get or create user's wallet
                val wallet = getUserWallet(userId) ?: return@withContext false
                val currentCoins = wallet.getInt(FIELD_COINS)

                // 2. Check if user has sufficient coins
                if (currentCoins < amount) {
                    FirebaseCrashlytics.getInstance()
                        .log("CoinManager: Insufficient coins. User has $currentCoins, needs $amount")
                    return@withContext false
                }

                // 3. Create transaction record (initially pending)
                val transactionId =
                    createCoinTransaction(
                        userId = userId,
                        actionLabel = actionLabel,
                        amount = amount,
                        linkedObjectId = linkedObjectId,
                        status = STATUS_PENDING,
                    )

                if (transactionId == null) {
                    FirebaseCrashlytics.getInstance()
                        .log("CoinManager: Failed to create transaction record")
                    return@withContext false
                }

                // 4. Deduct coins from wallet
                wallet.increment(FIELD_COINS, -amount)

                var walletSaveSuccess = false
                try {
                    wallet.save()
                    walletSaveSuccess = true
                } catch (e: ParseException) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    FirebaseCrashlytics.getInstance()
                        .log("CoinManager: Failed to save wallet: ${e.message}")
                }

                // 5. Update transaction status based on wallet save result
                val finalStatus = if (walletSaveSuccess) STATUS_SUCCESS else STATUS_FAILED
                updateTransactionStatus(transactionId, finalStatus)

                if (walletSaveSuccess) {
                    FirebaseCrashlytics.getInstance()
                        .log("CoinManager: Successfully spent $amount coins for $actionLabel")
                    return@withContext true
                } else {
                    FirebaseCrashlytics.getInstance()
                        .log("CoinManager: Failed to spend coins - wallet save failed")
                    return@withContext false
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                FirebaseCrashlytics.getInstance()
                    .log("CoinManager: Exception in spendCoins: ${e.message}")
                return@withContext false
            }
        }

    /**
     * Get user's current coin balance
     */
    suspend fun getUserCoinBalance(userId: String): Int =
        withContext(Dispatchers.IO) {
            try {
                val wallet = getUserWallet(userId)
                return@withContext wallet?.getInt(FIELD_COINS) ?: 0
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                return@withContext 0
            }
        }

    /**
     * Add coins to user's wallet (for bonuses, purchases, etc.)
     */
    suspend fun addCoins(
        userId: String,
        amount: Int,
        actionLabel: String,
        linkedObjectId: String? = null,
    ): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val wallet = getUserWallet(userId) ?: return@withContext false

                // Create positive transaction record
                val transactionId =
                    createCoinTransaction(
                        userId = userId,
                        actionLabel = actionLabel,
                        amount = amount, // Positive amount for adding coins
                        linkedObjectId = linkedObjectId,
                        status = STATUS_PENDING,
                    )

                if (transactionId == null) {
                    return@withContext false
                }

                // Add coins to wallet
                wallet.increment(FIELD_COINS, amount)

                var success = false
                try {
                    wallet.save()
                    success = true
                } catch (e: ParseException) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }

                // Update transaction status
                updateTransactionStatus(transactionId, if (success) STATUS_SUCCESS else STATUS_FAILED)

                if (success) {
                    FirebaseCrashlytics.getInstance()
                        .log("CoinManager: Successfully added $amount coins for $actionLabel")
                }

                return@withContext success
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                return@withContext false
            }
        }

    /**
     * Get or create user's wallet
     */
    private suspend fun getUserWallet(userId: String): ParseObject? =
        withContext(Dispatchers.IO) {
            try {
                // Try to find existing wallet
                val query = ParseQuery.getQuery<ParseObject>(WALLET_CLASS)
                query.whereEqualTo(FIELD_USER_ID, userId)

                val existingWallet =
                    try {
                        query.find().firstOrNull()
                    } catch (e: ParseException) {
                        if (e.code == ParseException.OBJECT_NOT_FOUND) {
                            null
                        } else {
                            throw e
                        }
                    }

                if (existingWallet != null) {
                    return@withContext existingWallet
                }

                // Create new wallet with default coins (e.g., 10 starting coins)
                val newWallet = ParseObject(WALLET_CLASS)
                newWallet.put(FIELD_USER_ID, userId)
                newWallet.put(FIELD_COINS, 10) // Starting coins

                try {
                    newWallet.save()
                    FirebaseCrashlytics.getInstance()
                        .log("CoinManager: Created new wallet for user $userId with 10 starting coins")
                    return@withContext newWallet
                } catch (e: ParseException) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    return@withContext null
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                return@withContext null
            }
        }

    /**
     * Create a coin transaction record
     */
    private suspend fun createCoinTransaction(
        userId: String,
        actionLabel: String,
        amount: Int,
        linkedObjectId: String?,
        status: String,
    ): String? =
        withContext(Dispatchers.IO) {
            try {
                val transaction = ParseObject(TRANSACTION_CLASS)
                transaction.put(FIELD_USER_ID, userId)
                transaction.put(FIELD_ACTION_LABEL, actionLabel)
                transaction.put(FIELD_AMOUNT, amount)
                transaction.put(FIELD_STATUS, status)
                transaction.put(FIELD_TIMESTAMP, Date())

                linkedObjectId?.let { transaction.put(FIELD_LINKED_OBJECT_ID, it) }

                transaction.save()
                return@withContext transaction.objectId
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                return@withContext null
            }
        }

    /**
     * Update transaction status
     */
    private suspend fun updateTransactionStatus(
        transactionId: String,
        status: String,
    ) = withContext(Dispatchers.IO) {
        try {
            val query = ParseQuery.getQuery<ParseObject>(TRANSACTION_CLASS)
            val transaction = query.get(transactionId)
            transaction.put(FIELD_STATUS, status)
            transaction.save()
        } catch (e: ParseException) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    /**
     * Get user's transaction history
     */
    suspend fun getUserTransactions(
        userId: String,
        limit: Int = 50,
    ): List<CoinTransaction> =
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>(TRANSACTION_CLASS)
                query.whereEqualTo(FIELD_USER_ID, userId)
                query.orderByDescending("createdAt")
                query.limit = limit

                val results = query.find()
                return@withContext results.map { parseObject ->
                    CoinTransaction(
                        id = parseObject.objectId,
                        userId = parseObject.getString(FIELD_USER_ID) ?: "",
                        actionLabel = parseObject.getString(FIELD_ACTION_LABEL) ?: "",
                        amount = parseObject.getInt(FIELD_AMOUNT),
                        linkedObjectId = parseObject.getString(FIELD_LINKED_OBJECT_ID),
                        status = parseObject.getString(FIELD_STATUS) ?: STATUS_PENDING,
                        timestamp = parseObject.getDate(FIELD_TIMESTAMP) ?: Date(),
                    )
                }
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                return@withContext emptyList()
            }
        }

    /**
     * Check if a specific action has already been charged for
     * (to prevent double-charging during retries)
     */
    suspend fun hasBeenCharged(
        userId: String,
        actionLabel: String,
        linkedObjectId: String,
    ): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>(TRANSACTION_CLASS)
                query.whereEqualTo(FIELD_USER_ID, userId)
                query.whereEqualTo(FIELD_ACTION_LABEL, actionLabel)
                query.whereEqualTo(FIELD_LINKED_OBJECT_ID, linkedObjectId)
                query.whereEqualTo(FIELD_STATUS, STATUS_SUCCESS)

                return@withContext query.count() > 0
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                return@withContext false
            }
        }
}

/**
 * Data class representing a coin transaction
 */
data class CoinTransaction(
    val id: String,
    val userId: String,
    val actionLabel: String,
    val amount: Int,
    val linkedObjectId: String?,
    val status: String,
    val timestamp: Date,
)

/**
 * Constants for coin costs across the app
 */
object CoinCosts {
    const val LIST_ROOSTER = 1
    const val LIST_CHICK = 1
    const val LIST_EGG = 1
    const val VERIFY_15_WEEK = 1
    const val VERIFY_40_WEEK = 2
    const val TRANSFER_OWNERSHIP = 1
    const val ANNUAL_MAINTENANCE = 5
    const val PREMIUM_LISTING = 3
    const val FEATURED_LISTING = 5
}
