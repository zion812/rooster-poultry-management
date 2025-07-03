package com.example.rooster.core.common.model;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\bW\b\u0087\b\u0018\u00002\u00020\u0001B\u00eb\u0002\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u000b\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u000e\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0013\u0012\b\b\u0002\u0010\u0014\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017\u0012\u000e\b\u0002\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00030\u0017\u0012\b\b\u0002\u0010\u001a\u001a\u00020\u001b\u0012\b\b\u0002\u0010\u001c\u001a\u00020\u001d\u0012\u000e\b\u0002\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00030\u0017\u0012\u000e\b\u0002\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00030\u0017\u0012\u000e\b\u0002\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00030\u0017\u0012\b\b\u0002\u0010!\u001a\u00020\"\u0012\u000e\b\u0002\u0010#\u001a\b\u0012\u0004\u0012\u00020$0\u0017\u0012\b\b\u0002\u0010%\u001a\u00020\u0003\u0012\b\b\u0002\u0010&\u001a\u00020\u0003\u0012\b\b\u0002\u0010\'\u001a\u00020\u0003\u0012\b\b\u0002\u0010(\u001a\u00020\u000b\u0012\b\b\u0002\u0010)\u001a\u00020*\u0012\b\b\u0002\u0010+\u001a\u00020\u000e\u0012\b\b\u0002\u0010,\u001a\u00020*\u0012\b\b\u0002\u0010-\u001a\u00020\u0013\u00a2\u0006\u0002\u0010.J\t\u0010[\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010\\\u001a\u0004\u0018\u00010\u000eH\u00c6\u0003\u00a2\u0006\u0002\u0010MJ\t\u0010]\u001a\u00020\u000eH\u00c6\u0003J\t\u0010^\u001a\u00020\u000eH\u00c6\u0003J\t\u0010_\u001a\u00020\u0013H\u00c6\u0003J\t\u0010`\u001a\u00020\u0003H\u00c6\u0003J\t\u0010a\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010b\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017H\u00c6\u0003J\u000f\u0010c\u001a\b\u0012\u0004\u0012\u00020\u00030\u0017H\u00c6\u0003J\t\u0010d\u001a\u00020\u001bH\u00c6\u0003J\t\u0010e\u001a\u00020\u001dH\u00c6\u0003J\t\u0010f\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010g\u001a\b\u0012\u0004\u0012\u00020\u00030\u0017H\u00c6\u0003J\u000f\u0010h\u001a\b\u0012\u0004\u0012\u00020\u00030\u0017H\u00c6\u0003J\u000f\u0010i\u001a\b\u0012\u0004\u0012\u00020\u00030\u0017H\u00c6\u0003J\t\u0010j\u001a\u00020\"H\u00c6\u0003J\u000f\u0010k\u001a\b\u0012\u0004\u0012\u00020$0\u0017H\u00c6\u0003J\t\u0010l\u001a\u00020\u0003H\u00c6\u0003J\t\u0010m\u001a\u00020\u0003H\u00c6\u0003J\t\u0010n\u001a\u00020\u0003H\u00c6\u0003J\t\u0010o\u001a\u00020\u000bH\u00c6\u0003J\t\u0010p\u001a\u00020*H\u00c6\u0003J\t\u0010q\u001a\u00020\u0003H\u00c6\u0003J\t\u0010r\u001a\u00020\u000eH\u00c6\u0003J\t\u0010s\u001a\u00020*H\u00c6\u0003J\t\u0010t\u001a\u00020\u0013H\u00c6\u0003J\t\u0010u\u001a\u00020\u0003H\u00c6\u0003J\t\u0010v\u001a\u00020\u0003H\u00c6\u0003J\t\u0010w\u001a\u00020\tH\u00c6\u0003J\t\u0010x\u001a\u00020\u000bH\u00c6\u0003J\t\u0010y\u001a\u00020\u000bH\u00c6\u0003J\t\u0010z\u001a\u00020\u000eH\u00c6\u0003J\u00f4\u0002\u0010{\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\b\b\u0002\u0010\u0010\u001a\u00020\u000e2\b\b\u0002\u0010\u0011\u001a\u00020\u000e2\b\b\u0002\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010\u0014\u001a\u00020\u00032\b\b\u0002\u0010\u0015\u001a\u00020\u00032\u000e\b\u0002\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u00172\u000e\b\u0002\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00030\u00172\b\b\u0002\u0010\u001a\u001a\u00020\u001b2\b\b\u0002\u0010\u001c\u001a\u00020\u001d2\u000e\b\u0002\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00030\u00172\u000e\b\u0002\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00030\u00172\u000e\b\u0002\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00030\u00172\b\b\u0002\u0010!\u001a\u00020\"2\u000e\b\u0002\u0010#\u001a\b\u0012\u0004\u0012\u00020$0\u00172\b\b\u0002\u0010%\u001a\u00020\u00032\b\b\u0002\u0010&\u001a\u00020\u00032\b\b\u0002\u0010\'\u001a\u00020\u00032\b\b\u0002\u0010(\u001a\u00020\u000b2\b\b\u0002\u0010)\u001a\u00020*2\b\b\u0002\u0010+\u001a\u00020\u000e2\b\b\u0002\u0010,\u001a\u00020*2\b\b\u0002\u0010-\u001a\u00020\u0013H\u00c6\u0001\u00a2\u0006\u0002\u0010|J\u0013\u0010}\u001a\u00020*2\b\u0010~\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u007f\u001a\u00020\u0013H\u00d6\u0001J\n\u0010\u0080\u0001\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u001a\u001a\u00020\u001b\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u00100R\u0011\u0010,\u001a\u00020*\u00a2\u0006\b\n\u0000\u001a\u0004\b1\u00102R\u0017\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u00104R\u0011\u0010\u0011\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b5\u00106R\u0011\u0010)\u001a\u00020*\u00a2\u0006\b\n\u0000\u001a\u0004\b7\u00102R\u0011\u0010(\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u00109R\u0011\u0010\u0010\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b:\u00106R\u0011\u0010\'\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b;\u0010<R\u0011\u0010+\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b=\u00106R\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b>\u0010<R\u0017\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00030\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b?\u00104R\u0011\u0010\f\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b@\u00109R\u0011\u0010-\u001a\u00020\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\bA\u0010BR\u0011\u0010\u0014\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bC\u0010<R\u0011\u0010\u0015\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bD\u0010<R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bE\u0010<R\u0017\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00030\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\bF\u00104R\u0011\u0010!\u001a\u00020\"\u00a2\u0006\b\n\u0000\u001a\u0004\bG\u0010HR\u0011\u0010&\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bI\u0010<R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\bJ\u0010KR\u0015\u0010\u000f\u001a\u0004\u0018\u00010\u000e\u00a2\u0006\n\n\u0002\u0010N\u001a\u0004\bL\u0010MR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bO\u0010<R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bP\u0010<R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\bQ\u00109R\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\bR\u00106R\u0011\u0010\u001c\u001a\u00020\u001d\u00a2\u0006\b\n\u0000\u001a\u0004\bS\u0010TR\u0011\u0010%\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bU\u0010<R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bV\u0010<R\u0011\u0010\u0012\u001a\u00020\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\bW\u0010BR\u0017\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00030\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\bX\u00104R\u0017\u0010#\u001a\b\u0012\u0004\u0012\u00020$0\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\bY\u00104R\u0017\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00030\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\bZ\u00104\u00a8\u0006\u0081\u0001"}, d2 = {"Lcom/example/rooster/core/common/model/Auction;", "", "id", "", "sellerId", "sellerName", "title", "description", "productDetails", "Lcom/example/rooster/core/common/model/Product;", "startTime", "", "endTime", "startingBid", "", "reservePrice", "currentBid", "bidIncrement", "totalBids", "", "highestBidderId", "highestBidderName", "bidHistory", "", "Lcom/example/rooster/core/common/model/Bid;", "watchers", "auctionType", "Lcom/example/rooster/core/common/model/AuctionType;", "status", "Lcom/example/rooster/core/common/model/AuctionStatus;", "images", "videos", "documents", "location", "Lcom/example/rooster/core/common/model/Address;", "viewingSchedule", "Lcom/example/rooster/core/common/model/ViewingSlot;", "terms", "paymentTerms", "deliveryTerms", "createdAt", "bidderDepositRequired", "", "depositPercentage", "autoExtendEnabled", "extensionMinutes", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/example/rooster/core/common/model/Product;JJDLjava/lang/Double;DDILjava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Lcom/example/rooster/core/common/model/AuctionType;Lcom/example/rooster/core/common/model/AuctionStatus;Ljava/util/List;Ljava/util/List;Ljava/util/List;Lcom/example/rooster/core/common/model/Address;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JZDZI)V", "getAuctionType", "()Lcom/example/rooster/core/common/model/AuctionType;", "getAutoExtendEnabled", "()Z", "getBidHistory", "()Ljava/util/List;", "getBidIncrement", "()D", "getBidderDepositRequired", "getCreatedAt", "()J", "getCurrentBid", "getDeliveryTerms", "()Ljava/lang/String;", "getDepositPercentage", "getDescription", "getDocuments", "getEndTime", "getExtensionMinutes", "()I", "getHighestBidderId", "getHighestBidderName", "getId", "getImages", "getLocation", "()Lcom/example/rooster/core/common/model/Address;", "getPaymentTerms", "getProductDetails", "()Lcom/example/rooster/core/common/model/Product;", "getReservePrice", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getSellerId", "getSellerName", "getStartTime", "getStartingBid", "getStatus", "()Lcom/example/rooster/core/common/model/AuctionStatus;", "getTerms", "getTitle", "getTotalBids", "getVideos", "getViewingSchedule", "getWatchers", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component20", "component21", "component22", "component23", "component24", "component25", "component26", "component27", "component28", "component29", "component3", "component30", "component31", "component32", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/example/rooster/core/common/model/Product;JJDLjava/lang/Double;DDILjava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Lcom/example/rooster/core/common/model/AuctionType;Lcom/example/rooster/core/common/model/AuctionStatus;Ljava/util/List;Ljava/util/List;Ljava/util/List;Lcom/example/rooster/core/common/model/Address;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JZDZI)Lcom/example/rooster/core/common/model/Auction;", "equals", "other", "hashCode", "toString", "core-common_debug"})
public final class Auction {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String sellerId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String sellerName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String title = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String description = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.Product productDetails = null;
    private final long startTime = 0L;
    private final long endTime = 0L;
    private final double startingBid = 0.0;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double reservePrice = null;
    private final double currentBid = 0.0;
    private final double bidIncrement = 0.0;
    private final int totalBids = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String highestBidderId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String highestBidderName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.rooster.core.common.model.Bid> bidHistory = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> watchers = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.AuctionType auctionType = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.AuctionStatus status = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> images = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> videos = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> documents = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.Address location = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.rooster.core.common.model.ViewingSlot> viewingSchedule = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String terms = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String paymentTerms = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String deliveryTerms = null;
    private final long createdAt = 0L;
    private final boolean bidderDepositRequired = false;
    private final double depositPercentage = 0.0;
    private final boolean autoExtendEnabled = false;
    private final int extensionMinutes = 0;
    
