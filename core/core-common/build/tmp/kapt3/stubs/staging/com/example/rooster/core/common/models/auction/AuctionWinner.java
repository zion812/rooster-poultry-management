package com.example.rooster.core.common.models.auction;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BC\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r\u00a2\u0006\u0002\u0010\u000fJ\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0007H\u00c6\u0003J\t\u0010 \u001a\u00020\tH\u00c6\u0003J\t\u0010!\u001a\u00020\u000bH\u00c6\u0003J\u000f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u00c6\u0003JU\u0010#\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u00c6\u0001J\u0013\u0010$\u001a\u00020%2\b\u0010&\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\'\u001a\u00020(H\u00d6\u0001J\t\u0010)\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0011R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001b\u00a8\u0006*"}, d2 = {"Lcom/example/rooster/core/common/models/auction/AuctionWinner;", "", "auctionId", "", "winnerId", "winnerName", "winningBid", "", "paymentDeadline", "Ljava/util/Date;", "paymentStatus", "Lcom/example/rooster/core/common/enums/AuctionPaymentStatus;", "backupBidders", "", "Lcom/example/rooster/core/common/models/auction/BackupBidder;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DLjava/util/Date;Lcom/example/rooster/core/common/enums/AuctionPaymentStatus;Ljava/util/List;)V", "getAuctionId", "()Ljava/lang/String;", "getBackupBidders", "()Ljava/util/List;", "getPaymentDeadline", "()Ljava/util/Date;", "getPaymentStatus", "()Lcom/example/rooster/core/common/enums/AuctionPaymentStatus;", "getWinnerId", "getWinnerName", "getWinningBid", "()D", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "hashCode", "", "toString", "core-common_staging"})
public final class AuctionWinner {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String auctionId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String winnerId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String winnerName = null;
    private final double winningBid = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Date paymentDeadline = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.enums.AuctionPaymentStatus paymentStatus = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.rooster.core.common.models.auction.BackupBidder> backupBidders = null;
    
    public AuctionWinner(@org.jetbrains.annotations.NotNull()
    java.lang.String auctionId, @org.jetbrains.annotations.NotNull()
    java.lang.String winnerId, @org.jetbrains.annotations.NotNull()
    java.lang.String winnerName, double winningBid, @org.jetbrains.annotations.NotNull()
    java.util.Date paymentDeadline, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.enums.AuctionPaymentStatus paymentStatus, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.models.auction.BackupBidder> backupBidders) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAuctionId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
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
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date getPaymentDeadline() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.enums.AuctionPaymentStatus getPaymentStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.models.auction.BackupBidder> getBackupBidders() {
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
    
    public final double component4() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.enums.AuctionPaymentStatus component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.models.auction.BackupBidder> component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.models.auction.AuctionWinner copy(@org.jetbrains.annotations.NotNull()
    java.lang.String auctionId, @org.jetbrains.annotations.NotNull()
    java.lang.String winnerId, @org.jetbrains.annotations.NotNull()
    java.lang.String winnerName, double winningBid, @org.jetbrains.annotations.NotNull()
    java.util.Date paymentDeadline, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.enums.AuctionPaymentStatus paymentStatus, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.models.auction.BackupBidder> backupBidders) {
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