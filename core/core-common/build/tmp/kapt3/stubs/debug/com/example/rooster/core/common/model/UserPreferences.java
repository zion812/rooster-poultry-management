package com.example.rooster.core.common.model;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BA\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001b\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u000bH\u00c6\u0003JE\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\u000bH\u00c6\u0001J\u0013\u0010\u001e\u001a\u00020\b2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010 \u001a\u00020!H\u00d6\u0001J\t\u0010\"\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000eR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\t\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0010\u00a8\u0006#"}, d2 = {"Lcom/example/rooster/core/common/model/UserPreferences;", "", "language", "", "currency", "notifications", "Lcom/example/rooster/core/common/model/NotificationPreferences;", "darkMode", "", "offlineMode", "dataSync", "Lcom/example/rooster/core/common/model/DataSyncPreferences;", "(Ljava/lang/String;Ljava/lang/String;Lcom/example/rooster/core/common/model/NotificationPreferences;ZZLcom/example/rooster/core/common/model/DataSyncPreferences;)V", "getCurrency", "()Ljava/lang/String;", "getDarkMode", "()Z", "getDataSync", "()Lcom/example/rooster/core/common/model/DataSyncPreferences;", "getLanguage", "getNotifications", "()Lcom/example/rooster/core/common/model/NotificationPreferences;", "getOfflineMode", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "", "toString", "core-common_debug"})
public final class UserPreferences {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String language = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String currency = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.NotificationPreferences notifications = null;
    private final boolean darkMode = false;
    private final boolean offlineMode = false;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.DataSyncPreferences dataSync = null;
    
    public UserPreferences(@org.jetbrains.annotations.NotNull()
    java.lang.String language, @org.jetbrains.annotations.NotNull()
    java.lang.String currency, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.NotificationPreferences notifications, boolean darkMode, boolean offlineMode, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.DataSyncPreferences dataSync) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getLanguage() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCurrency() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.NotificationPreferences getNotifications() {
        return null;
    }
    
    public final boolean getDarkMode() {
        return false;
    }
    
    public final boolean getOfflineMode() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.DataSyncPreferences getDataSync() {
        return null;
    }
    
    public UserPreferences() {
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
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.NotificationPreferences component3() {
        return null;
    }
    
    public final boolean component4() {
        return false;
    }
    
    public final boolean component5() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.DataSyncPreferences component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.UserPreferences copy(@org.jetbrains.annotations.NotNull()
    java.lang.String language, @org.jetbrains.annotations.NotNull()
    java.lang.String currency, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.NotificationPreferences notifications, boolean darkMode, boolean offlineMode, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.DataSyncPreferences dataSync) {
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