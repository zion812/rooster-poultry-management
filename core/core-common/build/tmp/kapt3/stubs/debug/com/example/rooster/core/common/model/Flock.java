package com.example.rooster.core.common.model;

import kotlinx.serialization.Serializable;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b.\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B\u00bb\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\n\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\u000e\b\u0002\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0014\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u0014\u0012\b\b\u0002\u0010\u0016\u001a\u00020\u0014\u0012\b\b\u0002\u0010\u0017\u001a\u00020\r\u0012\b\b\u0002\u0010\u0018\u001a\u00020\u0019\u0012\b\b\u0002\u0010\u001a\u001a\u00020\u001b\u0012\u000e\b\u0002\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u0011\u00a2\u0006\u0002\u0010\u001eJ\t\u00109\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010:\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u00c6\u0003J\t\u0010;\u001a\u00020\u0014H\u00c6\u0003J\t\u0010<\u001a\u00020\u0014H\u00c6\u0003J\t\u0010=\u001a\u00020\u0014H\u00c6\u0003J\t\u0010>\u001a\u00020\rH\u00c6\u0003J\t\u0010?\u001a\u00020\u0019H\u00c6\u0003J\t\u0010@\u001a\u00020\u001bH\u00c6\u0003J\u000f\u0010A\u001a\b\u0012\u0004\u0012\u00020\u001d0\u0011H\u00c6\u0003J\t\u0010B\u001a\u00020\u0003H\u00c6\u0003J\t\u0010C\u001a\u00020\u0003H\u00c6\u0003J\t\u0010D\u001a\u00020\u0003H\u00c6\u0003J\t\u0010E\u001a\u00020\bH\u00c6\u0003J\t\u0010F\u001a\u00020\nH\u00c6\u0003J\t\u0010G\u001a\u00020\nH\u00c6\u0003J\t\u0010H\u001a\u00020\rH\u00c6\u0003J\t\u0010I\u001a\u00020\u000fH\u00c6\u0003J\u00bf\u0001\u0010J\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\u000e\b\u0002\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\b\b\u0002\u0010\u0013\u001a\u00020\u00142\b\b\u0002\u0010\u0015\u001a\u00020\u00142\b\b\u0002\u0010\u0016\u001a\u00020\u00142\b\b\u0002\u0010\u0017\u001a\u00020\r2\b\b\u0002\u0010\u0018\u001a\u00020\u00192\b\b\u0002\u0010\u001a\u001a\u00020\u001b2\u000e\b\u0002\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u0011H\u00c6\u0001J\u0013\u0010K\u001a\u00020L2\b\u0010M\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010N\u001a\u00020\nH\u00d6\u0001J\t\u0010O\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u000b\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010\u0013\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u0017\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010$R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010&R\u0011\u0010\u001a\u001a\u00020\u001b\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010(R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010&R\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010$R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010.R\u0011\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u00100R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b1\u0010&R\u0011\u0010\u0017\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010*R\u0011\u0010\u0015\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u0010\"R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b4\u0010&R\u0011\u0010\u0016\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b5\u0010\"R\u0011\u0010\u0018\u001a\u00020\u0019\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u00107R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u0010 \u00a8\u0006P"}, d2 = {"Lcom/example/rooster/core/common/model/Flock;", "", "id", "", "name", "farmId", "breed", "flockType", "Lcom/example/rooster/core/common/model/FlockType;", "totalBirds", "", "activeBirds", "establishedDate", "", "housingType", "Lcom/example/rooster/core/common/model/HousingType;", "feedingSchedule", "", "Lcom/example/rooster/core/common/model/FeedingSchedule;", "averageWeight", "", "mortalityRate", "productionRate", "lastHealthCheck", "status", "Lcom/example/rooster/core/common/model/FlockStatus;", "environmentalConditions", "Lcom/example/rooster/core/common/model/EnvironmentalConditions;", "biosecurityMeasures", "Lcom/example/rooster/core/common/model/BiosecurityMeasure;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/example/rooster/core/common/model/FlockType;IIJLcom/example/rooster/core/common/model/HousingType;Ljava/util/List;DDDJLcom/example/rooster/core/common/model/FlockStatus;Lcom/example/rooster/core/common/model/EnvironmentalConditions;Ljava/util/List;)V", "getActiveBirds", "()I", "getAverageWeight", "()D", "getBiosecurityMeasures", "()Ljava/util/List;", "getBreed", "()Ljava/lang/String;", "getEnvironmentalConditions", "()Lcom/example/rooster/core/common/model/EnvironmentalConditions;", "getEstablishedDate", "()J", "getFarmId", "getFeedingSchedule", "getFlockType", "()Lcom/example/rooster/core/common/model/FlockType;", "getHousingType", "()Lcom/example/rooster/core/common/model/HousingType;", "getId", "getLastHealthCheck", "getMortalityRate", "getName", "getProductionRate", "getStatus", "()Lcom/example/rooster/core/common/model/FlockStatus;", "getTotalBirds", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "toString", "core-common_debug"})
public final class Flock {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String name = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String farmId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String breed = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.FlockType flockType = null;
    private final int totalBirds = 0;
    private final int activeBirds = 0;
    private final long establishedDate = 0L;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.HousingType housingType = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.rooster.core.common.model.FeedingSchedule> feedingSchedule = null;
    private final double averageWeight = 0.0;
    private final double mortalityRate = 0.0;
    private final double productionRate = 0.0;
    private final long lastHealthCheck = 0L;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.FlockStatus status = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.EnvironmentalConditions environmentalConditions = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.rooster.core.common.model.BiosecurityMeasure> biosecurityMeasures = null;
    
