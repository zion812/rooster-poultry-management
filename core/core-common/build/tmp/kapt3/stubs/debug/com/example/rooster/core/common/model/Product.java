package com.example.rooster.core.common.model;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000x\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010$\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\bR\b\u0087\b\u0018\u00002\u00020\u0001B\u00e7\u0002\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0010\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0010\u0012\u000e\b\u0002\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00030\u0013\u0012\u0014\b\u0002\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u0015\u0012\b\b\u0002\u0010\u0016\u001a\u00020\u0017\u0012\b\b\u0002\u0010\u0018\u001a\u00020\u0019\u0012\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u0019\u0012\u000e\b\u0002\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001c0\u0013\u0012\b\b\u0002\u0010\u001d\u001a\u00020\u001e\u0012\b\b\u0002\u0010\u001f\u001a\u00020\r\u0012\u000e\b\u0002\u0010 \u001a\b\u0012\u0004\u0012\u00020!0\u0013\u0012\b\b\u0002\u0010\"\u001a\u00020#\u0012\b\b\u0002\u0010$\u001a\u00020\r\u0012\b\b\u0002\u0010%\u001a\u00020\r\u0012\u000e\b\u0002\u0010&\u001a\b\u0012\u0004\u0012\u00020\'0\u0013\u0012\b\b\u0002\u0010(\u001a\u00020\u0019\u0012\b\b\u0002\u0010)\u001a\u00020\u0019\u0012\b\b\u0002\u0010*\u001a\u00020+\u0012\b\b\u0002\u0010,\u001a\u00020\u0003\u0012\b\b\u0002\u0010-\u001a\u00020\u0010\u0012\u000e\b\u0002\u0010.\u001a\b\u0012\u0004\u0012\u00020/0\u0013\u00a2\u0006\u0002\u00100J\t\u0010\\\u001a\u00020\u0003H\u00c6\u0003J\t\u0010]\u001a\u00020\u0003H\u00c6\u0003J\t\u0010^\u001a\u00020\u0010H\u00c6\u0003J\t\u0010_\u001a\u00020\u0010H\u00c6\u0003J\u000f\u0010`\u001a\b\u0012\u0004\u0012\u00020\u00030\u0013H\u00c6\u0003J\u0015\u0010a\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u0015H\u00c6\u0003J\t\u0010b\u001a\u00020\u0017H\u00c6\u0003J\t\u0010c\u001a\u00020\u0019H\u00c6\u0003J\u0010\u0010d\u001a\u0004\u0018\u00010\u0019H\u00c6\u0003\u00a2\u0006\u0002\u00104J\u000f\u0010e\u001a\b\u0012\u0004\u0012\u00020\u001c0\u0013H\u00c6\u0003J\t\u0010f\u001a\u00020\u001eH\u00c6\u0003J\t\u0010g\u001a\u00020\u0003H\u00c6\u0003J\t\u0010h\u001a\u00020\rH\u00c6\u0003J\u000f\u0010i\u001a\b\u0012\u0004\u0012\u00020!0\u0013H\u00c6\u0003J\t\u0010j\u001a\u00020#H\u00c6\u0003J\t\u0010k\u001a\u00020\rH\u00c6\u0003J\t\u0010l\u001a\u00020\rH\u00c6\u0003J\u000f\u0010m\u001a\b\u0012\u0004\u0012\u00020\'0\u0013H\u00c6\u0003J\t\u0010n\u001a\u00020\u0019H\u00c6\u0003J\t\u0010o\u001a\u00020\u0019H\u00c6\u0003J\t\u0010p\u001a\u00020+H\u00c6\u0003J\t\u0010q\u001a\u00020\u0003H\u00c6\u0003J\t\u0010r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010s\u001a\u00020\u0010H\u00c6\u0003J\u000f\u0010t\u001a\b\u0012\u0004\u0012\u00020/0\u0013H\u00c6\u0003J\t\u0010u\u001a\u00020\u0003H\u00c6\u0003J\t\u0010v\u001a\u00020\u0003H\u00c6\u0003J\t\u0010w\u001a\u00020\tH\u00c6\u0003J\t\u0010x\u001a\u00020\u0003H\u00c6\u0003J\t\u0010y\u001a\u00020\u0003H\u00c6\u0003J\t\u0010z\u001a\u00020\rH\u00c6\u0003J\u00f0\u0002\u0010{\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u00032\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u00102\u000e\b\u0002\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00030\u00132\u0014\b\u0002\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u00192\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u00192\u000e\b\u0002\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001c0\u00132\b\b\u0002\u0010\u001d\u001a\u00020\u001e2\b\b\u0002\u0010\u001f\u001a\u00020\r2\u000e\b\u0002\u0010 \u001a\b\u0012\u0004\u0012\u00020!0\u00132\b\b\u0002\u0010\"\u001a\u00020#2\b\b\u0002\u0010$\u001a\u00020\r2\b\b\u0002\u0010%\u001a\u00020\r2\u000e\b\u0002\u0010&\u001a\b\u0012\u0004\u0012\u00020\'0\u00132\b\b\u0002\u0010(\u001a\u00020\u00192\b\b\u0002\u0010)\u001a\u00020\u00192\b\b\u0002\u0010*\u001a\u00020+2\b\b\u0002\u0010,\u001a\u00020\u00032\b\b\u0002\u0010-\u001a\u00020\u00102\u000e\b\u0002\u0010.\u001a\b\u0012\u0004\u0012\u00020/0\u0013H\u00c6\u0001\u00a2\u0006\u0002\u0010|J\u0013\u0010}\u001a\u00020+2\b\u0010~\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u007f\u001a\u00020\rH\u00d6\u0001J\n\u0010\u0080\u0001\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0018\u001a\u00020\u0019\u00a2\u0006\b\n\u0000\u001a\u0004\b1\u00102R\u0015\u0010\u001a\u001a\u0004\u0018\u00010\u0019\u00a2\u0006\n\n\u0002\u00105\u001a\u0004\b3\u00104R\u0011\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u00107R\u0017\u0010 \u001a\b\u0012\u0004\u0012\u00020!0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u00109R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b:\u0010;R\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001c0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b<\u00109R\u0011\u0010(\u001a\u00020\u0019\u00a2\u0006\b\n\u0000\u001a\u0004\b=\u00102R\u0017\u0010.\u001a\b\u0012\u0004\u0012\u00020/0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b>\u00109R\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b?\u00107R\u0011\u0010%\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b@\u0010AR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bB\u00107R\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00030\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\bC\u00109R\u0011\u0010*\u001a\u00020+\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010DR\u0011\u0010\u0016\u001a\u00020\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\bE\u0010FR\u0011\u0010\u001f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\bG\u0010AR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bH\u00107R\u0011\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\bI\u0010JR\u0011\u0010\u001d\u001a\u00020\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\bK\u0010LR\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\bM\u0010AR\u0017\u0010&\u001a\b\u0012\u0004\u0012\u00020\'0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\bN\u00109R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bO\u00107R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bP\u00107R\u0011\u0010-\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\bQ\u0010JR\u001d\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\bR\u0010SR\u0011\u0010\"\u001a\u00020#\u00a2\u0006\b\n\u0000\u001a\u0004\bT\u0010UR\u0011\u0010\n\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bV\u00107R\u0011\u0010\u0011\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\bW\u0010JR\u0011\u0010,\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bX\u00107R\u0011\u0010\u000e\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bY\u00107R\u0011\u0010)\u001a\u00020\u0019\u00a2\u0006\b\n\u0000\u001a\u0004\bZ\u00102R\u0011\u0010$\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b[\u0010A\u00a8\u0006\u0081\u0001"}, d2 = {"Lcom/example/rooster/core/common/model/Product;", "", "id", "", "sellerId", "sellerName", "name", "description", "category", "Lcom/example/rooster/core/common/model/ProductCategory;", "subcategory", "breed", "quantity", "", "unit", "pricePerUnit", "", "totalPrice", "images", "", "specifications", "", "location", "Lcom/example/rooster/core/common/model/Address;", "availableFrom", "", "availableUntil", "certifications", "Lcom/example/rooster/core/common/model/Certification;", "qualityGrade", "Lcom/example/rooster/core/common/model/QualityGrade;", "minOrderQuantity", "bulkDiscounts", "Lcom/example/rooster/core/common/model/BulkDiscount;", "status", "Lcom/example/rooster/core/common/model/ProductStatus;", "views", "favorites", "ratings", "Lcom/example/rooster/core/common/model/Rating;", "createdAt", "updatedAt", "isTraceable", "", "traceabilityCode", "sellerRating", "deliveryOptions", "Lcom/example/rooster/core/common/model/DeliveryOption;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/example/rooster/core/common/model/ProductCategory;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;DDLjava/util/List;Ljava/util/Map;Lcom/example/rooster/core/common/model/Address;JLjava/lang/Long;Ljava/util/List;Lcom/example/rooster/core/common/model/QualityGrade;ILjava/util/List;Lcom/example/rooster/core/common/model/ProductStatus;IILjava/util/List;JJZLjava/lang/String;DLjava/util/List;)V", "getAvailableFrom", "()J", "getAvailableUntil", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getBreed", "()Ljava/lang/String;", "getBulkDiscounts", "()Ljava/util/List;", "getCategory", "()Lcom/example/rooster/core/common/model/ProductCategory;", "getCertifications", "getCreatedAt", "getDeliveryOptions", "getDescription", "getFavorites", "()I", "getId", "getImages", "()Z", "getLocation", "()Lcom/example/rooster/core/common/model/Address;", "getMinOrderQuantity", "getName", "getPricePerUnit", "()D", "getQualityGrade", "()Lcom/example/rooster/core/common/model/QualityGrade;", "getQuantity", "getRatings", "getSellerId", "getSellerName", "getSellerRating", "getSpecifications", "()Ljava/util/Map;", "getStatus", "()Lcom/example/rooster/core/common/model/ProductStatus;", "getSubcategory", "getTotalPrice", "getTraceabilityCode", "getUnit", "getUpdatedAt", "getViews", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component20", "component21", "component22", "component23", "component24", "component25", "component26", "component27", "component28", "component29", "component3", "component30", "component31", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/example/rooster/core/common/model/ProductCategory;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;DDLjava/util/List;Ljava/util/Map;Lcom/example/rooster/core/common/model/Address;JLjava/lang/Long;Ljava/util/List;Lcom/example/rooster/core/common/model/QualityGrade;ILjava/util/List;Lcom/example/rooster/core/common/model/ProductStatus;IILjava/util/List;JJZLjava/lang/String;DLjava/util/List;)Lcom/example/rooster/core/common/model/Product;", "equals", "other", "hashCode", "toString", "core-common_debug"})
public final class Product {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String sellerId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String sellerName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String name = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String description = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.ProductCategory category = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String subcategory = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String breed = null;
    private final int quantity = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String unit = null;
    private final double pricePerUnit = 0.0;
    private final double totalPrice = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> images = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.lang.String> specifications = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.Address location = null;
    private final long availableFrom = 0L;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long availableUntil = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.rooster.core.common.model.Certification> certifications = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.QualityGrade qualityGrade = null;
    private final int minOrderQuantity = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.rooster.core.common.model.BulkDiscount> bulkDiscounts = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.ProductStatus status = null;
    private final int views = 0;
    private final int favorites = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.rooster.core.common.model.Rating> ratings = null;
    private final long createdAt = 0L;
    private final long updatedAt = 0L;
    private final boolean isTraceable = false;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String traceabilityCode = null;
    private final double sellerRating = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.rooster.core.common.model.DeliveryOption> deliveryOptions = null;
    
