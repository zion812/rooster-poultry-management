package com.example.rooster.core.common.model;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b&\b\u0087\b\u0018\u00002\u00020\u0001Bw\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u000b\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0011\u0012\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\u0002\u0010\u0013J\t\u0010&\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\'\u001a\u00020\u0011H\u00c6\u0003J\u0010\u0010(\u001a\u0004\u0018\u00010\u000bH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0017J\u000b\u0010)\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010*\u001a\u00020\u0003H\u00c6\u0003J\t\u0010+\u001a\u00020\u0007H\u00c6\u0003J\t\u0010,\u001a\u00020\tH\u00c6\u0003J\t\u0010-\u001a\u00020\u000bH\u00c6\u0003J\t\u0010.\u001a\u00020\u000bH\u00c6\u0003J\t\u0010/\u001a\u00020\u000eH\u00c6\u0003J\t\u00100\u001a\u00020\u0003H\u00c6\u0003J\u0080\u0001\u00101\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00032\b\b\u0002\u0010\u0010\u001a\u00020\u00112\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u000bH\u00c6\u0001\u00a2\u0006\u0002\u00102J\u0013\u00103\u001a\u00020\u000e2\b\u00104\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00105\u001a\u00020\tH\u00d6\u0001J\t\u00106\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0015\u0010\u0012\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\n\n\u0002\u0010\u0018\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0010\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u001dR\u0011\u0010\f\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001cR\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010\u000f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0015R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0015R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0015R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010%\u00a8\u00067"}, d2 = {"Lcom/example/rooster/core/common/model/AuctionResult;", "", "auctionId", "", "winnerId", "winnerName", "winningBid", "", "totalBids", "", "endTime", "", "paymentDeadline", "isPaid", "", "transactionId", "deliveryStatus", "Lcom/example/rooster/core/common/model/DeliveryStatus;", "completionDate", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DIJJZLjava/lang/String;Lcom/example/rooster/core/common/model/DeliveryStatus;Ljava/lang/Long;)V", "getAuctionId", "()Ljava/lang/String;", "getCompletionDate", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getDeliveryStatus", "()Lcom/example/rooster/core/common/model/DeliveryStatus;", "getEndTime", "()J", "()Z", "getPaymentDeadline", "getTotalBids", "()I", "getTransactionId", "getWinnerId", "getWinnerName", "getWinningBid", "()D", "component1", "component10", "component11", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DIJJZLjava/lang/String;Lcom/example/rooster/core/common/model/DeliveryStatus;Ljava/lang/Long;)Lcom/example/rooster/core/common/model/AuctionResult;", "equals", "other", "hashCode", "toString", "core-common_debug"})
public final class AuctionResult {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String auctionId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String winnerId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String winnerName = null;
    private final double winningBid = 0.0;
    private final int totalBids = 0;
    private final long endTime = 0L;
    private final long paymentDeadline = 0L;
    private final boolean isPaid = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String transactionId = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.DeliveryStatus deliveryStatus = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long completionDate = null;
    
    public AuctionResult(@org.jetbrains.annotations.NotNull()
    java.lang.String auctionId, @org.jetbrains.annotations.Nullable()
    java.lang.String winnerId, @org.jetbrains.annotations.NotNull()
    java.lang.String winnerName, double winningBid, int totalBids, long endTime, long paymentDeadline, boolean isPaid, @org.jetbrains.annotations.NotNull()
    java.lang.String transactionId, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.DeliveryStatus deliveryStatus, @org.jetbrains.annotations.Nullable()
    java.lang.Long completionDate) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAuctionId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getWinnerId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getWinnerName() {
        return null;
    }
    
    public final double getWinningBid() {
        return 0.0;
    }
    
    public final int getTotalBids() {
        return 0;
    }
    
    public final long getEndTime() {
        return 0L;
    }
    
    public final long getPaymentDeadline() {
        return 0L;
    }
    
    public final boolean isPaid() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTransactionId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.DeliveryStatus getDeliveryStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getCompletionDate() {
        return null;
    }
    
    public AuctionResult() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.DeliveryStatus component10() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component11() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    public final double component4() {
        return 0.0;
    }
    
    public final int component5() {
        return 0;
    }
    
    public final long component6() {
        return 0L;
    }
    
    public final long component7() {
        return 0L;
    }
    
    public final boolean component8() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.AuctionResult copy(@org.jetbrains.annotations.NotNull()
    java.lang.String auctionId, @org.jetbrains.annotations.Nullable()
    java.lang.String winnerId, @org.jetbrains.annotations.NotNull()
    java.lang.String winnerName, double winningBid, int totalBids, long endTime, long paymentDeadline, boolean isPaid, @org.jetbrains.annotations.NotNull()
    java.lang.String transactionId, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.DeliveryStatus deliveryStatus, @org.jetbrains.annotations.Nullable()
    java.lang.Long completionDate) {
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