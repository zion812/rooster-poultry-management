package com.example.rooster.feature.farm.domain.model;

/**
 * Registry Requirements by Age Group and Type
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0002J\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0007\u001a\u00020\bJ\u001c\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000bJ\u0016\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\n\u001a\u00020\u000bH\u0002\u00a8\u0006\r"}, d2 = {"Lcom/example/rooster/feature/farm/domain/model/RegistryRequirements;", "", "()V", "getNonTraceableRequirements", "", "", "getOptionalFields", "registryType", "Lcom/example/rooster/feature/farm/domain/model/RegistryType;", "getRequiredFields", "ageGroup", "Lcom/example/rooster/feature/farm/domain/model/AgeGroup;", "getTraceableRequirements", "feature-farm_release"})
public final class RegistryRequirements {
    @org.jetbrains.annotations.NotNull()
    public static final com.example.rooster.feature.farm.domain.model.RegistryRequirements INSTANCE = null;
    
    private RegistryRequirements() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getRequiredFields(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.RegistryType registryType, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.AgeGroup ageGroup) {
        return null;
    }
    
    private final java.util.Set<java.lang.String> getTraceableRequirements(com.example.rooster.feature.farm.domain.model.AgeGroup ageGroup) {
        return null;
    }
    
    private final java.util.Set<java.lang.String> getNonTraceableRequirements() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getOptionalFields(@org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.RegistryType registryType) {
        return null;
    }
}