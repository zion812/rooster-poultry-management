package com.example.rooster.core.common.model;

import kotlinx.serialization.Serializable;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B-\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0007H\u00c6\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u001bH\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001c"}, d2 = {"Lcom/example/rooster/core/common/model/PaymentTerms;", "", "preferredMethod", "Lcom/example/rooster/core/common/model/PaymentMethod;", "creditDays", "", "advancePercentage", "", "penaltyRate", "(Lcom/example/rooster/core/common/model/PaymentMethod;IDD)V", "getAdvancePercentage", "()D", "getCreditDays", "()I", "getPenaltyRate", "getPreferredMethod", "()Lcom/example/rooster/core/common/model/PaymentMethod;", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "toString", "", "core-common_debug"})
public final class PaymentTerms {
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.PaymentMethod preferredMethod = null;
    private final int creditDays = 0;
    private final double advancePercentage = 0.0;
    private final double penaltyRate = 0.0;
    
    public PaymentTerms(@org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.PaymentMethod preferredMethod, int creditDays, double advancePercentage, double penaltyRate) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.PaymentMethod getPreferredMethod() {
        return null;
    }
    
    public final int getCreditDays() {
        return 0;
    }
    
    public final double getAdvancePercentage() {
        return 0.0;
    }
    
    public final double getPenaltyRate() {
        return 0.0;
    }
    
    public PaymentTerms() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.PaymentMethod component1() {
        return null;
    }
    
    public final int component2() {
        return 0;
    }
    
    public final double component3() {
        return 0.0;
    }
    
    public final double component4() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.PaymentTerms copy(@org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.PaymentMethod preferredMethod, int creditDays, double advancePercentage, double penaltyRate) {
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