    public Auction(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String sellerId, @org.jetbrains.annotations.NotNull()
    java.lang.String sellerName, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String description, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.Product productDetails, long startTime, long endTime, double startingBid, @org.jetbrains.annotations.Nullable()
    java.lang.Double reservePrice, double currentBid, double bidIncrement, int totalBids, @org.jetbrains.annotations.NotNull()
    java.lang.String highestBidderId, @org.jetbrains.annotations.NotNull()
    java.lang.String highestBidderName, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.Bid> bidHistory, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> watchers, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.AuctionType auctionType, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.AuctionStatus status, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> images, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> videos, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> documents, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.Address location, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.ViewingSlot> viewingSchedule, @org.jetbrains.annotations.NotNull()
    java.lang.String terms, @org.jetbrains.annotations.NotNull()
    java.lang.String paymentTerms, @org.jetbrains.annotations.NotNull()
    java.lang.String deliveryTerms, long createdAt, boolean bidderDepositRequired, double depositPercentage, boolean autoExtendEnabled, int extensionMinutes) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
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
    public final java.lang.String getTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDescription() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Product getProductDetails() {
        return null;
    }
    
    public final long getStartTime() {
        return 0L;
    }
    
    public final long getEndTime() {
        return 0L;
    }
    
    public final double getStartingBid() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getReservePrice() {
        return null;
    }
    
