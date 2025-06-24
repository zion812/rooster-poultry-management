package com.example.rooster.data.entities

/**
 * Payment entity
 */
data class Payment(
    val id: String = "payment_${System.currentTimeMillis()}",
    val userId: String,
    val orderId: String,
    val amount: Double,
    val coinsUsed: Int = 0,
    val paymentMethod: PaymentMethod,
    val status: PaymentStatus,
    val transactionId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
)

/**
 * Payment method enum
 */
enum class PaymentMethod {
    COINS,
    UPI,
    CARD,
    NET_BANKING,
    WALLET,
    COD,
}

/**
 * Payment status enum
 */
enum class PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED,
    REFUNDED,
}

/**
 * Coin transaction entity
 */
data class CoinTransaction(
    val id: String = "coin_${System.currentTimeMillis()}",
    val userId: String,
    val amount: Int,
    val type: CoinTransactionType,
    val description: String,
    val createdAt: Long = System.currentTimeMillis(),
)

/**
 * Coin transaction type enum
 */
enum class CoinTransactionType {
    PURCHASE,
    REWARD,
    SPEND,
    REFUND,
}
