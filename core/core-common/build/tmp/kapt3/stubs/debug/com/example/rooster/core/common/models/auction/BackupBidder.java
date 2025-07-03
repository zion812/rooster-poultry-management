package com.example.rooster.core.common.models.auction;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B;\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\b\u0010\t\u001a\u0004\u0018\u00010\n\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\b\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0006H\u00c6\u0003J\u000b\u0010\u001a\u001a\u0004\u0018\u00010\bH\u00c6\u0003J\u000b\u0010\u001b\u001a\u0004\u0018\u00010\nH\u00c6\u0003J\u000b\u0010\u001c\u001a\u0004\u0018\u00010\bH\u00c6\u0003JK\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\bH\u00c6\u0001J\u0013\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010!\u001a\u00020\"H\u00d6\u0001J\t\u0010#\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0013\u0010\t\u001a\u0004\u0018\u00010\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0015\u00a8\u0006$"}, d2 = {"Lcom/example/rooster/core/common/models/auction/BackupBidder;", "", "bidderId", "", "bidderName", "bidAmount", "", "offerSentTime", "Ljava/util/Date;", "offerResponse", "Lcom/example/rooster/core/common/enums/OfferResponse;", "responseDeadline", "(Ljava/lang/String;Ljava/lang/String;DLjava/util/Date;Lcom/example/rooster/core/common/enums/OfferResponse;Ljava/util/Date;)V", "getBidAmount", "()D", "getBidderId", "()Ljava/lang/String;", "getBidderName", "getOfferResponse", "()Lcom/example/rooster/core/common/enums/OfferResponse;", "getOfferSentTime", "()Ljava/util/Date;", "getResponseDeadline", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "", "toString", "core-common_debug"})
public final class BackupBidder {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String bidderId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String bidderName = null;
    private final double bidAmount = 0.0;
    @org.jetbrains.annotations.Nullable()
    private final java.util.Date offerSentTime = null;
    @org.jetbrains.annotations.Nullable()
    private final com.example.rooster.core.common.enums.OfferResponse offerResponse = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.Date responseDeadline = null;
    
    public BackupBidder(@org.jetbrains.annotations.NotNull()
    java.lang.String bidderId, @org.jetbrains.annotations.NotNull()
    java.lang.String bidderName, double bidAmount, @org.jetbrains.annotations.Nullable()
    java.util.Date offerSentTime, @org.jetbrains.annotations.Nullable()
    com.example.rooster.core.common.enums.OfferResponse offerResponse, @org.jetbrains.annotations.Nullable()
    java.util.Date responseDeadline) {
        super();
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
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date getOfferSentTime() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.rooster.core.common.enums.OfferResponse getOfferResponse() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date getResponseDeadline() {
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
    
    public final double component3() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.rooster.core.common.enums.OfferResponse component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.models.auction.BackupBidder copy(@org.jetbrains.annotations.NotNull()
    java.lang.String bidderId, @org.jetbrains.annotations.NotNull()
    java.lang.String bidderName, double bidAmount, @org.jetbrains.annotations.Nullable()
    java.util.Date offerSentTime, @org.jetbrains.annotations.Nullable()
    com.example.rooster.core.common.enums.OfferResponse offerResponse, @org.jetbrains.annotations.Nullable()
    java.util.Date responseDeadline) {
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