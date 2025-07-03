package com.example.rooster.core.common.models.auction;

import com.example.rooster.core.common.enums.AuctionStatus;
import com.example.rooster.core.common.enums.BidMonitoringCategory;
import kotlinx.serialization.Serializable;
import java.util.Date;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\bQ\b\u0087\b\u0018\u00002\u00020\u0001B\u0095\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\u000b\u0012\u0006\u0010\r\u001a\u00020\u0003\u0012\u0006\u0010\u000e\u001a\u00020\u0003\u0012\u0006\u0010\u000f\u001a\u00020\u0003\u0012\u0006\u0010\u0010\u001a\u00020\u0011\u0012\u0006\u0010\u0012\u001a\u00020\u0013\u0012\u0006\u0010\u0014\u001a\u00020\u0007\u0012\u000e\b\u0002\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00030\u0016\u0012\u0006\u0010\u0017\u001a\u00020\u0018\u0012\u0006\u0010\u0019\u001a\u00020\u0003\u0012\u0006\u0010\u001a\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\u0011\u0012\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u001d\u001a\u0004\u0018\u00010\u0013\u0012\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\u001f\u001a\u0004\u0018\u00010\u0013\u0012\n\b\u0002\u0010 \u001a\u0004\u0018\u00010!\u0012\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010\u0013\u0012\n\b\u0002\u0010#\u001a\u0004\u0018\u00010\u0011\u0012\n\b\u0002\u0010$\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010%\u001a\u0004\u0018\u00010\u0011\u00a2\u0006\u0002\u0010&J\t\u0010P\u001a\u00020\u0003H\u00c6\u0003J\t\u0010Q\u001a\u00020\u0003H\u00c6\u0003J\t\u0010R\u001a\u00020\u0003H\u00c6\u0003J\t\u0010S\u001a\u00020\u0011H\u00c6\u0003J\t\u0010T\u001a\u00020\u0013H\u00c6\u0003J\t\u0010U\u001a\u00020\u0007H\u00c6\u0003J\u000f\u0010V\u001a\b\u0012\u0004\u0012\u00020\u00030\u0016H\u00c6\u0003J\t\u0010W\u001a\u00020\u0018H\u00c6\u0003J\t\u0010X\u001a\u00020\u0003H\u00c6\u0003J\t\u0010Y\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010Z\u001a\u0004\u0018\u00010\u0011H\u00c6\u0003\u00a2\u0006\u0002\u00107J\t\u0010[\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010\\\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003\u00a2\u0006\u0002\u00100J\u0010\u0010]\u001a\u0004\u0018\u00010\u0013H\u00c6\u0003\u00a2\u0006\u0002\u0010(J\u0010\u0010^\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003\u00a2\u0006\u0002\u00100J\u0010\u0010_\u001a\u0004\u0018\u00010\u0013H\u00c6\u0003\u00a2\u0006\u0002\u0010(J\u000b\u0010`\u001a\u0004\u0018\u00010!H\u00c6\u0003J\u0010\u0010a\u001a\u0004\u0018\u00010\u0013H\u00c6\u0003\u00a2\u0006\u0002\u0010(J\u0010\u0010b\u001a\u0004\u0018\u00010\u0011H\u00c6\u0003\u00a2\u0006\u0002\u00107J\u0010\u0010c\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003\u00a2\u0006\u0002\u00100J\u0010\u0010d\u001a\u0004\u0018\u00010\u0011H\u00c6\u0003\u00a2\u0006\u0002\u00107J\t\u0010e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010f\u001a\u00020\u0007H\u00c6\u0003J\t\u0010g\u001a\u00020\u0007H\u00c6\u0003J\t\u0010h\u001a\u00020\u0007H\u00c6\u0003J\t\u0010i\u001a\u00020\u000bH\u00c6\u0003J\t\u0010j\u001a\u00020\u000bH\u00c6\u0003J\t\u0010k\u001a\u00020\u0003H\u00c6\u0003J\u00c0\u0002\u0010l\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u00072\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\u00032\b\b\u0002\u0010\u000e\u001a\u00020\u00032\b\b\u0002\u0010\u000f\u001a\u00020\u00032\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010\u0014\u001a\u00020\u00072\u000e\b\u0002\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00030\u00162\b\b\u0002\u0010\u0017\u001a\u00020\u00182\b\b\u0002\u0010\u0019\u001a\u00020\u00032\b\b\u0002\u0010\u001a\u001a\u00020\u00032\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\u00112\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u001d\u001a\u0004\u0018\u00010\u00132\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\u001f\u001a\u0004\u0018\u00010\u00132\n\b\u0002\u0010 \u001a\u0004\u0018\u00010!2\n\b\u0002\u0010\"\u001a\u0004\u0018\u00010\u00132\n\b\u0002\u0010#\u001a\u0004\u0018\u00010\u00112\n\b\u0002\u0010$\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010%\u001a\u0004\u0018\u00010\u0011H\u00c6\u0001\u00a2\u0006\u0002\u0010mJ\u0013\u0010n\u001a\u00020\u00132\b\u0010o\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010p\u001a\u00020\u0011H\u00d6\u0001J\t\u0010q\u001a\u00020\u0003H\u00d6\u0001R\u0015\u0010\u001f\u001a\u0004\u0018\u00010\u0013\u00a2\u0006\n\n\u0002\u0010)\u001a\u0004\b\'\u0010(R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010+R\u0015\u0010\"\u001a\u0004\u0018\u00010\u0013\u00a2\u0006\n\n\u0002\u0010)\u001a\u0004\b,\u0010(R\u0011\u0010\u0010\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010.R\u0015\u0010\u001e\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\n\n\u0002\u00101\u001a\u0004\b/\u00100R\u0015\u0010$\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\n\n\u0002\u00101\u001a\u0004\b2\u00100R\u0011\u0010\u001a\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u0010+R\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b4\u00105R\u0015\u0010\u001b\u001a\u0004\u0018\u00010\u0011\u00a2\u0006\n\n\u0002\u00108\u001a\u0004\b6\u00107R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b9\u0010+R\u0011\u0010\f\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b:\u0010;R\u0015\u0010#\u001a\u0004\u0018\u00010\u0011\u00a2\u0006\n\n\u0002\u00108\u001a\u0004\b<\u00107R\u0011\u0010\u000f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b=\u0010+R\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00030\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b>\u0010?R\u0011\u0010\u0012\u001a\u00020\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010@R\u0011\u0010\u0019\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bA\u0010+R\u0015\u0010\u001c\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\n\n\u0002\u00101\u001a\u0004\bB\u00100R\u0011\u0010\t\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\bC\u00105R\u0015\u0010\u001d\u001a\u0004\u0018\u00010\u0013\u00a2\u0006\n\n\u0002\u0010)\u001a\u0004\bD\u0010(R\u0011\u0010\u0014\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\bE\u00105R\u0013\u0010 \u001a\u0004\u0018\u00010!\u00a2\u0006\b\n\u0000\u001a\u0004\bF\u0010GR\u0011\u0010\r\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bH\u0010+R\u0011\u0010\u000e\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bI\u0010+R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\bJ\u0010;R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\bK\u00105R\u0011\u0010\u0017\u001a\u00020\u0018\u00a2\u0006\b\n\u0000\u001a\u0004\bL\u0010MR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bN\u0010+R\u0015\u0010%\u001a\u0004\u0018\u00010\u0011\u00a2\u0006\n\n\u0002\u00108\u001a\u0004\bO\u00107\u00a8\u0006r"}, d2 = {"Lcom/example/rooster/core/common/models/auction/AuctionListing;", "", "auctionId", "", "title", "description", "startingPrice", "", "currentBid", "minimumIncrement", "startTime", "Ljava/util/Date;", "endTime", "sellerId", "sellerName", "fowlId", "bidCount", "", "isReserveSet", "", "reservePrice", "imageUrls", "", "status", "Lcom/example/rooster/core/common/enums/AuctionStatus;", "location", "category", "customDurationHours", "minimumBidPrice", "requiresBidderDeposit", "bidderDepositPercentage", "allowsProxyBidding", "sellerBidMonitoring", "Lcom/example/rooster/core/common/enums/BidMonitoringCategory;", "autoExtendOnLastMinuteBid", "extensionMinutes", "buyNowPrice", "watchers", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DDDLjava/util/Date;Ljava/util/Date;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IZDLjava/util/List;Lcom/example/rooster/core/common/enums/AuctionStatus;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Double;Ljava/lang/Boolean;Ljava/lang/Double;Ljava/lang/Boolean;Lcom/example/rooster/core/common/enums/BidMonitoringCategory;Ljava/lang/Boolean;Ljava/lang/Integer;Ljava/lang/Double;Ljava/lang/Integer;)V", "getAllowsProxyBidding", "()Ljava/lang/Boolean;", "Ljava/lang/Boolean;", "getAuctionId", "()Ljava/lang/String;", "getAutoExtendOnLastMinuteBid", "getBidCount", "()I", "getBidderDepositPercentage", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getBuyNowPrice", "getCategory", "getCurrentBid", "()D", "getCustomDurationHours", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getDescription", "getEndTime", "()Ljava/util/Date;", "getExtensionMinutes", "getFowlId", "getImageUrls", "()Ljava/util/List;", "()Z", "getLocation", "getMinimumBidPrice", "getMinimumIncrement", "getRequiresBidderDeposit", "getReservePrice", "getSellerBidMonitoring", "()Lcom/example/rooster/core/common/enums/BidMonitoringCategory;", "getSellerId", "getSellerName", "getStartTime", "getStartingPrice", "getStatus", "()Lcom/example/rooster/core/common/enums/AuctionStatus;", "getTitle", "getWatchers", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component20", "component21", "component22", "component23", "component24", "component25", "component26", "component27", "component28", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DDDLjava/util/Date;Ljava/util/Date;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IZDLjava/util/List;Lcom/example/rooster/core/common/enums/AuctionStatus;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Double;Ljava/lang/Boolean;Ljava/lang/Double;Ljava/lang/Boolean;Lcom/example/rooster/core/common/enums/BidMonitoringCategory;Ljava/lang/Boolean;Ljava/lang/Integer;Ljava/lang/Double;Ljava/lang/Integer;)Lcom/example/rooster/core/common/models/auction/AuctionListing;", "equals", "other", "hashCode", "toString", "core-common_debug"})
public final class AuctionListing {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String auctionId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String title = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String description = null;
    private final double startingPrice = 0.0;
    private final double currentBid = 0.0;
    private final double minimumIncrement = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Date startTime = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Date endTime = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String sellerId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String sellerName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String fowlId = null;
    private final int bidCount = 0;
    private final boolean isReserveSet = false;
    private final double reservePrice = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> imageUrls = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.enums.AuctionStatus status = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String location = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String category = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer customDurationHours = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double minimumBidPrice = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Boolean requiresBidderDeposit = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double bidderDepositPercentage = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Boolean allowsProxyBidding = null;
    @org.jetbrains.annotations.Nullable()
    private final com.example.rooster.core.common.enums.BidMonitoringCategory sellerBidMonitoring = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Boolean autoExtendOnLastMinuteBid = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer extensionMinutes = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double buyNowPrice = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer watchers = null;
    
