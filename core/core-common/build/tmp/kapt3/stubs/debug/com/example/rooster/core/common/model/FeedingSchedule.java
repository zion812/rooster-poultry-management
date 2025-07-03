package com.example.rooster.core.common.model;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001BQ\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00030\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u0005\u0012\b\b\u0002\u0010\r\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u000eJ\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0007H\u00c6\u0003J\u000f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00030\tH\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u000bH\u00c6\u0003J\t\u0010 \u001a\u00020\u0005H\u00c6\u0003J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003JU\u0010\"\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00030\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u00052\b\b\u0002\u0010\r\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010#\u001a\u00020$2\b\u0010%\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010&\u001a\u00020\u0007H\u00d6\u0001J\t\u0010\'\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\f\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0010R\u0011\u0010\r\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0012R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00030\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001a\u00a8\u0006("}, d2 = {"Lcom/example/rooster/core/common/model/FeedingSchedule;", "", "feedType", "", "quantity", "", "frequency", "", "timing", "", "nutritionalInfo", "Lcom/example/rooster/core/common/model/NutritionalInfo;", "cost", "supplier", "(Ljava/lang/String;DILjava/util/List;Lcom/example/rooster/core/common/model/NutritionalInfo;DLjava/lang/String;)V", "getCost", "()D", "getFeedType", "()Ljava/lang/String;", "getFrequency", "()I", "getNutritionalInfo", "()Lcom/example/rooster/core/common/model/NutritionalInfo;", "getQuantity", "getSupplier", "getTiming", "()Ljava/util/List;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "hashCode", "toString", "core-common_debug"})
public final class FeedingSchedule {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String feedType = null;
    private final double quantity = 0.0;
    private final int frequency = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> timing = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.NutritionalInfo nutritionalInfo = null;
    private final double cost = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String supplier = null;
    
    public FeedingSchedule(@org.jetbrains.annotations.NotNull()
    java.lang.String feedType, double quantity, int frequency, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> timing, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.NutritionalInfo nutritionalInfo, double cost, @org.jetbrains.annotations.NotNull()
    java.lang.String supplier) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFeedType() {
        return null;
    }
    
    public final double getQuantity() {
        return 0.0;
    }
    
    public final int getFrequency() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getTiming() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.NutritionalInfo getNutritionalInfo() {
        return null;
    }
    
    public final double getCost() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSupplier() {
        return null;
    }
    
    public FeedingSchedule() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    public final double component2() {
        return 0.0;
    }
    
    public final int component3() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.NutritionalInfo component5() {
        return null;
    }
    
    public final double component6() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.FeedingSchedule copy(@org.jetbrains.annotations.NotNull()
    java.lang.String feedType, double quantity, int frequency, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> timing, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.NutritionalInfo nutritionalInfo, double cost, @org.jetbrains.annotations.NotNull()
    java.lang.String supplier) {
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