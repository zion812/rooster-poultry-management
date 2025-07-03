package com.example.rooster.core.common.model;

import kotlinx.serialization.Serializable;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BS\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\f\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010\u001b\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0014J\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0003H\u00c6\u0003J\\\u0010 \u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00032\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0002\u0010\u000b\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\u0003H\u00c6\u0001\u00a2\u0006\u0002\u0010!J\u0013\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010%\u001a\u00020&H\u00d6\u0001J\t\u0010\'\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000fR\u0015\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\n\n\u0002\u0010\u0015\u001a\u0004\b\u0013\u0010\u0014R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u000f\u00a8\u0006("}, d2 = {"Lcom/example/rooster/core/common/model/TrackingInfo;", "", "trackingNumber", "", "carrier", "estimatedDelivery", "", "currentStatus", "statusHistory", "", "Lcom/example/rooster/core/common/model/StatusUpdate;", "currentLocation", "deliveryInstructions", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;)V", "getCarrier", "()Ljava/lang/String;", "getCurrentLocation", "getCurrentStatus", "getDeliveryInstructions", "getEstimatedDelivery", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getStatusHistory", "()Ljava/util/List;", "getTrackingNumber", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;)Lcom/example/rooster/core/common/model/TrackingInfo;", "equals", "", "other", "hashCode", "", "toString", "core-common_debug"})
public final class TrackingInfo {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String trackingNumber = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String carrier = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long estimatedDelivery = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String currentStatus = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.rooster.core.common.model.StatusUpdate> statusHistory = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String currentLocation = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String deliveryInstructions = null;
    
    public TrackingInfo(@org.jetbrains.annotations.NotNull()
    java.lang.String trackingNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String carrier, @org.jetbrains.annotations.Nullable()
    java.lang.Long estimatedDelivery, @org.jetbrains.annotations.NotNull()
    java.lang.String currentStatus, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.StatusUpdate> statusHistory, @org.jetbrains.annotations.NotNull()
    java.lang.String currentLocation, @org.jetbrains.annotations.NotNull()
    java.lang.String deliveryInstructions) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTrackingNumber() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCarrier() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getEstimatedDelivery() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCurrentStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.StatusUpdate> getStatusHistory() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCurrentLocation() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDeliveryInstructions() {
        return null;
    }
    
    public TrackingInfo() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.StatusUpdate> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.TrackingInfo copy(@org.jetbrains.annotations.NotNull()
    java.lang.String trackingNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String carrier, @org.jetbrains.annotations.Nullable()
    java.lang.Long estimatedDelivery, @org.jetbrains.annotations.NotNull()
    java.lang.String currentStatus, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.StatusUpdate> statusHistory, @org.jetbrains.annotations.NotNull()
    java.lang.String currentLocation, @org.jetbrains.annotations.NotNull()
    java.lang.String deliveryInstructions) {
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