    public final double getCurrentBid() {
        return 0.0;
    }
    
    public final double getBidIncrement() {
        return 0.0;
    }
    
    public final int getTotalBids() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getHighestBidderId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getHighestBidderName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.Bid> getBidHistory() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getWatchers() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.AuctionType getAuctionType() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.AuctionStatus getStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getImages() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getVideos() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getDocuments() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Address getLocation() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.ViewingSlot> getViewingSchedule() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTerms() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPaymentTerms() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDeliveryTerms() {
        return null;
    }
    
    public final long getCreatedAt() {
        return 0L;
    }
    
    public final boolean getBidderDepositRequired() {
        return false;
    }
    
    public final double getDepositPercentage() {
        return 0.0;
    }
    
    public final boolean getAutoExtendEnabled() {
        return false;
    }
    
    public final int getExtensionMinutes() {
        return 0;
    }
    
    public Auction() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component10() {
        return null;
    }
    
    public final double component11() {
        return 0.0;
    }
    
    public final double component12() {
        return 0.0;
    }
    
    public final int component13() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component14() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component15() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.Bid> component16() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component17() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.AuctionType component18() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.AuctionStatus component19() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component20() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component21() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component22() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Address component23() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.ViewingSlot> component24() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component25() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component26() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component27() {
        return null;
    }
    
