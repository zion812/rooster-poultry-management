package com.example.rooster.core.common.model;

import kotlinx.serialization.Serializable;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b!\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001Bs\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u0012\b\b\u0002\u0010\r\u001a\u00020\n\u0012\b\b\u0002\u0010\u000e\u001a\u00020\n\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0010\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0012\u001a\u00020\n\u00a2\u0006\u0002\u0010\u0013J\t\u0010%\u001a\u00020\u0003H\u00c6\u0003J\t\u0010&\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\'\u001a\u00020\nH\u00c6\u0003J\t\u0010(\u001a\u00020\u0003H\u00c6\u0003J\t\u0010)\u001a\u00020\u0006H\u00c6\u0003J\t\u0010*\u001a\u00020\bH\u00c6\u0003J\t\u0010+\u001a\u00020\nH\u00c6\u0003J\t\u0010,\u001a\u00020\fH\u00c6\u0003J\t\u0010-\u001a\u00020\nH\u00c6\u0003J\t\u0010.\u001a\u00020\nH\u00c6\u0003J\t\u0010/\u001a\u00020\u0010H\u00c6\u0003Jw\u00100\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\n2\b\b\u0002\u0010\u000e\u001a\u00020\n2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u00032\b\b\u0002\u0010\u0012\u001a\u00020\nH\u00c6\u0001J\u0013\u00101\u001a\u0002022\b\u00103\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00104\u001a\u00020\bH\u00d6\u0001J\t\u00105\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0011\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0015R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010\r\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001cR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0015R\u0011\u0010\u0012\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001cR\u0011\u0010\u000e\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001c\u00a8\u00066"}, d2 = {"Lcom/example/rooster/core/common/model/ProductionRecord;", "", "id", "", "birdId", "date", "", "eggsProduced", "", "eggWeight", "", "eggGrade", "Lcom/example/rooster/core/common/model/EggGrade;", "feedConsumption", "waterConsumption", "environmentalConditions", "Lcom/example/rooster/core/common/model/EnvironmentalConditions;", "behaviorNotes", "performanceScore", "(Ljava/lang/String;Ljava/lang/String;JIDLcom/example/rooster/core/common/model/EggGrade;DDLcom/example/rooster/core/common/model/EnvironmentalConditions;Ljava/lang/String;D)V", "getBehaviorNotes", "()Ljava/lang/String;", "getBirdId", "getDate", "()J", "getEggGrade", "()Lcom/example/rooster/core/common/model/EggGrade;", "getEggWeight", "()D", "getEggsProduced", "()I", "getEnvironmentalConditions", "()Lcom/example/rooster/core/common/model/EnvironmentalConditions;", "getFeedConsumption", "getId", "getPerformanceScore", "getWaterConsumption", "component1", "component10", "component11", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "toString", "core-common_debug"})
public final class ProductionRecord {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String birdId = null;
    private final long date = 0L;
    private final int eggsProduced = 0;
    private final double eggWeight = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.EggGrade eggGrade = null;
    private final double feedConsumption = 0.0;
    private final double waterConsumption = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.EnvironmentalConditions environmentalConditions = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String behaviorNotes = null;
    private final double performanceScore = 0.0;
    
    public ProductionRecord(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String birdId, long date, int eggsProduced, double eggWeight, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.EggGrade eggGrade, double feedConsumption, double waterConsumption, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.EnvironmentalConditions environmentalConditions, @org.jetbrains.annotations.NotNull()
    java.lang.String behaviorNotes, double performanceScore) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getBirdId() {
        return null;
    }
    
    public final long getDate() {
        return 0L;
    }
    
    public final int getEggsProduced() {
        return 0;
    }
    
    public final double getEggWeight() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.EggGrade getEggGrade() {
        return null;
    }
    
    public final double getFeedConsumption() {
        return 0.0;
    }
    
    public final double getWaterConsumption() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.EnvironmentalConditions getEnvironmentalConditions() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getBehaviorNotes() {
        return null;
    }
    
    public final double getPerformanceScore() {
        return 0.0;
    }
    
    public ProductionRecord() {
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
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    public final long component3() {
        return 0L;
    }
    
    public final int component4() {
        return 0;
    }
    
    public final double component5() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.EggGrade component6() {
        return null;
    }
    
    public final double component7() {
        return 0.0;
    }
    
    public final double component8() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.EnvironmentalConditions component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.ProductionRecord copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String birdId, long date, int eggsProduced, double eggWeight, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.EggGrade eggGrade, double feedConsumption, double waterConsumption, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.EnvironmentalConditions environmentalConditions, @org.jetbrains.annotations.NotNull()
    java.lang.String behaviorNotes, double performanceScore) {
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