    public Product(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String sellerId, @org.jetbrains.annotations.NotNull()
    java.lang.String sellerName, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String description, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.ProductCategory category, @org.jetbrains.annotations.NotNull()
    java.lang.String subcategory, @org.jetbrains.annotations.NotNull()
    java.lang.String breed, int quantity, @org.jetbrains.annotations.NotNull()
    java.lang.String unit, double pricePerUnit, double totalPrice, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> images, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> specifications, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.Address location, long availableFrom, @org.jetbrains.annotations.Nullable()
    java.lang.Long availableUntil, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.Certification> certifications, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.QualityGrade qualityGrade, int minOrderQuantity, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.BulkDiscount> bulkDiscounts, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.ProductStatus status, int views, int favorites, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.Rating> ratings, long createdAt, long updatedAt, boolean isTraceable, @org.jetbrains.annotations.NotNull()
    java.lang.String traceabilityCode, double sellerRating, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.DeliveryOption> deliveryOptions) {
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
    public final java.lang.String getName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDescription() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.ProductCategory getCategory() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSubcategory() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getBreed() {
        return null;
    }
    
    public final int getQuantity() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getUnit() {
        return null;
    }
    
    public final double getPricePerUnit() {
        return 0.0;
    }
    
    public final double getTotalPrice() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getImages() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.String> getSpecifications() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Address getLocation() {
        return null;
    }
    
    public final long getAvailableFrom() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getAvailableUntil() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.Certification> getCertifications() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.QualityGrade getQualityGrade() {
        return null;
    }
    
    public final int getMinOrderQuantity() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.BulkDiscount> getBulkDiscounts() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.ProductStatus getStatus() {
        return null;
    }
    
    public final int getViews() {
        return 0;
    }
    
    public final int getFavorites() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.Rating> getRatings() {
        return null;
    }
    
    public final long getCreatedAt() {
        return 0L;
    }
    
    public final long getUpdatedAt() {
        return 0L;
    }
    
    public final boolean isTraceable() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTraceabilityCode() {
        return null;
    }
    
    public final double getSellerRating() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.DeliveryOption> getDeliveryOptions() {
        return null;
    }
    
    public Product() {
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
    
    public final double component11() {
        return 0.0;
    }
    
    public final double component12() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component13() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.String> component14() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Address component15() {
        return null;
    }
    
    public final long component16() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component17() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.Certification> component18() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.QualityGrade component19() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    public final int component20() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.BulkDiscount> component21() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.ProductStatus component22() {
        return null;
    }
    
    public final int component23() {
        return 0;
    }
    
    public final int component24() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.Rating> component25() {
        return null;
    }
    
    public final long component26() {
        return 0L;
    }
    
    public final long component27() {
        return 0L;
    }
    
    public final boolean component28() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component29() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    public final double component30() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.DeliveryOption> component31() {
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
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.ProductCategory component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component8() {
        return null;
    }
    
    public final int component9() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Product copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String sellerId, @org.jetbrains.annotations.NotNull()
    java.lang.String sellerName, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String description, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.ProductCategory category, @org.jetbrains.annotations.NotNull()
    java.lang.String subcategory, @org.jetbrains.annotations.NotNull()
    java.lang.String breed, int quantity, @org.jetbrains.annotations.NotNull()
    java.lang.String unit, double pricePerUnit, double totalPrice, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> images, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> specifications, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.Address location, long availableFrom, @org.jetbrains.annotations.Nullable()
    java.lang.Long availableUntil, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.Certification> certifications, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.QualityGrade qualityGrade, int minOrderQuantity, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.BulkDiscount> bulkDiscounts, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.ProductStatus status, int views, int favorites, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.Rating> ratings, long createdAt, long updatedAt, boolean isTraceable, @org.jetbrains.annotations.NotNull()
    java.lang.String traceabilityCode, double sellerRating, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.DeliveryOption> deliveryOptions) {
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