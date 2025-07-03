package com.example.rooster.core.common.models.auction;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b.\b\u0087\b\u0018\u00002\u00020\u0001B\u0089\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\f\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\b\u0012\b\u0010\u000f\u001a\u0004\u0018\u00010\b\u0012\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u0012\u0006\u0010\u0012\u001a\u00020\u0013\u0012\b\u0010\u0014\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0015\u001a\u0004\u0018\u00010\b\u0012\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017\u00a2\u0006\u0002\u0010\u0018J\t\u00100\u001a\u00020\u0003H\u00c6\u0003J\u0010\u00101\u001a\u0004\u0018\u00010\bH\u00c6\u0003\u00a2\u0006\u0002\u0010&J\u000b\u00102\u001a\u0004\u0018\u00010\u0011H\u00c6\u0003J\t\u00103\u001a\u00020\u0013H\u00c6\u0003J\u000b\u00104\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0010\u00105\u001a\u0004\u0018\u00010\bH\u00c6\u0003\u00a2\u0006\u0002\u0010&J\u0010\u00106\u001a\u0004\u0018\u00010\u0017H\u00c6\u0003\u00a2\u0006\u0002\u0010-J\t\u00107\u001a\u00020\u0003H\u00c6\u0003J\t\u00108\u001a\u00020\u0003H\u00c6\u0003J\t\u00109\u001a\u00020\u0003H\u00c6\u0003J\t\u0010:\u001a\u00020\bH\u00c6\u0003J\t\u0010;\u001a\u00020\nH\u00c6\u0003J\t\u0010<\u001a\u00020\fH\u00c6\u0003J\t\u0010=\u001a\u00020\fH\u00c6\u0003J\u0010\u0010>\u001a\u0004\u0018\u00010\bH\u00c6\u0003\u00a2\u0006\u0002\u0010&J\u00b0\u0001\u0010?\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\f2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u00132\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u00c6\u0001\u00a2\u0006\u0002\u0010@J\u0013\u0010A\u001a\u00020\f2\b\u0010B\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010C\u001a\u00020\u0017H\u00d6\u0001J\t\u0010D\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001aR\u0013\u0010\u0014\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001aR\u0011\u0010\u0012\u001a\u00020\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001aR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001aR\u0015\u0010\u0015\u001a\u0004\u0018\u00010\b\u00a2\u0006\n\n\u0002\u0010\'\u001a\u0004\b%\u0010&R\u0015\u0010\u000f\u001a\u0004\u0018\u00010\b\u00a2\u0006\n\n\u0002\u0010\'\u001a\u0004\b(\u0010&R\u0013\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u0011\u0010\r\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010+R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010+R\u0015\u0010\u0016\u001a\u0004\u0018\u00010\u0017\u00a2\u0006\n\n\u0002\u0010.\u001a\u0004\b,\u0010-R\u0015\u0010\u000e\u001a\u0004\u0018\u00010\b\u00a2\u0006\n\n\u0002\u0010\'\u001a\u0004\b/\u0010&\u00a8\u0006E"}, d2 = {"Lcom/example/rooster/core/common/models/auction/EnhancedAuctionBid;", "", "bidId", "", "auctionId", "bidderId", "bidderName", "bidAmount", "", "bidTime", "Ljava/util/Date;", "isWinning", "", "isProxyBid", "proxyMaxAmount", "depositAmount", "depositStatus", "Lcom/example/rooster/core/common/enums/DepositStatus;", "bidStatus", "Lcom/example/rooster/core/common/enums/BidStatus;", "bidMessage", "bidderRating", "previousBidCount", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DLjava/util/Date;ZZLjava/lang/Double;Ljava/lang/Double;Lcom/example/rooster/core/common/enums/DepositStatus;Lcom/example/rooster/core/common/enums/BidStatus;Ljava/lang/String;Ljava/lang/Double;Ljava/lang/Integer;)V", "getAuctionId", "()Ljava/lang/String;", "getBidAmount", "()D", "getBidId", "getBidMessage", "getBidStatus", "()Lcom/example/rooster/core/common/enums/BidStatus;", "getBidTime", "()Ljava/util/Date;", "getBidderId", "getBidderName", "getBidderRating", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getDepositAmount", "getDepositStatus", "()Lcom/example/rooster/core/common/enums/DepositStatus;", "()Z", "getPreviousBidCount", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getProxyMaxAmount", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DLjava/util/Date;ZZLjava/lang/Double;Ljava/lang/Double;Lcom/example/rooster/core/common/enums/DepositStatus;Lcom/example/rooster/core/common/enums/BidStatus;Ljava/lang/String;Ljava/lang/Double;Ljava/lang/Integer;)Lcom/example/rooster/core/common/models/auction/EnhancedAuctionBid;", "equals", "other", "hashCode", "toString", "core-common_release"})
public final class EnhancedAuctionBid {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String bidId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String auctionId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String bidderId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String bidderName = null;
    private final double bidAmount = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Date bidTime = null;
    private final boolean isWinning = false;
    private final boolean isProxyBid = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double proxyMaxAmount = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double depositAmount = null;
    @org.jetbrains.annotations.Nullable()
    private final com.example.rooster.core.common.enums.DepositStatus depositStatus = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.enums.BidStatus bidStatus = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String bidMessage = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double bidderRating = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer previousBidCount = null;
    
