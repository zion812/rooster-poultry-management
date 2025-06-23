package com.example.rooster.feature.farm.domain.model;

/**
 * Represents an operational update for a fowl or section.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\bH\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u001a\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003JG\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001f\u001a\u00020 H\u00d6\u0001J\t\u0010!\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\t\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014\u00a8\u0006\""}, d2 = {"Lcom/example/rooster/feature/farm/domain/model/UpdateRecord;", "", "id", "", "fowlId", "type", "Lcom/example/rooster/feature/farm/domain/model/UpdateType;", "date", "Ljava/util/Date;", "details", "attachmentUrl", "(Ljava/lang/String;Ljava/lang/String;Lcom/example/rooster/feature/farm/domain/model/UpdateType;Ljava/util/Date;Ljava/lang/String;Ljava/lang/String;)V", "getAttachmentUrl", "()Ljava/lang/String;", "getDate", "()Ljava/util/Date;", "getDetails", "getFowlId", "getId", "getType", "()Lcom/example/rooster/feature/farm/domain/model/UpdateType;", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "", "toString", "feature-farm_release"})
public final class UpdateRecord {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String fowlId = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.model.UpdateType type = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Date date = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String details = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String attachmentUrl = null;
    
    public UpdateRecord(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String fowlId, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.UpdateType type, @org.jetbrains.annotations.NotNull()
    java.util.Date date, @org.jetbrains.annotations.NotNull()
    java.lang.String details, @org.jetbrains.annotations.Nullable()
    java.lang.String attachmentUrl) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFowlId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.UpdateType getType() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date getDate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDetails() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getAttachmentUrl() {
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
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.UpdateType component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.UpdateRecord copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String fowlId, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.UpdateType type, @org.jetbrains.annotations.NotNull()
    java.util.Date date, @org.jetbrains.annotations.NotNull()
    java.lang.String details, @org.jetbrains.annotations.Nullable()
    java.lang.String attachmentUrl) {
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