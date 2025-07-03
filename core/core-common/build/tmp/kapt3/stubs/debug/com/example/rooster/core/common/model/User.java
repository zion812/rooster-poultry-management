package com.example.rooster.core.common.model;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b.\b\u0087\b\u0018\u00002\u00020\u0001B\u00b3\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u000b\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u000b\u0012\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0012\u0012\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0014\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u0016\u0012\b\b\u0002\u0010\u0017\u001a\u00020\u0018\u0012\b\b\u0002\u0010\u0019\u001a\u00020\u000b\u0012\b\b\u0002\u0010\u001a\u001a\u00020\u001b\u00a2\u0006\u0002\u0010\u001cJ\t\u00103\u001a\u00020\u0003H\u00c6\u0003J\t\u00104\u001a\u00020\u000eH\u00c6\u0003J\t\u00105\u001a\u00020\u000bH\u00c6\u0003J\u000b\u00106\u001a\u0004\u0018\u00010\u0012H\u00c6\u0003J\u000b\u00107\u001a\u0004\u0018\u00010\u0014H\u00c6\u0003J\t\u00108\u001a\u00020\u0016H\u00c6\u0003J\t\u00109\u001a\u00020\u0018H\u00c6\u0003J\t\u0010:\u001a\u00020\u000bH\u00c6\u0003J\t\u0010;\u001a\u00020\u001bH\u00c6\u0003J\t\u0010<\u001a\u00020\u0003H\u00c6\u0003J\t\u0010=\u001a\u00020\u0003H\u00c6\u0003J\t\u0010>\u001a\u00020\u0003H\u00c6\u0003J\t\u0010?\u001a\u00020\u0003H\u00c6\u0003J\t\u0010@\u001a\u00020\tH\u00c6\u0003J\t\u0010A\u001a\u00020\u000bH\u00c6\u0003J\t\u0010B\u001a\u00020\u000bH\u00c6\u0003J\t\u0010C\u001a\u00020\u000eH\u00c6\u0003J\u00b7\u0001\u0010D\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u000e2\b\b\u0002\u0010\u0010\u001a\u00020\u000b2\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u00122\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u00142\b\b\u0002\u0010\u0015\u001a\u00020\u00162\b\b\u0002\u0010\u0017\u001a\u00020\u00182\b\b\u0002\u0010\u0019\u001a\u00020\u000b2\b\b\u0002\u0010\u001a\u001a\u00020\u001bH\u00c6\u0001J\u0013\u0010E\u001a\u00020\u000b2\b\u0010F\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010G\u001a\u00020\u0018H\u00d6\u0001J\t\u0010H\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\u0013\u001a\u0004\u0018\u00010\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\"R\u0013\u0010\u0011\u001a\u0004\u0018\u00010\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\"R\u0011\u0010\u0010\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\'R\u0011\u0010\u0019\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\'R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\'R\u0011\u0010\f\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\'R\u0011\u0010\u000f\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010 R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\"R\u0011\u0010\u0015\u001a\u00020\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010+R\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010\"R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010.R\u0011\u0010\u0017\u001a\u00020\u0018\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u00100R\u0011\u0010\u001a\u001a\u00020\u001b\u00a2\u0006\b\n\u0000\u001a\u0004\b1\u00102\u00a8\u0006I"}, d2 = {"Lcom/example/rooster/core/common/model/User;", "", "id", "", "email", "phoneNumber", "displayName", "profileImageUrl", "role", "Lcom/example/rooster/core/common/model/UserRole;", "isEmailVerified", "", "isPhoneVerified", "createdAt", "", "lastLoginAt", "isActive", "farmProfile", "Lcom/example/rooster/core/common/model/FarmProfile;", "buyerProfile", "Lcom/example/rooster/core/common/model/BuyerProfile;", "preferences", "Lcom/example/rooster/core/common/model/UserPreferences;", "tokenBalance", "", "isActivityVerified", "verificationLevel", "Lcom/example/rooster/core/common/model/VerificationLevel;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/example/rooster/core/common/model/UserRole;ZZJJZLcom/example/rooster/core/common/model/FarmProfile;Lcom/example/rooster/core/common/model/BuyerProfile;Lcom/example/rooster/core/common/model/UserPreferences;IZLcom/example/rooster/core/common/model/VerificationLevel;)V", "getBuyerProfile", "()Lcom/example/rooster/core/common/model/BuyerProfile;", "getCreatedAt", "()J", "getDisplayName", "()Ljava/lang/String;", "getEmail", "getFarmProfile", "()Lcom/example/rooster/core/common/model/FarmProfile;", "getId", "()Z", "getLastLoginAt", "getPhoneNumber", "getPreferences", "()Lcom/example/rooster/core/common/model/UserPreferences;", "getProfileImageUrl", "getRole", "()Lcom/example/rooster/core/common/model/UserRole;", "getTokenBalance", "()I", "getVerificationLevel", "()Lcom/example/rooster/core/common/model/VerificationLevel;", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "core-common_debug"})
public final class User {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String email = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String phoneNumber = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String displayName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String profileImageUrl = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.UserRole role = null;
    private final boolean isEmailVerified = false;
    private final boolean isPhoneVerified = false;
    private final long createdAt = 0L;
    private final long lastLoginAt = 0L;
    private final boolean isActive = false;
    @org.jetbrains.annotations.Nullable()
    private final com.example.rooster.core.common.model.FarmProfile farmProfile = null;
    @org.jetbrains.annotations.Nullable()
    private final com.example.rooster.core.common.model.BuyerProfile buyerProfile = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.UserPreferences preferences = null;
    private final int tokenBalance = 0;
    private final boolean isActivityVerified = false;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.VerificationLevel verificationLevel = null;
    