    public EnhancedAuctionBid(@org.jetbrains.annotations.NotNull()
    java.lang.String bidId, @org.jetbrains.annotations.NotNull()
    java.lang.String auctionId, @org.jetbrains.annotations.NotNull()
    java.lang.String bidderId, @org.jetbrains.annotations.NotNull()
    java.lang.String bidderName, double bidAmount, @org.jetbrains.annotations.NotNull()
    java.util.Date bidTime, boolean isWinning, boolean isProxyBid, @org.jetbrains.annotations.Nullable()
    java.lang.Double proxyMaxAmount, @org.jetbrains.annotations.Nullable()
    java.lang.Double depositAmount, @org.jetbrains.annotations.Nullable()
    com.example.rooster.core.common.enums.DepositStatus depositStatus, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.enums.BidStatus bidStatus, @org.jetbrains.annotations.Nullable()
    java.lang.String bidMessage, @org.jetbrains.annotations.Nullable()
    java.lang.Double bidderRating, @org.jetbrains.annotations.Nullable()
    java.lang.Integer previousBidCount) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getBidId() {
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
    
    public final double getBidAmount() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date getBidTime() {
        return null;
    }
    
    public final boolean isWinning() {
        return false;
    }
    
    public final boolean isProxyBid() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getProxyMaxAmount() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getDepositAmount() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.rooster.core.common.enums.DepositStatus getDepositStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.enums.BidStatus getBidStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getBidMessage() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getBidderRating() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getPreviousBidCount() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component10() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.rooster.core.common.enums.DepositStatus component11() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.enums.BidStatus component12() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component13() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component14() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component15() {
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
    
    public final double component5() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date component6() {
        return null;
    }
    
    public final boolean component7() {
        return false;
    }
    
    public final boolean component8() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.models.auction.EnhancedAuctionBid copy(@org.jetbrains.annotations.NotNull()
    java.lang.String bidId, @org.jetbrains.annotations.NotNull()
    java.lang.String auctionId, @org.jetbrains.annotations.NotNull()
    java.lang.String bidderId, @org.jetbrains.annotations.NotNull()
    java.lang.String bidderName, double bidAmount, @org.jetbrains.annotations.NotNull()
    java.util.Date bidTime, boolean isWinning, boolean isProxyBid, @org.jetbrains.annotations.Nullable()
    java.lang.Double proxyMaxAmount, @org.jetbrains.annotations.Nullable()
    java.lang.Double depositAmount, @org.jetbrains.annotations.Nullable()
    com.example.rooster.core.common.enums.DepositStatus depositStatus, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.enums.BidStatus bidStatus, @org.jetbrains.annotations.Nullable()
    java.lang.String bidMessage, @org.jetbrains.annotations.Nullable()
    java.lang.Double bidderRating, @org.jetbrains.annotations.Nullable()
    java.lang.Integer previousBidCount) {
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