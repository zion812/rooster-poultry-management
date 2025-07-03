package com.example.rooster.core.common.event;

/**
 * Sealed class representing payment events, specifically for Razorpay results.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0002\u0003\u0004B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0002\u0005\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/example/rooster/core/common/event/PaymentEvent;", "", "()V", "Failure", "Success", "Lcom/example/rooster/core/common/event/PaymentEvent$Failure;", "Lcom/example/rooster/core/common/event/PaymentEvent$Success;", "core-common_debug"})
public abstract class PaymentEvent {
    
    private PaymentEvent() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u000e\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010\u000f\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J+\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0016\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000b\u00a8\u0006\u0017"}, d2 = {"Lcom/example/rooster/core/common/event/PaymentEvent$Failure;", "Lcom/example/rooster/core/common/event/PaymentEvent;", "code", "", "description", "", "orderId", "(ILjava/lang/String;Ljava/lang/String;)V", "getCode", "()I", "getDescription", "()Ljava/lang/String;", "getOrderId", "component1", "component2", "component3", "copy", "equals", "", "other", "", "hashCode", "toString", "core-common_debug"})
    public static final class Failure extends com.example.rooster.core.common.event.PaymentEvent {
        private final int code = 0;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String description = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String orderId = null;
        
        public Failure(int code, @org.jetbrains.annotations.Nullable()
        java.lang.String description, @org.jetbrains.annotations.Nullable()
        java.lang.String orderId) {
        }
        
        public final int getCode() {
            return 0;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getDescription() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getOrderId() {
            return null;
        }
        
        public final int component1() {
            return 0;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.rooster.core.common.event.PaymentEvent.Failure copy(int code, @org.jetbrains.annotations.Nullable()
        java.lang.String description, @org.jetbrains.annotations.Nullable()
        java.lang.String orderId) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J+\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u00d6\u0003J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001J\t\u0010\u0015\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b\u00a8\u0006\u0016"}, d2 = {"Lcom/example/rooster/core/common/event/PaymentEvent$Success;", "Lcom/example/rooster/core/common/event/PaymentEvent;", "paymentId", "", "orderId", "signature", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getOrderId", "()Ljava/lang/String;", "getPaymentId", "getSignature", "component1", "component2", "component3", "copy", "equals", "", "other", "", "hashCode", "", "toString", "core-common_debug"})
    public static final class Success extends com.example.rooster.core.common.event.PaymentEvent {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String paymentId = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String orderId = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String signature = null;
        
        public Success(@org.jetbrains.annotations.NotNull()
        java.lang.String paymentId, @org.jetbrains.annotations.Nullable()
        java.lang.String orderId, @org.jetbrains.annotations.Nullable()
        java.lang.String signature) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getPaymentId() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getOrderId() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getSignature() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.rooster.core.common.event.PaymentEvent.Success copy(@org.jetbrains.annotations.NotNull()
        java.lang.String paymentId, @org.jetbrains.annotations.Nullable()
        java.lang.String orderId, @org.jetbrains.annotations.Nullable()
        java.lang.String signature) {
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
}