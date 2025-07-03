package com.example.rooster.core.common.model;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b!\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B\u0085\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u000b\u0012\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000e\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0014\u0012\u000e\b\u0002\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00030\u000e\u00a2\u0006\u0002\u0010\u0016J\t\u0010)\u001a\u00020\u0003H\u00c6\u0003J\t\u0010*\u001a\u00020\u0014H\u00c6\u0003J\u000f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00030\u000eH\u00c6\u0003J\t\u0010,\u001a\u00020\u0005H\u00c6\u0003J\t\u0010-\u001a\u00020\u0007H\u00c6\u0003J\t\u0010.\u001a\u00020\tH\u00c6\u0003J\t\u0010/\u001a\u00020\u000bH\u00c6\u0003J\t\u00100\u001a\u00020\u000bH\u00c6\u0003J\u000f\u00101\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u00c6\u0003J\t\u00102\u001a\u00020\u0003H\u00c6\u0003J\u000f\u00103\u001a\b\u0012\u0004\u0012\u00020\u00120\u000eH\u00c6\u0003J\u0089\u0001\u00104\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u000b2\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\b\b\u0002\u0010\u0010\u001a\u00020\u00032\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000e2\b\b\u0002\u0010\u0013\u001a\u00020\u00142\u000e\b\u0002\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00030\u000eH\u00c6\u0001J\u0013\u00105\u001a\u0002062\b\u00107\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00108\u001a\u00020\u000bH\u00d6\u0001J\t\u00109\u001a\u00020\u0003H\u00d6\u0001R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\f\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0018R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0011\u0010\u0010\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001fR\u0011\u0010\u0013\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010&R\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00030\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\u0018R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u001a\u00a8\u0006:"}, d2 = {"Lcom/example/rooster/core/common/model/FarmProfile;", "", "farmName", "", "farmAddress", "Lcom/example/rooster/core/common/model/Address;", "farmSize", "", "farmType", "Lcom/example/rooster/core/common/model/FarmType;", "totalBirds", "", "establishedYear", "certifications", "", "Lcom/example/rooster/core/common/model/Certification;", "licenseNumber", "facilities", "Lcom/example/rooster/core/common/model/Facility;", "operatingHours", "Lcom/example/rooster/core/common/model/OperatingHours;", "specializations", "(Ljava/lang/String;Lcom/example/rooster/core/common/model/Address;DLcom/example/rooster/core/common/model/FarmType;IILjava/util/List;Ljava/lang/String;Ljava/util/List;Lcom/example/rooster/core/common/model/OperatingHours;Ljava/util/List;)V", "getCertifications", "()Ljava/util/List;", "getEstablishedYear", "()I", "getFacilities", "getFarmAddress", "()Lcom/example/rooster/core/common/model/Address;", "getFarmName", "()Ljava/lang/String;", "getFarmSize", "()D", "getFarmType", "()Lcom/example/rooster/core/common/model/FarmType;", "getLicenseNumber", "getOperatingHours", "()Lcom/example/rooster/core/common/model/OperatingHours;", "getSpecializations", "getTotalBirds", "component1", "component10", "component11", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "toString", "core-common_debug"})
public final class FarmProfile {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String farmName = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.Address farmAddress = null;
    private final double farmSize = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.FarmType farmType = null;
    private final int totalBirds = 0;
    private final int establishedYear = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.rooster.core.common.model.Certification> certifications = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String licenseNumber = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.rooster.core.common.model.Facility> facilities = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.OperatingHours operatingHours = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> specializations = null;
    
    public FarmProfile(@org.jetbrains.annotations.NotNull()
    java.lang.String farmName, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.Address farmAddress, double farmSize, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.FarmType farmType, int totalBirds, int establishedYear, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.Certification> certifications, @org.jetbrains.annotations.NotNull()
    java.lang.String licenseNumber, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.Facility> facilities, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.OperatingHours operatingHours, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> specializations) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFarmName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Address getFarmAddress() {
        return null;
    }
    
    public final double getFarmSize() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.FarmType getFarmType() {
        return null;
    }
    
    public final int getTotalBirds() {
        return 0;
    }
    
    public final int getEstablishedYear() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.Certification> getCertifications() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getLicenseNumber() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.Facility> getFacilities() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.OperatingHours getOperatingHours() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getSpecializations() {
        return null;
    }
    
    public FarmProfile() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.OperatingHours component10() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component11() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.Address component2() {
        return null;
    }
    
    public final double component3() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.FarmType component4() {
        return null;
    }
    
    public final int component5() {
        return 0;
    }
    
    public final int component6() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.Certification> component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.Facility> component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.FarmProfile copy(@org.jetbrains.annotations.NotNull()
    java.lang.String farmName, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.Address farmAddress, double farmSize, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.FarmType farmType, int totalBirds, int establishedYear, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.Certification> certifications, @org.jetbrains.annotations.NotNull()
    java.lang.String licenseNumber, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.Facility> facilities, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.OperatingHours operatingHours, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> specializations) {
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