    public Flock(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String farmId, @org.jetbrains.annotations.NotNull()
    java.lang.String breed, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.FlockType flockType, int totalBirds, int activeBirds, long establishedDate, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.HousingType housingType, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.FeedingSchedule> feedingSchedule, double averageWeight, double mortalityRate, double productionRate, long lastHealthCheck, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.FlockStatus status, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.EnvironmentalConditions environmentalConditions, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.BiosecurityMeasure> biosecurityMeasures) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFarmId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getBreed() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.FlockType getFlockType() {
        return null;
    }
    
    public final int getTotalBirds() {
        return 0;
    }
    
    public final int getActiveBirds() {
        return 0;
    }
    
    public final long getEstablishedDate() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.HousingType getHousingType() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.FeedingSchedule> getFeedingSchedule() {
        return null;
    }
    
    public final double getAverageWeight() {
        return 0.0;
    }
    
    public final double getMortalityRate() {
        return 0.0;
    }
    
    public final double getProductionRate() {
        return 0.0;
    }
    
    public final long getLastHealthCheck() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.FlockStatus getStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.EnvironmentalConditions getEnvironmentalConditions() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.BiosecurityMeasure> getBiosecurityMeasures() {
        return null;
    }
    
    public Flock() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.FeedingSchedule> component10() {
        return null;
    }
    
    public final double component11() {
        return 0.0;
    }
    
    public final double component12() {
        return 0.0;
    }
    
    public final double component13() {
        return 0.0;
    }
    
    public final long component14() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.FlockStatus component15() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.EnvironmentalConditions component16() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.BiosecurityMeasure> component17() {
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
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.FlockType component5() {
        return null;
    }
    
    public final int component6() {
        return 0;
    }
    
    public final int component7() {
        return 0;
    }
    
    public final long component8() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.HousingType component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Flock copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String farmId, @org.jetbrains.annotations.NotNull()
    java.lang.String breed, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.FlockType flockType, int totalBirds, int activeBirds, long establishedDate, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.HousingType housingType, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.FeedingSchedule> feedingSchedule, double averageWeight, double mortalityRate, double productionRate, long lastHealthCheck, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.FlockStatus status, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.EnvironmentalConditions environmentalConditions, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.BiosecurityMeasure> biosecurityMeasures) {
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