    public AuctionListing(@org.jetbrains.annotations.NotNull()
    java.lang.String auctionId, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String description, double startingPrice, double currentBid, double minimumIncrement, @org.jetbrains.annotations.NotNull()
    java.util.Date startTime, @org.jetbrains.annotations.NotNull()
    java.util.Date endTime, @org.jetbrains.annotations.NotNull()
    java.lang.String sellerId, @org.jetbrains.annotations.NotNull()
    java.lang.String sellerName, @org.jetbrains.annotations.NotNull()
    java.lang.String fowlId, int bidCount, boolean isReserveSet, double reservePrice, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> imageUrls, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.enums.AuctionStatus status, @org.jetbrains.annotations.NotNull()
    java.lang.String location, @org.jetbrains.annotations.NotNull()
    java.lang.String category, @org.jetbrains.annotations.Nullable()
    java.lang.Integer customDurationHours, @org.jetbrains.annotations.Nullable()
    java.lang.Double minimumBidPrice, @org.jetbrains.annotations.Nullable()
    java.lang.Boolean requiresBidderDeposit, @org.jetbrains.annotations.Nullable()
    java.lang.Double bidderDepositPercentage, @org.jetbrains.annotations.Nullable()
    java.lang.Boolean allowsProxyBidding, @org.jetbrains.annotations.Nullable()
    com.example.rooster.core.common.enums.BidMonitoringCategory sellerBidMonitoring, @org.jetbrains.annotations.Nullable()
    java.lang.Boolean autoExtendOnLastMinuteBid, @org.jetbrains.annotations.Nullable()
    java.lang.Integer extensionMinutes, @org.jetbrains.annotations.Nullable()
    java.lang.Double buyNowPrice, @org.jetbrains.annotations.Nullable()
    java.lang.Integer watchers) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAuctionId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDescription() {
        return null;
    }
    
