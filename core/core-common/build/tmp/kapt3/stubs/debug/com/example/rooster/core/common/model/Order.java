package com.example.rooster.core.common.model;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\bB\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B\u00f1\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\n\u0012\b\b\u0002\u0010\f\u001a\u00020\n\u0012\b\b\u0002\u0010\r\u001a\u00020\n\u0012\b\b\u0002\u0010\u000e\u001a\u00020\n\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0010\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0012\u0012\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0014\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u0016\u0012\b\b\u0002\u0010\u0017\u001a\u00020\u0018\u0012\b\b\u0002\u0010\u0019\u001a\u00020\u001a\u0012\b\b\u0002\u0010\u001b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u001c\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u001d\u001a\u0004\u0018\u00010\u001e\u0012\b\b\u0002\u0010\u001f\u001a\u00020\u0014\u0012\n\b\u0002\u0010 \u001a\u0004\u0018\u00010\u0014\u0012\b\b\u0002\u0010!\u001a\u00020\u0003\u0012\b\b\u0002\u0010\"\u001a\u00020\n\u00a2\u0006\u0002\u0010#J\t\u0010G\u001a\u00020\u0003H\u00c6\u0003J\t\u0010H\u001a\u00020\nH\u00c6\u0003J\t\u0010I\u001a\u00020\u0010H\u00c6\u0003J\t\u0010J\u001a\u00020\u0012H\u00c6\u0003J\u0010\u0010K\u001a\u0004\u0018\u00010\u0014H\u00c6\u0003\u00a2\u0006\u0002\u0010(J\t\u0010L\u001a\u00020\u0016H\u00c6\u0003J\t\u0010M\u001a\u00020\u0018H\u00c6\u0003J\t\u0010N\u001a\u00020\u001aH\u00c6\u0003J\t\u0010O\u001a\u00020\u0003H\u00c6\u0003J\t\u0010P\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010Q\u001a\u0004\u0018\u00010\u001eH\u00c6\u0003J\t\u0010R\u001a\u00020\u0003H\u00c6\u0003J\t\u0010S\u001a\u00020\u0014H\u00c6\u0003J\u0010\u0010T\u001a\u0004\u0018\u00010\u0014H\u00c6\u0003\u00a2\u0006\u0002\u0010(J\t\u0010U\u001a\u00020\u0003H\u00c6\u0003J\t\u0010V\u001a\u00020\nH\u00c6\u0003J\t\u0010W\u001a\u00020\u0003H\u00c6\u0003J\t\u0010X\u001a\u00020\u0003H\u00c6\u0003J\t\u0010Y\u001a\u00020\bH\u00c6\u0003J\t\u0010Z\u001a\u00020\nH\u00c6\u0003J\t\u0010[\u001a\u00020\nH\u00c6\u0003J\t\u0010\\\u001a\u00020\nH\u00c6\u0003J\t\u0010]\u001a\u00020\nH\u00c6\u0003J\u00fa\u0001\u0010^\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\n2\b\b\u0002\u0010\r\u001a\u00020\n2\b\b\u0002\u0010\u000e\u001a\u00020\n2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u00122\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u00142\b\b\u0002\u0010\u0015\u001a\u00020\u00162\b\b\u0002\u0010\u0017\u001a\u00020\u00182\b\b\u0002\u0010\u0019\u001a\u00020\u001a2\b\b\u0002\u0010\u001b\u001a\u00020\u00032\b\b\u0002\u0010\u001c\u001a\u00020\u00032\n\b\u0002\u0010\u001d\u001a\u0004\u0018\u00010\u001e2\b\b\u0002\u0010\u001f\u001a\u00020\u00142\n\b\u0002\u0010 \u001a\u0004\u0018\u00010\u00142\b\b\u0002\u0010!\u001a\u00020\u00032\b\b\u0002\u0010\"\u001a\u00020\nH\u00c6\u0001\u00a2\u0006\u0002\u0010_J\u0013\u0010`\u001a\u00020a2\b\u0010b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010c\u001a\u00020\bH\u00d6\u0001J\t\u0010d\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u0011\u0010!\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010%R\u0015\u0010 \u001a\u0004\u0018\u00010\u0014\u00a2\u0006\n\n\u0002\u0010)\u001a\u0004\b\'\u0010(R\u0011\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010+R\u0015\u0010\u0013\u001a\u0004\u0018\u00010\u0014\u00a2\u0006\n\n\u0002\u0010)\u001a\u0004\b,\u0010(R\u0011\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010.R\u0011\u0010\f\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u00100R\u0011\u0010\u000e\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b1\u00100R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010%R\u0011\u0010\u001c\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u0010%R\u0011\u0010\u001f\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b4\u00105R\u0011\u0010\u0015\u001a\u00020\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u00107R\u0011\u0010\u0019\u001a\u00020\u001a\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u00109R\u0011\u0010\u0017\u001a\u00020\u0018\u00a2\u0006\b\n\u0000\u001a\u0004\b:\u0010;R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b<\u0010%R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b=\u0010>R\u0011\u0010\"\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b?\u00100R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b@\u0010%R\u0011\u0010\r\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\bA\u00100R\u0011\u0010\u000b\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\bB\u00100R\u0013\u0010\u001d\u001a\u0004\u0018\u00010\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\bC\u0010DR\u0011\u0010\u001b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bE\u0010%R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\bF\u00100\u00a8\u0006e"}, d2 = {"Lcom/example/rooster/core/common/model/Order;", "", "id", "", "buyerId", "sellerId", "productId", "quantity", "", "unitPrice", "", "totalAmount", "discount", "taxes", "finalAmount", "deliveryAddress", "Lcom/example/rooster/core/common/model/Address;", "deliveryType", "Lcom/example/rooster/core/common/model/DeliveryType;", "deliveryDate", "", "orderStatus", "Lcom/example/rooster/core/common/model/OrderStatus;", "paymentStatus", "Lcom/example/rooster/core/common/model/PaymentStatus;", "paymentMethod", "Lcom/example/rooster/core/common/model/PaymentMethod;", "transactionId", "notes", "trackingInfo", "Lcom/example/rooster/core/common/model/TrackingInfo;", "orderDate", "completedDate", "cancellationReason", "refundAmount", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IDDDDDLcom/example/rooster/core/common/model/Address;Lcom/example/rooster/core/common/model/DeliveryType;Ljava/lang/Long;Lcom/example/rooster/core/common/model/OrderStatus;Lcom/example/rooster/core/common/model/PaymentStatus;Lcom/example/rooster/core/common/model/PaymentMethod;Ljava/lang/String;Ljava/lang/String;Lcom/example/rooster/core/common/model/TrackingInfo;JLjava/lang/Long;Ljava/lang/String;D)V", "getBuyerId", "()Ljava/lang/String;", "getCancellationReason", "getCompletedDate", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getDeliveryAddress", "()Lcom/example/rooster/core/common/model/Address;", "getDeliveryDate", "getDeliveryType", "()Lcom/example/rooster/core/common/model/DeliveryType;", "getDiscount", "()D", "getFinalAmount", "getId", "getNotes", "getOrderDate", "()J", "getOrderStatus", "()Lcom/example/rooster/core/common/model/OrderStatus;", "getPaymentMethod", "()Lcom/example/rooster/core/common/model/PaymentMethod;", "getPaymentStatus", "()Lcom/example/rooster/core/common/model/PaymentStatus;", "getProductId", "getQuantity", "()I", "getRefundAmount", "getSellerId", "getTaxes", "getTotalAmount", "getTrackingInfo", "()Lcom/example/rooster/core/common/model/TrackingInfo;", "getTransactionId", "getUnitPrice", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component20", "component21", "component22", "component23", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IDDDDDLcom/example/rooster/core/common/model/Address;Lcom/example/rooster/core/common/model/DeliveryType;Ljava/lang/Long;Lcom/example/rooster/core/common/model/OrderStatus;Lcom/example/rooster/core/common/model/PaymentStatus;Lcom/example/rooster/core/common/model/PaymentMethod;Ljava/lang/String;Ljava/lang/String;Lcom/example/rooster/core/common/model/TrackingInfo;JLjava/lang/Long;Ljava/lang/String;D)Lcom/example/rooster/core/common/model/Order;", "equals", "", "other", "hashCode", "toString", "core-common_debug"})
public final class Order {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String buyerId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String sellerId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String productId = null;
    private final int quantity = 0;
    private final double unitPrice = 0.0;
    private final double totalAmount = 0.0;
    private final double discount = 0.0;
    private final double taxes = 0.0;
    private final double finalAmount = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.Address deliveryAddress = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.DeliveryType deliveryType = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long deliveryDate = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.OrderStatus orderStatus = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.PaymentStatus paymentStatus = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.PaymentMethod paymentMethod = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String transactionId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String notes = null;
    @org.jetbrains.annotations.Nullable()
    private final com.example.rooster.core.common.model.TrackingInfo trackingInfo = null;
    private final long orderDate = 0L;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long completedDate = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String cancellationReason = null;
    private final double refundAmount = 0.0;
    
