package com.example.rooster.core.common.model;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b-\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u00bd\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00030\r\u0012\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\r\u0012\u000e\b\u0002\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\r\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0013\u0012\b\b\u0002\u0010\u0014\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0016\u001a\u00020\u0017\u0012\u000e\b\u0002\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00030\r\u0012\b\b\u0002\u0010\u0019\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u001a\u001a\u00020\t\u00a2\u0006\u0002\u0010\u001bJ\t\u00103\u001a\u00020\u0003H\u00c6\u0003J\t\u00104\u001a\u00020\u0013H\u00c6\u0003J\t\u00105\u001a\u00020\u0003H\u00c6\u0003J\t\u00106\u001a\u00020\u0003H\u00c6\u0003J\t\u00107\u001a\u00020\u0017H\u00c6\u0003J\u000f\u00108\u001a\b\u0012\u0004\u0012\u00020\u00030\rH\u00c6\u0003J\t\u00109\u001a\u00020\u0003H\u00c6\u0003J\t\u0010:\u001a\u00020\tH\u00c6\u0003J\t\u0010;\u001a\u00020\u0003H\u00c6\u0003J\t\u0010<\u001a\u00020\u0003H\u00c6\u0003J\t\u0010=\u001a\u00020\u0007H\u00c6\u0003J\t\u0010>\u001a\u00020\tH\u00c6\u0003J\t\u0010?\u001a\u00020\u000bH\u00c6\u0003J\u000f\u0010@\u001a\b\u0012\u0004\u0012\u00020\u00030\rH\u00c6\u0003J\u000f\u0010A\u001a\b\u0012\u0004\u0012\u00020\u000f0\rH\u00c6\u0003J\u000f\u0010B\u001a\b\u0012\u0004\u0012\u00020\u00110\rH\u00c6\u0003J\u00c1\u0001\u0010C\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00030\r2\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\r2\u000e\b\u0002\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\r2\b\b\u0002\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010\u0014\u001a\u00020\u00032\b\b\u0002\u0010\u0015\u001a\u00020\u00032\b\b\u0002\u0010\u0016\u001a\u00020\u00172\u000e\b\u0002\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00030\r2\b\b\u0002\u0010\u0019\u001a\u00020\u00032\b\b\u0002\u0010\u001a\u001a\u00020\tH\u00c6\u0001J\u0013\u0010D\u001a\u00020E2\b\u0010F\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010G\u001a\u00020HH\u00d6\u0001J\t\u0010I\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\u0015\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001dR\u0011\u0010\u0014\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001dR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00030\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010#R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\u001dR\u0011\u0010\u001a\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010%R\u0011\u0010\u0016\u001a\u00020\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u0011\u0010\u0019\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\u001dR\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00030\r\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010#R\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\r\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010#R\u0011\u0010\u0012\u001a\u00020\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010/R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b0\u0010\u001dR\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b1\u00102\u00a8\u0006J"}, d2 = {"Lcom/example/rooster/core/common/model/Bird;", "", "id", "", "tagId", "breed", "gender", "Lcom/example/rooster/core/common/model/Gender;", "hatchDate", "", "weight", "", "parentBirdIds", "", "healthRecords", "Lcom/example/rooster/core/common/model/HealthRecord;", "productionRecords", "Lcom/example/rooster/core/common/model/ProductionRecord;", "status", "Lcom/example/rooster/core/common/model/BirdStatus;", "flockId", "currentLocation", "lineage", "Lcom/example/rooster/core/common/model/LineageInfo;", "geneticMarkers", "microchipId", "lastUpdated", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/example/rooster/core/common/model/Gender;JDLjava/util/List;Ljava/util/List;Ljava/util/List;Lcom/example/rooster/core/common/model/BirdStatus;Ljava/lang/String;Ljava/lang/String;Lcom/example/rooster/core/common/model/LineageInfo;Ljava/util/List;Ljava/lang/String;J)V", "getBreed", "()Ljava/lang/String;", "getCurrentLocation", "getFlockId", "getGender", "()Lcom/example/rooster/core/common/model/Gender;", "getGeneticMarkers", "()Ljava/util/List;", "getHatchDate", "()J", "getHealthRecords", "getId", "getLastUpdated", "getLineage", "()Lcom/example/rooster/core/common/model/LineageInfo;", "getMicrochipId", "getParentBirdIds", "getProductionRecords", "getStatus", "()Lcom/example/rooster/core/common/model/BirdStatus;", "getTagId", "getWeight", "()D", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "", "toString", "core-common_debug"})
public final class Bird {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String tagId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String breed = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.Gender gender = null;
    private final long hatchDate = 0L;
    private final double weight = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> parentBirdIds = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.rooster.core.common.model.HealthRecord> healthRecords = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.rooster.core.common.model.ProductionRecord> productionRecords = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.BirdStatus status = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String flockId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String currentLocation = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.LineageInfo lineage = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> geneticMarkers = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String microchipId = null;
    private final long lastUpdated = 0L;
    
    public Bird(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String tagId, @org.jetbrains.annotations.NotNull()
    java.lang.String breed, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.Gender gender, long hatchDate, double weight, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> parentBirdIds, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.HealthRecord> healthRecords, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.ProductionRecord> productionRecords, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.BirdStatus status, @org.jetbrains.annotations.NotNull()
    java.lang.String flockId, @org.jetbrains.annotations.NotNull()
    java.lang.String currentLocation, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.LineageInfo lineage, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> geneticMarkers, @org.jetbrains.annotations.NotNull()
    java.lang.String microchipId, long lastUpdated) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTagId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getBreed() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Gender getGender() {
        return null;
    }
    
    public final long getHatchDate() {
        return 0L;
    }
    
    public final double getWeight() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getParentBirdIds() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.HealthRecord> getHealthRecords() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.ProductionRecord> getProductionRecords() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.BirdStatus getStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFlockId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCurrentLocation() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.LineageInfo getLineage() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getGeneticMarkers() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getMicrochipId() {
        return null;
    }
    
    public final long getLastUpdated() {
        return 0L;
    }
    
    public Bird() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.BirdStatus component10() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component11() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component12() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.LineageInfo component13() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component14() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component15() {
        return null;
    }
    
    public final long component16() {
        return 0L;
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
    public final com.example.rooster.core.common.model.Gender component4() {
        return null;
    }
    
    public final long component5() {
        return 0L;
    }
    
    public final double component6() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.HealthRecord> component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.ProductionRecord> component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Bird copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String tagId, @org.jetbrains.annotations.NotNull()
    java.lang.String breed, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.Gender gender, long hatchDate, double weight, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> parentBirdIds, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.HealthRecord> healthRecords, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.ProductionRecord> productionRecords, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.BirdStatus status, @org.jetbrains.annotations.NotNull()
    java.lang.String flockId, @org.jetbrains.annotations.NotNull()
    java.lang.String currentLocation, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.LineageInfo lineage, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> geneticMarkers, @org.jetbrains.annotations.NotNull()
    java.lang.String microchipId, long lastUpdated) {
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