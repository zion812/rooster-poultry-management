package com.example.rooster.feature.farm.domain.model;

/**
 * Represents comprehensive sensor readings from the farm's IoT devices.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BO\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0006\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0006\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0006\u0012\u0006\u0010\u000b\u001a\u00020\f\u00a2\u0006\u0002\u0010\rJ\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000fJ\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000fJ\u0010\u0010\u001e\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000fJ\u0010\u0010\u001f\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000fJ\u0010\u0010 \u001a\u0004\u0018\u00010\u0006H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000fJ\t\u0010!\u001a\u00020\fH\u00c6\u0003Jh\u0010\"\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00062\b\b\u0002\u0010\u000b\u001a\u00020\fH\u00c6\u0001\u00a2\u0006\u0002\u0010#J\u0013\u0010$\u001a\u00020%2\b\u0010&\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\'\u001a\u00020(H\u00d6\u0001J\t\u0010)\u001a\u00020\u0003H\u00d6\u0001R\u0015\u0010\b\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0015\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u0013\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0012R\u0015\u0010\t\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u0015\u0010\u000fR\u0015\u0010\n\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u0016\u0010\u000fR\u0015\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u0017\u0010\u000fR\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019\u00a8\u0006*"}, d2 = {"Lcom/example/rooster/feature/farm/domain/model/SensorData;", "", "id", "", "deviceId", "temperature", "", "humidity", "airQuality", "lightLevel", "noiseLevel", "timestamp", "Ljava/util/Date;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Float;Ljava/lang/Float;Ljava/lang/Float;Ljava/lang/Float;Ljava/lang/Float;Ljava/util/Date;)V", "getAirQuality", "()Ljava/lang/Float;", "Ljava/lang/Float;", "getDeviceId", "()Ljava/lang/String;", "getHumidity", "getId", "getLightLevel", "getNoiseLevel", "getTemperature", "getTimestamp", "()Ljava/util/Date;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Float;Ljava/lang/Float;Ljava/lang/Float;Ljava/lang/Float;Ljava/lang/Float;Ljava/util/Date;)Lcom/example/rooster/feature/farm/domain/model/SensorData;", "equals", "", "other", "hashCode", "", "toString", "feature-farm_staging"})
public final class SensorData {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String deviceId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Float temperature = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Float humidity = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Float airQuality = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Float lightLevel = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Float noiseLevel = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Date timestamp = null;
    
    public SensorData(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String deviceId, @org.jetbrains.annotations.Nullable()
    java.lang.Float temperature, @org.jetbrains.annotations.Nullable()
    java.lang.Float humidity, @org.jetbrains.annotations.Nullable()
    java.lang.Float airQuality, @org.jetbrains.annotations.Nullable()
    java.lang.Float lightLevel, @org.jetbrains.annotations.Nullable()
    java.lang.Float noiseLevel, @org.jetbrains.annotations.NotNull()
    java.util.Date timestamp) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDeviceId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float getTemperature() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float getHumidity() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float getAirQuality() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float getLightLevel() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float getNoiseLevel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date getTimestamp() {
        return null;
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
    public final java.lang.Float component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.SensorData copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String deviceId, @org.jetbrains.annotations.Nullable()
    java.lang.Float temperature, @org.jetbrains.annotations.Nullable()
    java.lang.Float humidity, @org.jetbrains.annotations.Nullable()
    java.lang.Float airQuality, @org.jetbrains.annotations.Nullable()
    java.lang.Float lightLevel, @org.jetbrains.annotations.Nullable()
    java.lang.Float noiseLevel, @org.jetbrains.annotations.NotNull()
    java.util.Date timestamp) {
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