    public final long component28() {
        return 0L;
    }
    
    public final boolean component29() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    public final double component30() {
        return 0.0;
    }
    
    public final boolean component31() {
        return false;
    }
    
    public final int component32() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Product component6() {
        return null;
    }
    
    public final long component7() {
        return 0L;
    }
    
    public final long component8() {
        return 0L;
    }
    
    public final double component9() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Auction copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String sellerId, @org.jetbrains.annotations.NotNull()
    java.lang.String sellerName, @org.jetbrains.annotations.NotNull()
    java.lang.String title, @org.jetbrains.annotations.NotNull()
    java.lang.String description, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.Product productDetails, long startTime, long endTime, double startingBid, @org.jetbrains.annotations.Nullable()
    java.lang.Double reservePrice, double currentBid, double bidIncrement, int totalBids, @org.jetbrains.annotations.NotNull()
    java.lang.String highestBidderId, @org.jetbrains.annotations.NotNull()
    java.lang.String highestBidderName, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.Bid> bidHistory, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> watchers, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.AuctionType auctionType, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.AuctionStatus status, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> images, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> videos, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> documents, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.Address location, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.ViewingSlot> viewingSchedule, @org.jetbrains.annotations.NotNull()
    java.lang.String terms, @org.jetbrains.annotations.NotNull()
    java.lang.String paymentTerms, @org.jetbrains.annotations.NotNull()
    java.lang.String deliveryTerms, long createdAt, boolean bidderDepositRequired, double depositPercentage, boolean autoExtendEnabled, int extensionMinutes) {
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