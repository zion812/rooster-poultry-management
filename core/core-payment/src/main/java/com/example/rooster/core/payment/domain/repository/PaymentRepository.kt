package com.example.rooster.core.payment.domain.repository // Updated package

import com.example.rooster.core.common.Result
import com.example.rooster.core.common.models.payment.CreateOrderRequest
import com.example.rooster.core.common.models.payment.RazorpayOrderResponse
import com.example.rooster.core.common.models.payment.VerifyPaymentRequest
import com.example.rooster.core.common.models.payment.VerifyPaymentResponse

/**
 * Interface for handling payment operations, e.g., with Razorpay via a backend.
 */
interface PaymentRepository {

    /**
     * Creates a payment order via the backend.
     * @param orderRequest Details for creating the order.
     * @return A Result containing the RazorpayOrderResponse from the backend.
     */
    suspend fun createRazorpayOrder(orderRequest: CreateOrderRequest): Result<RazorpayOrderResponse>

    /**
     * Verifies a payment with the backend after client-side Razorpay completion.
     * @param verifyRequest Details needed for backend verification.
     * @return A Result containing the VerifyPaymentResponse from the backend.
     */
    suspend fun verifyRazorpayPayment(verifyRequest: VerifyPaymentRequest): Result<VerifyPaymentResponse>

    // Potentially add other payment related methods here, e.g.,
    // getTransactionHistory, processRefund, etc.
}
