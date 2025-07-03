package com.example.rooster.core.common.models.payment;

import kotlinx.serialization.SerialName;
import kotlinx.serialization.Serializable;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0007J\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0014\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J3\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001J\t\u0010\u001b\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u001c\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\n\u0010\u000b\u001a\u0004\b\f\u0010\tR\u001c\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\r\u0010\u000b\u001a\u0004\b\u000e\u0010\tR\u001c\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\u000f\u0010\u000b\u001a\u0004\b\u0010\u0010\t\u00a8\u0006\u001c"}, d2 = {"Lcom/example/rooster/core/common/models/payment/VerifyPaymentRequest;", "", "razorpayOrderId", "", "razorpayPaymentId", "razorpaySignature", "auctionId", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getAuctionId", "()Ljava/lang/String;", "getRazorpayOrderId$annotations", "()V", "getRazorpayOrderId", "getRazorpayPaymentId$annotations", "getRazorpayPaymentId", "getRazorpaySignature$annotations", "getRazorpaySignature", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "core-common_debug"})
public final class VerifyPaymentRequest {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String razorpayOrderId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String razorpayPaymentId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String razorpaySignature = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String auctionId = null;
    
    public VerifyPaymentRequest(@org.jetbrains.annotations.NotNull()
    java.lang.String razorpayOrderId, @org.jetbrains.annotations.NotNull()
    java.lang.String razorpayPaymentId, @org.jetbrains.annotations.NotNull()
    java.lang.String razorpaySignature, @org.jetbrains.annotations.Nullable()
    java.lang.String auctionId) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRazorpayOrderId() {
        return null;
    }
    
    @kotlinx.serialization.SerialName(value = "razorpay_order_id")
    @java.lang.Deprecated()
    public static void getRazorpayOrderId$annotations() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRazorpayPaymentId() {
        return null;
    }
    
    @kotlinx.serialization.SerialName(value = "razorpay_payment_id")
    @java.lang.Deprecated()
    public static void getRazorpayPaymentId$annotations() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRazorpaySignature() {
        return null;
    }
    
    @kotlinx.serialization.SerialName(value = "razorpay_signature")
    @java.lang.Deprecated()
    public static void getRazorpaySignature$annotations() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getAuctionId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.models.payment.VerifyPaymentRequest copy(@org.jetbrains.annotations.NotNull()
    java.lang.String razorpayOrderId, @org.jetbrains.annotations.NotNull()
    java.lang.String razorpayPaymentId, @org.jetbrains.annotations.NotNull()
    java.lang.String razorpaySignature, @org.jetbrains.annotations.Nullable()
    java.lang.String auctionId) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}