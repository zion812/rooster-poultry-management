package com.example.rooster.feature.farm.domain.model;

/**
 * Enhanced Flock Registration Data for Traceable/Non-Traceable Registry System
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b:\b\u0087\b\u0018\u00002\u00020\u0001B\u00cf\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\t\u001a\u0004\u0018\u00010\n\u0012\u000e\u0010\u000b\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\f\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u000e\u0012\b\u0010\u000f\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u0012\b\u0010\u0012\u001a\u0004\u0018\u00010\u0003\u0012\u000e\u0010\u0013\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\f\u0012\b\u0010\u0014\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0015\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0016\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018\u0012\u000e\u0010\u0019\u001a\n\u0012\u0004\u0012\u00020\u001a\u0018\u00010\f\u0012\b\u0010\u001b\u001a\u0004\u0018\u00010\n\u0012\b\b\u0002\u0010\u001c\u001a\u00020\u001d\u0012\b\u0010\u001e\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u001fJ\t\u0010>\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010?\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0011\u0010@\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\fH\u00c6\u0003J\u000b\u0010A\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010B\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010C\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010D\u001a\u0004\u0018\u00010\u0018H\u00c6\u0003J\u0011\u0010E\u001a\n\u0012\u0004\u0012\u00020\u001a\u0018\u00010\fH\u00c6\u0003J\u0010\u0010F\u001a\u0004\u0018\u00010\nH\u00c6\u0003\u00a2\u0006\u0002\u0010,J\t\u0010G\u001a\u00020\u001dH\u00c6\u0003J\u000b\u0010H\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010I\u001a\u00020\u0005H\u00c6\u0003J\t\u0010J\u001a\u00020\u0007H\u00c6\u0003J\u000b\u0010K\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0010\u0010L\u001a\u0004\u0018\u00010\nH\u00c6\u0003\u00a2\u0006\u0002\u0010,J\u0011\u0010M\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\fH\u00c6\u0003J\u000b\u0010N\u001a\u0004\u0018\u00010\u000eH\u00c6\u0003J\u000b\u0010O\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0010\u0010P\u001a\u0004\u0018\u00010\u0011H\u00c6\u0003\u00a2\u0006\u0002\u00108J\u00fc\u0001\u0010Q\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n2\u0010\b\u0002\u0010\u000b\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\f2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00112\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u00032\u0010\b\u0002\u0010\u0013\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\f2\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0017\u001a\u0004\u0018\u00010\u00182\u0010\b\u0002\u0010\u0019\u001a\n\u0012\u0004\u0012\u00020\u001a\u0018\u00010\f2\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\n2\b\b\u0002\u0010\u001c\u001a\u00020\u001d2\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001\u00a2\u0006\u0002\u0010RJ\u0013\u0010S\u001a\u00020\u001d2\b\u0010T\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010U\u001a\u00020\u0011H\u00d6\u0001J\t\u0010V\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0019\u0010\u000b\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\f\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u0013\u0010\u0017\u001a\u0004\u0018\u00010\u0018\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\'R\u0013\u0010\u0014\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010#R\u0013\u0010\r\u001a\u0004\u0018\u00010\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u0015\u0010\u001b\u001a\u0004\u0018\u00010\n\u00a2\u0006\n\n\u0002\u0010-\u001a\u0004\b+\u0010,R\u0013\u0010\u000f\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010#R\u0013\u0010\u0015\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u0010#R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b0\u0010#R\u0013\u0010\u0016\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b1\u0010#R\u0019\u0010\u0013\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\f\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010%R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u00104R\u0011\u0010\u001c\u001a\u00020\u001d\u00a2\u0006\b\n\u0000\u001a\u0004\b5\u00106R\u0015\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u00a2\u0006\n\n\u0002\u00109\u001a\u0004\b7\u00108R\u0013\u0010\u0012\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b:\u0010#R\u0019\u0010\u0019\u001a\n\u0012\u0004\u0012\u00020\u001a\u0018\u00010\f\u00a2\u0006\b\n\u0000\u001a\u0004\b;\u0010%R\u0013\u0010\u001e\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b<\u0010#R\u0015\u0010\t\u001a\u0004\u0018\u00010\n\u00a2\u0006\n\n\u0002\u0010-\u001a\u0004\b=\u0010,\u00a8\u0006W"}, d2 = {"Lcom/example/rooster/feature/farm/domain/model/FlockRegistrationData;", "", "ownerId", "", "registryType", "Lcom/example/rooster/feature/farm/domain/model/RegistryType;", "ageGroup", "Lcom/example/rooster/feature/farm/domain/model/AgeGroup;", "breed", "weight", "", "colors", "", "gender", "Lcom/example/rooster/feature/farm/domain/model/Gender;", "identification", "size", "", "specialty", "proofs", "fatherId", "motherId", "placeOfBirth", "dateOfBirth", "Ljava/util/Date;", "vaccinationRecords", "Lcom/example/rooster/feature/farm/domain/model/VaccinationRecord;", "height", "requiresVerification", "", "verificationNotes", "(Ljava/lang/String;Lcom/example/rooster/feature/farm/domain/model/RegistryType;Lcom/example/rooster/feature/farm/domain/model/AgeGroup;Ljava/lang/String;Ljava/lang/Double;Ljava/util/List;Lcom/example/rooster/feature/farm/domain/model/Gender;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/Date;Ljava/util/List;Ljava/lang/Double;ZLjava/lang/String;)V", "getAgeGroup", "()Lcom/example/rooster/feature/farm/domain/model/AgeGroup;", "getBreed", "()Ljava/lang/String;", "getColors", "()Ljava/util/List;", "getDateOfBirth", "()Ljava/util/Date;", "getFatherId", "getGender", "()Lcom/example/rooster/feature/farm/domain/model/Gender;", "getHeight", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getIdentification", "getMotherId", "getOwnerId", "getPlaceOfBirth", "getProofs", "getRegistryType", "()Lcom/example/rooster/feature/farm/domain/model/RegistryType;", "getRequiresVerification", "()Z", "getSize", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getSpecialty", "getVaccinationRecords", "getVerificationNotes", "getWeight", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Lcom/example/rooster/feature/farm/domain/model/RegistryType;Lcom/example/rooster/feature/farm/domain/model/AgeGroup;Ljava/lang/String;Ljava/lang/Double;Ljava/util/List;Lcom/example/rooster/feature/farm/domain/model/Gender;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/Date;Ljava/util/List;Ljava/lang/Double;ZLjava/lang/String;)Lcom/example/rooster/feature/farm/domain/model/FlockRegistrationData;", "equals", "other", "hashCode", "toString", "feature-farm_release"})
public final class FlockRegistrationData {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String ownerId = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.model.RegistryType registryType = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.model.AgeGroup ageGroup = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String breed = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double weight = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.List<java.lang.String> colors = null;
    @org.jetbrains.annotations.Nullable()
    private final com.example.rooster.feature.farm.domain.model.Gender gender = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String identification = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer size = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String specialty = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.List<java.lang.String> proofs = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String fatherId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String motherId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String placeOfBirth = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.Date dateOfBirth = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.List<com.example.rooster.feature.farm.domain.model.VaccinationRecord> vaccinationRecords = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double height = null;
    private final boolean requiresVerification = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String verificationNotes = null;
    
    public FlockRegistrationData(@org.jetbrains.annotations.NotNull()
    java.lang.String ownerId, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.RegistryType registryType, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.AgeGroup ageGroup, @org.jetbrains.annotations.Nullable()
    java.lang.String breed, @org.jetbrains.annotations.Nullable()
    java.lang.Double weight, @org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> colors, @org.jetbrains.annotations.Nullable()
    com.example.rooster.feature.farm.domain.model.Gender gender, @org.jetbrains.annotations.Nullable()
    java.lang.String identification, @org.jetbrains.annotations.Nullable()
    java.lang.Integer size, @org.jetbrains.annotations.Nullable()
    java.lang.String specialty, @org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> proofs, @org.jetbrains.annotations.Nullable()
    java.lang.String fatherId, @org.jetbrains.annotations.Nullable()
    java.lang.String motherId, @org.jetbrains.annotations.Nullable()
    java.lang.String placeOfBirth, @org.jetbrains.annotations.Nullable()
    java.util.Date dateOfBirth, @org.jetbrains.annotations.Nullable()
    java.util.List<com.example.rooster.feature.farm.domain.model.VaccinationRecord> vaccinationRecords, @org.jetbrains.annotations.Nullable()
    java.lang.Double height, boolean requiresVerification, @org.jetbrains.annotations.Nullable()
    java.lang.String verificationNotes) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOwnerId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.RegistryType getRegistryType() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.AgeGroup getAgeGroup() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getBreed() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getWeight() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.String> getColors() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.rooster.feature.farm.domain.model.Gender getGender() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getIdentification() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getSize() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSpecialty() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.String> getProofs() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getFatherId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getMotherId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPlaceOfBirth() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date getDateOfBirth() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.example.rooster.feature.farm.domain.model.VaccinationRecord> getVaccinationRecords() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getHeight() {
        return null;
    }
    
    public final boolean getRequiresVerification() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getVerificationNotes() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component10() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.String> component11() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component12() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component13() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component14() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date component15() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.example.rooster.feature.farm.domain.model.VaccinationRecord> component16() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component17() {
        return null;
    }
    
    public final boolean component18() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component19() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.RegistryType component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.AgeGroup component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.String> component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.rooster.feature.farm.domain.model.Gender component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.FlockRegistrationData copy(@org.jetbrains.annotations.NotNull()
    java.lang.String ownerId, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.RegistryType registryType, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.AgeGroup ageGroup, @org.jetbrains.annotations.Nullable()
    java.lang.String breed, @org.jetbrains.annotations.Nullable()
    java.lang.Double weight, @org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> colors, @org.jetbrains.annotations.Nullable()
    com.example.rooster.feature.farm.domain.model.Gender gender, @org.jetbrains.annotations.Nullable()
    java.lang.String identification, @org.jetbrains.annotations.Nullable()
    java.lang.Integer size, @org.jetbrains.annotations.Nullable()
    java.lang.String specialty, @org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> proofs, @org.jetbrains.annotations.Nullable()
    java.lang.String fatherId, @org.jetbrains.annotations.Nullable()
    java.lang.String motherId, @org.jetbrains.annotations.Nullable()
    java.lang.String placeOfBirth, @org.jetbrains.annotations.Nullable()
    java.util.Date dateOfBirth, @org.jetbrains.annotations.Nullable()
    java.util.List<com.example.rooster.feature.farm.domain.model.VaccinationRecord> vaccinationRecords, @org.jetbrains.annotations.Nullable()
    java.lang.Double height, boolean requiresVerification, @org.jetbrains.annotations.Nullable()
    java.lang.String verificationNotes) {
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