    public final double getStartingPrice() {
        return 0.0;
    }
    
    public final double getCurrentBid() {
        return 0.0;
    }
    
    public final double getMinimumIncrement() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date getStartTime() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date getEndTime() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSellerId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSellerName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFowlId() {
        return null;
    }
    
    public final int getBidCount() {
        return 0;
    }
    
    public final boolean isReserveSet() {
        return false;
    }
    
    public final double getReservePrice() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getImageUrls() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.enums.AuctionStatus getStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getLocation() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCategory() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getCustomDurationHours() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getMinimumBidPrice() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Boolean getRequiresBidderDeposit() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getBidderDepositPercentage() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Boolean getAllowsProxyBidding() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.rooster.core.common.enums.BidMonitoringCategory getSellerBidMonitoring() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Boolean getAutoExtendOnLastMinuteBid() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getExtensionMinutes() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getBuyNowPrice() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getWatchers() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component10() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component11() {
        return null;
    }
    
    public final int component12() {
        return 0;
    }
    
    public final boolean component13() {
        return false;
    }
    
    public final double component14() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component15() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.enums.AuctionStatus component16() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component17() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component18() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component19() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component20() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Boolean component21() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component22() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Boolean component23() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.rooster.core.common.enums.BidMonitoringCategory component24() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Boolean component25() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component26() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component27() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component28() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    public final double component4() {
        return 0.0;
    }
    
