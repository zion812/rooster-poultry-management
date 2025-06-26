package com.example.rooster.core.common.event

/**
 * Sealed class representing payment events, specifically for Razorpay results.
 */
sealed class PaymentEvent {
    data class Success(val paymentId: String, val orderId: String? = null, val signature: String? = null) : PaymentEvent()
    data class Failure(val code: Int, val description: String?, val orderId: String? = null) : PaymentEvent()
    // Could add other events like CancelledByUser if Razorpay distinguishes that explicitly
}
