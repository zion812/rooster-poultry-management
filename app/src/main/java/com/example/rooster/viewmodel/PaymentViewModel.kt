package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.data.entities.CoinTransaction
import com.example.rooster.data.entities.CoinTransactionType
import com.example.rooster.data.entities.Payment
import com.example.rooster.data.entities.PaymentMethod
import com.example.rooster.data.entities.PaymentStatus
import com.example.rooster.data.repositories.PaymentRepository
import com.example.rooster.domain.repository.UserRepository
import com.example.rooster.util.ShoppingCartManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel
    @Inject
    constructor(
        private val paymentRepository: PaymentRepository,
        private val userRepository: UserRepository,
        private val shoppingCartManager: ShoppingCartManager,
    ) : ViewModel() {
        private val _isProcessing = MutableStateFlow(false)
        val isProcessing: StateFlow<Boolean> = _isProcessing

        private val _paymentStatus = MutableStateFlow<PaymentStatus?>(null)
        val paymentStatus: StateFlow<PaymentStatus?> = _paymentStatus

        private val _totalAmount = MutableStateFlow(0.0)
        val totalAmount: StateFlow<Double> = _totalAmount

        private val _availableCoins = MutableStateFlow(0)
        val availableCoins: StateFlow<Int> = _availableCoins

        init {
            loadAvailableCoins()
        }

    private suspend fun getCurrentUserId(): String? {
            return userRepository.getCurrentUser()
        }

    private suspend fun getCurrentUserProfile(): UserProfile? {
        return try {
            val userId = getCurrentUserId()
            if (userId != null) {
                UserProfile(
                    userId = userId,
                    coins = getUserCoins(userId)
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    data class UserProfile(
        val userId: String,
        val coins: Int = 0
    )

    private fun getUserCoins(userId: String): Int {
            // Mock implementation - replace with actual repository call
            return 100
        }

        private suspend fun updateUserCoins(userId: String, coins: Int): Boolean {
            // Mock implementation - replace with actual repository call
            return try {
                // userRepository.updateUserCoins(userId, coins)
                true
            } catch (e: Exception) {
                false
            }
        }

        fun processPayment(
            orderId: String,
            paymentMethod: PaymentMethod,
            coinsUsed: Int = 0,
        ) {
            viewModelScope.launch {
                _isProcessing.value = true
                try {
                    val currentUser = getCurrentUserProfile()
                    val cartItems = shoppingCartManager.cart.value
                    val totalAmount = shoppingCartManager.totalPrice.value

                    // Create payment record
                    val payment =
                        Payment(
                            userId = currentUser?.userId ?: "",
                            orderId = orderId,
                            amount = totalAmount,
                            coinsUsed = coinsUsed,
                            paymentMethod = paymentMethod,
                            status = PaymentStatus.PENDING,
                        )

                    // Process payment based on method
                    when (paymentMethod) {
                        PaymentMethod.COINS -> processCoinPayment(payment)
                        PaymentMethod.UPI -> processOnlinePayment(payment)
                        PaymentMethod.CARD -> processOnlinePayment(payment)
                        PaymentMethod.NET_BANKING -> processOnlinePayment(payment)
                        PaymentMethod.WALLET -> processOnlinePayment(payment)
                        PaymentMethod.COD -> processCodPayment(payment)
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    _paymentStatus.value = PaymentStatus.FAILED
                } finally {
                    _isProcessing.value = false
                }
            }
        }

        private suspend fun processCoinPayment(payment: Payment) {
            val currentUser = getCurrentUserProfile()
            val currentCoins = currentUser?.coins ?: 0
            val coinsRequired = (payment.amount / 5).toInt() // 5 coins per ₹1

            if (currentCoins >= coinsRequired) {
                // Deduct coins
                val transaction =
                    paymentRepository.createCoinTransaction(
                        userId = currentUser?.userId ?: "",
                        amount = -coinsRequired,
                        type = CoinTransactionType.SPEND,
                        description = "Payment for order ${payment.orderId}",
                    )

                // Update user coins
                val success =
                    updateUserCoins(currentUser?.userId ?: "", currentCoins - coinsRequired)

                if (success) {
                    paymentRepository.completePayment(
                        payment.copy(
                            status = PaymentStatus.COMPLETED,
                            transactionId = transaction.id,
                        ),
                    )
                    _paymentStatus.value = PaymentStatus.COMPLETED
                } else {
                    _paymentStatus.value = PaymentStatus.FAILED
                }
            } else {
                _paymentStatus.value = PaymentStatus.FAILED
            }
        }

        private suspend fun processOnlinePayment(payment: Payment) {
            // TODO: Implement actual payment gateway integration
            // For now, simulate payment processing
            val success = true // Simulate payment success
            if (success) {
                paymentRepository.completePayment(
                    payment.copy(
                        status = PaymentStatus.COMPLETED,
                        transactionId = "TXN_${payment.id}",
                    ),
                )
                _paymentStatus.value = PaymentStatus.COMPLETED
            } else {
                _paymentStatus.value = PaymentStatus.FAILED
            }
        }

        private suspend fun processCodPayment(payment: Payment) {
            // For COD, payment is considered completed without actual processing
            paymentRepository.completePayment(
                payment.copy(
                    status = PaymentStatus.COMPLETED,
                ),
            )
            _paymentStatus.value = PaymentStatus.COMPLETED
        }

        fun loadAvailableCoins() {
            viewModelScope.launch {
                try {
                    val currentUser = getCurrentUserProfile()
                    _availableCoins.value = currentUser?.coins ?: 0
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }

        fun purchaseCoins(amount: Int) {
            viewModelScope.launch {
                _isProcessing.value = true
                try {
                    val currentUser = getCurrentUserProfile()
                    val coins = (amount / 5).toInt() // 5 coins per ₹1

                    // Create coin transaction
                    val transaction =
                        paymentRepository.createCoinTransaction(
                            userId = currentUser?.userId ?: "",
                            amount = coins,
                            type = CoinTransactionType.PURCHASE,
                            description = "Coins purchased",
                        )

                    // Update user coins
                    val success = updateUserCoins(
                        currentUser?.userId ?: "",
                        (currentUser?.coins ?: 0) + coins
                    )

                    if (success) {
                        _availableCoins.value = (currentUser?.coins ?: 0) + coins
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                } finally {
                    _isProcessing.value = false
                }
            }
        }

        suspend fun getPaymentHistory(): List<Payment> {
            val currentUser = getCurrentUserProfile()
            return paymentRepository.getPaymentHistory(currentUser?.userId ?: "")
        }

        suspend fun getCoinTransactions(): List<CoinTransaction> {
            val currentUser = getCurrentUserProfile()
            return paymentRepository.getCoinTransactions(currentUser?.userId ?: "")
        }
    }