    public final double component5() {
        return 0.0;
    }
    
    public final double component6() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.models.auction.AuctionListing copy(@org.jetbrains.annotations.NotNull()
    java.lang.String auctionId, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String description, double startingPrice, double currentBid, double minimumIncrement, @org.jetbrains.annotations.NotNull()
    java.util.Date startTime, @org.jetbrains.annotations.NotNull()
    java.util.Date endTime, @org.jetbrains.annotations.NotNull()
    java.lang.String sellerId, @org.jetbrains.annotations.NotNull()
    java.lang.String sellerName, @org.jetbrains.annotations.NotNull()
    java.lang.String fowlId, int bidCount, boolean isReserveSet, double reservePrice, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> imageUrls, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.enums.AuctionStatus status, @org.jetbrains.annotations.NotNull()
    java.lang.String location, @org.jetbrains.annotations.NotNull()
    java.lang.String category, @org.jetbrains.annotations.Nullable()
    java.lang.Integer customDurationHours, @org.jetbrains.annotations.Nullable()
    java.lang.Double minimumBidPrice, @org.jetbrains.annotations.Nullable()
    java.lang.Boolean requiresBidderDeposit, @org.jetbrains.annotations.Nullable()
    java.lang.Double bidderDepositPercentage, @org.jetbrains.annotations.Nullable()
    java.lang.Boolean allowsProxyBidding, @org.jetbrains.annotations.Nullable()
    com.example.rooster.core.common.enums.BidMonitoringCategory sellerBidMonitoring, @org.jetbrains.annotations.Nullable()
    java.lang.Boolean autoExtendOnLastMinuteBid, @org.jetbrains.annotations.Nullable()
    java.lang.Integer extensionMinutes, @org.jetbrains.annotations.Nullable()
    java.lang.Double buyNowPrice, @org.jetbrains.annotations.Nullable()
    java.lang.Integer watchers) {
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