    public Order(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String buyerId, @org.jetbrains.annotations.NotNull()
    java.lang.String sellerId, @org.jetbrains.annotations.NotNull()
    java.lang.String productId, int quantity, double unitPrice, double totalAmount, double discount, double taxes, double finalAmount, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.Address deliveryAddress, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.DeliveryType deliveryType, @org.jetbrains.annotations.Nullable()
    java.lang.Long deliveryDate, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.OrderStatus orderStatus, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.PaymentStatus paymentStatus, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.PaymentMethod paymentMethod, @org.jetbrains.annotations.NotNull()
    java.lang.String transactionId, @org.jetbrains.annotations.NotNull()
    java.lang.String notes, @org.jetbrains.annotations.Nullable()
    com.example.rooster.core.common.model.TrackingInfo trackingInfo, long orderDate, @org.jetbrains.annotations.Nullable()
    java.lang.Long completedDate, @org.jetbrains.annotations.NotNull()
    java.lang.String cancellationReason, double refundAmount) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getBuyerId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSellerId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getProductId() {
        return null;
    }
    
    public final int getQuantity() {
        return 0;
    }
    
    public final double getUnitPrice() {
        return 0.0;
    }
    
    public final double getTotalAmount() {
        return 0.0;
    }
    
    public final double getDiscount() {
        return 0.0;
    }
    
