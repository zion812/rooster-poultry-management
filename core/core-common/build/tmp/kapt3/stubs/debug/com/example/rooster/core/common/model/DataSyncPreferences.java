package com.example.rooster.core.common.model;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B7\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J;\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0017\u001a\u00020\u00032\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001J\t\u0010\u001b\u001a\u00020\u001cH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000b\u00a8\u0006\u001d"}, d2 = {"Lcom/example/rooster/core/common/model/DataSyncPreferences;", "", "autoSync", "", "syncOnWifiOnly", "syncFrequency", "Lcom/example/rooster/core/common/model/SyncFrequency;", "compressImages", "backgroundSync", "(ZZLcom/example/rooster/core/common/model/SyncFrequency;ZZ)V", "getAutoSync", "()Z", "getBackgroundSync", "getCompressImages", "getSyncFrequency", "()Lcom/example/rooster/core/common/model/SyncFrequency;", "getSyncOnWifiOnly", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "", "toString", "", "core-common_debug"})
public final class DataSyncPreferences {
    private final boolean autoSync = false;
    private final boolean syncOnWifiOnly = false;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.SyncFrequency syncFrequency = null;
    private final boolean compressImages = false;
    private final boolean backgroundSync = false;
    
    public DataSyncPreferences(boolean autoSync, boolean syncOnWifiOnly, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.SyncFrequency syncFrequency, boolean compressImages, boolean backgroundSync) {
        super();
    }
    
    public final boolean getAutoSync() {
        return false;
    }
    
    public final boolean getSyncOnWifiOnly() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.SyncFrequency getSyncFrequency() {
        return null;
    }
    
    public final boolean getCompressImages() {
        return false;
    }
    
    public final boolean getBackgroundSync() {
        return false;
    }
    
    public DataSyncPreferences() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    public final boolean component2() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.SyncFrequency component3() {
        return null;
    }
    
    public final boolean component4() {
        return false;
    }
    
    public final boolean component5() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.DataSyncPreferences copy(boolean autoSync, boolean syncOnWifiOnly, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.SyncFrequency syncFrequency, boolean compressImages, boolean backgroundSync) {
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