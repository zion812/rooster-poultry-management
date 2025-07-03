package com.example.rooster.core.common.model;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BK\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\tH\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\fH\u00c6\u0003JO\u0010 \u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\fH\u00c6\u0001J\u0013\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010$\u001a\u00020%H\u00d6\u0001J\t\u0010&\u001a\u00020\u0007H\u00d6\u0001R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u0011\u0010\n\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006\'"}, d2 = {"Lcom/example/rooster/core/common/model/EnvironmentalConditions;", "", "temperature", "", "humidity", "lightHours", "ventilation", "", "airQuality", "Lcom/example/rooster/core/common/model/AirQuality;", "noiseLevel", "recordedAt", "", "(DDDLjava/lang/String;Lcom/example/rooster/core/common/model/AirQuality;DJ)V", "getAirQuality", "()Lcom/example/rooster/core/common/model/AirQuality;", "getHumidity", "()D", "getLightHours", "getNoiseLevel", "getRecordedAt", "()J", "getTemperature", "getVentilation", "()Ljava/lang/String;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "hashCode", "", "toString", "core-common_debug"})
public final class EnvironmentalConditions {
    private final double temperature = 0.0;
    private final double humidity = 0.0;
    private final double lightHours = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String ventilation = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.AirQuality airQuality = null;
    private final double noiseLevel = 0.0;
    private final long recordedAt = 0L;
    
    public EnvironmentalConditions(double temperature, double humidity, double lightHours, @org.jetbrains.annotations.NotNull()
    java.lang.String ventilation, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.AirQuality airQuality, double noiseLevel, long recordedAt) {
        super();
    }
    
    public final double getTemperature() {
        return 0.0;
    }
    
    public final double getHumidity() {
        return 0.0;
    }
    
    public final double getLightHours() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getVentilation() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.AirQuality getAirQuality() {
        return null;
    }
    
    public final double getNoiseLevel() {
        return 0.0;
    }
    
    public final long getRecordedAt() {
        return 0L;
    }
    
    public EnvironmentalConditions() {
        super();
    }
    
    public final double component1() {
        return 0.0;
    }
    
    public final double component2() {
        return 0.0;
    }
    
    public final double component3() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.AirQuality component5() {
        return null;
    }
    
    public final double component6() {
        return 0.0;
    }
    
    public final long component7() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.EnvironmentalConditions copy(double temperature, double humidity, double lightHours, @org.jetbrains.annotations.NotNull()
    java.lang.String ventilation, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.AirQuality airQuality, double noiseLevel, long recordedAt) {
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