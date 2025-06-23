package com.example.rooster.data.repositories

import com.example.rooster.data.entities.CoinTransaction
import com.example.rooster.data.entities.CoinTransactionType
import com.example.rooster.data.entities.Payment
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Payment repository implementation
 */
@Singleton
class PaymentRepository @Inject constructor() {

    // Mock data storage
    private val payments = mutableListOf<Payment>()
    private val coinTransactions = mutableListOf<CoinTransaction>()

    suspend fun completePayment(payment: Payment): Payment {
        val index = payments.indexOfFirst { it.id == payment.id }
        return if (index >= 0) {
            payments[index] = payment.copy(completedAt = System.currentTimeMillis())
            payments[index]
        } else {
            payments.add(payment)
            payment
        }
    }

    suspend fun createCoinTransaction(
        userId: String,
        amount: Int,
        type: CoinTransactionType,
        description: String
    ): CoinTransaction {
        val transaction = CoinTransaction(
            userId = userId,
            amount = amount,
            type = type,
            description = description
        )
        coinTransactions.add(transaction)
        return transaction
    }

    fun getPaymentHistory(userId: String): List<Payment> {
        return payments.filter { it.userId == userId }
    }

    fun getCoinTransactions(userId: String): List<CoinTransaction> {
        return coinTransactions.filter { it.userId == userId }
    }
}