package com.example.rooster.core.common.domain.repository;

/**
 * Interface for handling payment operations, e.g., with Razorpay via a backend.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u001c\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\u00a6@\u00a2\u0006\u0002\u0010\u0007J\u001c\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u00032\u0006\u0010\n\u001a\u00020\u000bH\u00a6@\u00a2\u0006\u0002\u0010\f\u00a8\u0006\r"}, d2 = {"Lcom/example/rooster/core/common/domain/repository/PaymentRepository;", "", "createRazorpayOrder", "Lcom/example/rooster/core/common/Result;", "Lcom/example/rooster/core/common/models/payment/RazorpayOrderResponse;", "orderRequest", "Lcom/example/rooster/core/common/models/payment/CreateOrderRequest;", "(Lcom/example/rooster/core/common/models/payment/CreateOrderRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "verifyRazorpayPayment", "Lcom/example/rooster/core/common/models/payment/VerifyPaymentResponse;", "verifyRequest", "Lcom/example/rooster/core/common/models/payment/VerifyPaymentRequest;", "(Lcom/example/rooster/core/common/models/payment/VerifyPaymentRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "core-common_debug"})
public abstract interface PaymentRepository {
    
    /**
     * Creates a payment order via the backend.
     * @param orderRequest Details for creating the order.
     * @return A Result containing the RazorpayOrderResponse from the backend.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object createRazorpayOrder(@org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.models.payment.CreateOrderRequest orderRequest, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.rooster.core.common.Result<com.example.rooster.core.common.models.payment.RazorpayOrderResponse>> $completion);
    
    /**
     * Verifies a payment with the backend after client-side Razorpay completion.
     * @param verifyRequest Details needed for backend verification.
     * @return A Result containing the VerifyPaymentResponse from the backend.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object verifyRazorpayPayment(@org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.models.payment.VerifyPaymentRequest verifyRequest, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.rooster.core.common.Result<com.example.rooster.core.common.models.payment.VerifyPaymentResponse>> $completion);
}