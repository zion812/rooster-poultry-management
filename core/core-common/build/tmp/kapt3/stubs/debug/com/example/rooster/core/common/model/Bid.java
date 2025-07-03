package com.example.rooster.core.common.model;

import kotlinx.serialization.Serializable;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b&\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u007f\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\b\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\b\u0012\b\b\u0002\u0010\u0011\u001a\u00020\f\u0012\b\b\u0002\u0010\u0012\u001a\u00020\f\u00a2\u0006\u0002\u0010\u0013J\t\u0010%\u001a\u00020\u0003H\u00c6\u0003J\t\u0010&\u001a\u00020\bH\u00c6\u0003J\t\u0010\'\u001a\u00020\fH\u00c6\u0003J\t\u0010(\u001a\u00020\fH\u00c6\u0003J\t\u0010)\u001a\u00020\u0003H\u00c6\u0003J\t\u0010*\u001a\u00020\u0003H\u00c6\u0003J\t\u0010+\u001a\u00020\u0003H\u00c6\u0003J\t\u0010,\u001a\u00020\bH\u00c6\u0003J\t\u0010-\u001a\u00020\nH\u00c6\u0003J\t\u0010.\u001a\u00020\fH\u00c6\u0003J\u0010\u0010/\u001a\u0004\u0018\u00010\bH\u00c6\u0003\u00a2\u0006\u0002\u0010\u001fJ\t\u00100\u001a\u00020\u000fH\u00c6\u0003J\u0088\u0001\u00101\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\b2\b\b\u0002\u0010\u0011\u001a\u00020\f2\b\b\u0002\u0010\u0012\u001a\u00020\fH\u00c6\u0001\u00a2\u0006\u0002\u00102J\u0013\u00103\u001a\u00020\f2\b\u00104\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00105\u001a\u000206H\u00d6\u0001J\t\u00107\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0017R\u0011\u0010\u0010\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0015R\u0011\u0010\u0011\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0017R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u001cR\u0011\u0010\u0012\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u001cR\u0015\u0010\r\u001a\u0004\u0018\u00010\b\u00a2\u0006\n\n\u0002\u0010 \u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010$\u00a8\u00068"}, d2 = {"Lcom/example/rooster/core/common/model/Bid;", "", "id", "", "auctionId", "bidderId", "bidderName", "amount", "", "timestamp", "", "isAutoBid", "", "maxBidAmount", "status", "Lcom/example/rooster/core/common/model/BidStatus;", "bidderRating", "depositPaid", "isWinning", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DJZLjava/lang/Double;Lcom/example/rooster/core/common/model/BidStatus;DZZ)V", "getAmount", "()D", "getAuctionId", "()Ljava/lang/String;", "getBidderId", "getBidderName", "getBidderRating", "getDepositPaid", "()Z", "getId", "getMaxBidAmount", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getStatus", "()Lcom/example/rooster/core/common/model/BidStatus;", "getTimestamp", "()J", "component1", "component10", "component11", "component12", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DJZLjava/lang/Double;Lcom/example/rooster/core/common/model/BidStatus;DZZ)Lcom/example/rooster/core/common/model/Bid;", "equals", "other", "hashCode", "", "toString", "core-common_debug"})
public final class Bid {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String auctionId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String bidderId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String bidderName = null;
    private final double amount = 0.0;
    private final long timestamp = 0L;
    private final boolean isAutoBid = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double maxBidAmount = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.BidStatus status = null;
    private final double bidderRating = 0.0;
    private final boolean depositPaid = false;
    private final boolean isWinning = false;
    
    public Bid(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String auctionId, @org.jetbrains.annotations.NotNull()
    java.lang.String bidderId, @org.jetbrains.annotations.NotNull()
    java.lang.String bidderName, double amount, long timestamp, boolean isAutoBid, @org.jetbrains.annotations.Nullable()
    java.lang.Double maxBidAmount, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.BidStatus status, double bidderRating, boolean depositPaid, boolean isWinning) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAuctionId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getBidderId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getBidderName() {
        return null;
    }
    
    public final double getAmount() {
        return 0.0;
    }
    
    public final long getTimestamp() {
        return 0L;
    }
    
    public final boolean isAutoBid() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getMaxBidAmount() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.BidStatus getStatus() {
        return null;
    }
    
    public final double getBidderRating() {
        return 0.0;
    }
    
    public final boolean getDepositPaid() {
        return false;
    }
    
    public final boolean isWinning() {
        return false;
    }
    
    public Bid() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    public final double component10() {
        return 0.0;
    }
    
    public final boolean component11() {
        return false;
    }
    
    public final boolean component12() {
        return false;
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
    
    public final double component5() {
        return 0.0;
    }
    
    public final long component6() {
        return 0L;
    }
    
    public final boolean component7() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.BidStatus component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Bid copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String auctionId, @org.jetbrains.annotations.NotNull()
    java.lang.String bidderId, @org.jetbrains.annotations.NotNull()
    java.lang.String bidderName, double amount, long timestamp, boolean isAutoBid, @org.jetbrains.annotations.Nullable()
    java.lang.Double maxBidAmount, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.BidStatus status, double bidderRating, boolean depositPaid, boolean isWinning) {
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