    public User(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String displayName, @org.jetbrains.annotations.NotNull()
    java.lang.String profileImageUrl, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.UserRole role, boolean isEmailVerified, boolean isPhoneVerified, long createdAt, long lastLoginAt, boolean isActive, @org.jetbrains.annotations.Nullable()
    com.example.rooster.core.common.model.FarmProfile farmProfile, @org.jetbrains.annotations.Nullable()
    com.example.rooster.core.common.model.BuyerProfile buyerProfile, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.UserPreferences preferences, int tokenBalance, boolean isActivityVerified, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.VerificationLevel verificationLevel) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getEmail() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPhoneNumber() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDisplayName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getProfileImageUrl() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.UserRole getRole() {
        return null;
    }
    
    public final boolean isEmailVerified() {
        return false;
    }
    
    public final boolean isPhoneVerified() {
        return false;
    }
    
    public final long getCreatedAt() {
        return 0L;
    }
    
    public final long getLastLoginAt() {
        return 0L;
    }
    
    public final boolean isActive() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.rooster.core.common.model.FarmProfile getFarmProfile() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.rooster.core.common.model.BuyerProfile getBuyerProfile() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.UserPreferences getPreferences() {
        return null;
    }
    
    public final int getTokenBalance() {
        return 0;
    }
    
    public final boolean isActivityVerified() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.VerificationLevel getVerificationLevel() {
        return null;
    }
    
    public User() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    public final long component10() {
        return 0L;
    }
    
    public final boolean component11() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.rooster.core.common.model.FarmProfile component12() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.rooster.core.common.model.BuyerProfile component13() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.UserPreferences component14() {
        return null;
    }
    
    public final int component15() {
        return 0;
    }
    
    public final boolean component16() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.VerificationLevel component17() {
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
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.UserRole component6() {
        return null;
    }
    
    public final boolean component7() {
        return false;
    }
    
    public final boolean component8() {
        return false;
    }
    
    public final long component9() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.User copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String displayName, @org.jetbrains.annotations.NotNull()
    java.lang.String profileImageUrl, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.UserRole role, boolean isEmailVerified, boolean isPhoneVerified, long createdAt, long lastLoginAt, boolean isActive, @org.jetbrains.annotations.Nullable()
    com.example.rooster.core.common.model.FarmProfile farmProfile, @org.jetbrains.annotations.Nullable()
    com.example.rooster.core.common.model.BuyerProfile buyerProfile, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.UserPreferences preferences, int tokenBalance, boolean isActivityVerified, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.VerificationLevel verificationLevel) {
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