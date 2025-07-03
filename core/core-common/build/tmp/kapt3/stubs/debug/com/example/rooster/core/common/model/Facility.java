package com.example.rooster.core.common.model;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B1\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\bH\u00c6\u0003J\t\u0010\u0019\u001a\u00020\nH\u00c6\u0003J;\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\nH\u00c6\u0001J\u0013\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001e\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u001f\u001a\u00020\bH\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\rR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014\u00a8\u0006 "}, d2 = {"Lcom/example/rooster/core/common/model/Facility;", "", "type", "Lcom/example/rooster/core/common/model/FacilityType;", "capacity", "", "currentOccupancy", "condition", "", "lastMaintenance", "", "(Lcom/example/rooster/core/common/model/FacilityType;IILjava/lang/String;J)V", "getCapacity", "()I", "getCondition", "()Ljava/lang/String;", "getCurrentOccupancy", "getLastMaintenance", "()J", "getType", "()Lcom/example/rooster/core/common/model/FacilityType;", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "toString", "core-common_debug"})
public final class Facility {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.FacilityType type = null;
    private final int capacity = 0;
    private final int currentOccupancy = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String condition = null;
    private final long lastMaintenance = 0L;
    
    public Facility(@org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.FacilityType type, int capacity, int currentOccupancy, @org.jetbrains.annotations.NotNull()
    java.lang.String condition, long lastMaintenance) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.FacilityType getType() {
        return null;
    }
    
    public final int getCapacity() {
        return 0;
    }
    
    public final int getCurrentOccupancy() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCondition() {
        return null;
    }
    
    public final long getLastMaintenance() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.FacilityType component1() {
        return null;
    }
    
    public final int component2() {
        return 0;
    }
    
    public final int component3() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    public final long component5() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Facility copy(@org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.FacilityType type, int capacity, int currentOccupancy, @org.jetbrains.annotations.NotNull()
    java.lang.String condition, long lastMaintenance) {
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