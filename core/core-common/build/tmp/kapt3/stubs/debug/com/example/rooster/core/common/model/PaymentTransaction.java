package com.example.rooster.core.common.model;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\'\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0089\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0011\u0012\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u0011\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0014J\t\u0010)\u001a\u00020\u0003H\u00c6\u0003J\t\u0010*\u001a\u00020\u0003H\u00c6\u0003J\t\u0010+\u001a\u00020\u0011H\u00c6\u0003J\u0010\u0010,\u001a\u0004\u0018\u00010\u0011H\u00c6\u0003\u00a2\u0006\u0002\u0010\u001aJ\t\u0010-\u001a\u00020\u0003H\u00c6\u0003J\t\u0010.\u001a\u00020\u0003H\u00c6\u0003J\t\u0010/\u001a\u00020\u0003H\u00c6\u0003J\t\u00100\u001a\u00020\u0003H\u00c6\u0003J\t\u00101\u001a\u00020\u0003H\u00c6\u0003J\t\u00102\u001a\u00020\tH\u00c6\u0003J\t\u00103\u001a\u00020\u000bH\u00c6\u0003J\t\u00104\u001a\u00020\rH\u00c6\u0003J\t\u00105\u001a\u00020\u0003H\u00c6\u0003J\u0092\u0001\u00106\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u00032\b\b\u0002\u0010\u000f\u001a\u00020\u00032\b\b\u0002\u0010\u0010\u001a\u00020\u00112\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u00112\b\b\u0002\u0010\u0013\u001a\u00020\u0003H\u00c6\u0001\u00a2\u0006\u0002\u00107J\u0013\u00108\u001a\u0002092\b\u0010:\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010;\u001a\u00020<H\u00d6\u0001J\t\u0010=\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0015\u0010\u0012\u001a\u0004\u0018\u00010\u0011\u00a2\u0006\n\n\u0002\u0010\u001b\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0010\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\u0013\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0018R\u0011\u0010\u000f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0018R\u0011\u0010\u000e\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u0018R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0018R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0018R\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0018R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0018R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010&R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010(\u00a8\u0006>"}, d2 = {"Lcom/example/rooster/core/common/model/PaymentTransaction;", "", "id", "", "orderId", "auctionId", "payerId", "payeeId", "amount", "", "paymentMethod", "Lcom/example/rooster/core/common/model/PaymentMethod;", "status", "Lcom/example/rooster/core/common/model/PaymentStatus;", "gatewayTransactionId", "gatewayResponse", "createdAt", "", "completedAt", "failureReason", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DLcom/example/rooster/core/common/model/PaymentMethod;Lcom/example/rooster/core/common/model/PaymentStatus;Ljava/lang/String;Ljava/lang/String;JLjava/lang/Long;Ljava/lang/String;)V", "getAmount", "()D", "getAuctionId", "()Ljava/lang/String;", "getCompletedAt", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getCreatedAt", "()J", "getFailureReason", "getGatewayResponse", "getGatewayTransactionId", "getId", "getOrderId", "getPayeeId", "getPayerId", "getPaymentMethod", "()Lcom/example/rooster/core/common/model/PaymentMethod;", "getStatus", "()Lcom/example/rooster/core/common/model/PaymentStatus;", "component1", "component10", "component11", "component12", "component13", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DLcom/example/rooster/core/common/model/PaymentMethod;Lcom/example/rooster/core/common/model/PaymentStatus;Ljava/lang/String;Ljava/lang/String;JLjava/lang/Long;Ljava/lang/String;)Lcom/example/rooster/core/common/model/PaymentTransaction;", "equals", "", "other", "hashCode", "", "toString", "core-common_debug"})
public final class PaymentTransaction {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String orderId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String auctionId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String payerId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String payeeId = null;
    private final double amount = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.PaymentMethod paymentMethod = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.PaymentStatus status = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String gatewayTransactionId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String gatewayResponse = null;
    private final long createdAt = 0L;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long completedAt = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String failureReason = null;
    
    public PaymentTransaction(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String orderId, @org.jetbrains.annotations.NotNull()
    java.lang.String auctionId, @org.jetbrains.annotations.NotNull()
    java.lang.String payerId, @org.jetbrains.annotations.NotNull()
    java.lang.String payeeId, double amount, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.PaymentMethod paymentMethod, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.PaymentStatus status, @org.jetbrains.annotations.NotNull()
    java.lang.String gatewayTransactionId, @org.jetbrains.annotations.NotNull()
    java.lang.String gatewayResponse, long createdAt, @org.jetbrains.annotations.Nullable()
    java.lang.Long completedAt, @org.jetbrains.annotations.NotNull()
    java.lang.String failureReason) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOrderId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAuctionId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPayerId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPayeeId() {
        return null;
    }
    
    public final double getAmount() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.PaymentMethod getPaymentMethod() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.PaymentStatus getStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getGatewayTransactionId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getGatewayResponse() {
        return null;
    }
    
    public final long getCreatedAt() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getCompletedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFailureReason() {
        return null;
    }
    
    public PaymentTransaction() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component10() {
        return null;
    }
    
    public final long component11() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component12() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component13() {
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
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    public final double component6() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.PaymentMethod component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.PaymentStatus component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.PaymentTransaction copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String orderId, @org.jetbrains.annotations.NotNull()
    java.lang.String auctionId, @org.jetbrains.annotations.NotNull()
    java.lang.String payerId, @org.jetbrains.annotations.NotNull()
    java.lang.String payeeId, double amount, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.PaymentMethod paymentMethod, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.PaymentStatus status, @org.jetbrains.annotations.NotNull()
    java.lang.String gatewayTransactionId, @org.jetbrains.annotations.NotNull()
    java.lang.String gatewayResponse, long createdAt, @org.jetbrains.annotations.Nullable()
    java.lang.Long completedAt, @org.jetbrains.annotations.NotNull()
    java.lang.String failureReason) {
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