    public final double getTaxes() {
        return 0.0;
    }
    
    public final double getFinalAmount() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Address getDeliveryAddress() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.DeliveryType getDeliveryType() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getDeliveryDate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.OrderStatus getOrderStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.PaymentStatus getPaymentStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.PaymentMethod getPaymentMethod() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTransactionId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getNotes() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.rooster.core.common.model.TrackingInfo getTrackingInfo() {
        return null;
    }
    
    public final long getOrderDate() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getCompletedDate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCancellationReason() {
        return null;
    }
    
    public final double getRefundAmount() {
        return 0.0;
    }
    
    public Order() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    public final double component10() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Address component11() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.DeliveryType component12() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component13() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.OrderStatus component14() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.PaymentStatus component15() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.PaymentMethod component16() {
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
    public final com.example.rooster.core.common.model.TrackingInfo component19() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    public final long component20() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component21() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component22() {
        return null;
    }
    
    public final double component23() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    public final int component5() {
        return 0;
    }
    
    public final double component6() {
        return 0.0;
    }
    
    public final double component7() {
        return 0.0;
    }
    
    public final double component8() {
        return 0.0;
    }
    
    public final double component9() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Order copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String buyerId, @org.jetbrains.annotations.NotNull()
    java.lang.String sellerId, @org.jetbrains.annotations.NotNull()
    java.lang.String productId, int quantity, double unitPrice, double totalAmount, double discount, double taxes, double finalAmount, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.Address deliveryAddress, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.DeliveryType deliveryType, @org.jetbrains.annotations.Nullable()
    java.lang.Long deliveryDate, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.OrderStatus orderStatus, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.PaymentStatus paymentStatus, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.PaymentMethod paymentMethod, @org.jetbrains.annotations.NotNull()
    java.lang.String transactionId, @org.jetbrains.annotations.NotNull()
    java.lang.String notes, @org.jetbrains.annotations.Nullable()
    com.example.rooster.core.common.model.TrackingInfo trackingInfo, long orderDate, @org.jetbrains.annotations.Nullable()
    java.lang.Long completedDate, @org.jetbrains.annotations.NotNull()
    java.lang.String cancellationReason, double refundAmount) {
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