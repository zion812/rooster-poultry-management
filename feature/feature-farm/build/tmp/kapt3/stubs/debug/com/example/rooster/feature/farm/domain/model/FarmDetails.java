package com.example.rooster.feature.farm.domain.model;

/**
 * Domain model representing comprehensive Farm Details for enterprise traceability
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u0006\n\u0002\b\n\n\u0002\u0010 \n\u0002\bV\b\u0087\b\u0018\u00002\u00020\u0001B\u00b3\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\f\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u0012\b\u0010\u0010\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0011\u001a\u0004\u0018\u00010\b\u0012\b\u0010\u0012\u001a\u0004\u0018\u00010\b\u0012\u0006\u0010\u0013\u001a\u00020\u0014\u0012\u0006\u0010\u0015\u001a\u00020\u0014\u0012\u0006\u0010\u0016\u001a\u00020\u0014\u0012\u0006\u0010\u0017\u001a\u00020\u0014\u0012\u0006\u0010\u0018\u001a\u00020\u0014\u0012\b\u0010\u0019\u001a\u0004\u0018\u00010\b\u0012\u0006\u0010\u001a\u001a\u00020\u001b\u0012\u0006\u0010\u001c\u001a\u00020\u001b\u0012\b\u0010\u001d\u001a\u0004\u0018\u00010\u001b\u0012\b\u0010\u001e\u001a\u0004\u0018\u00010\u001b\u0012\b\u0010\u001f\u001a\u0004\u0018\u00010\u001b\u0012\u0006\u0010 \u001a\u00020\u0014\u0012\u0006\u0010!\u001a\u00020\u0014\u0012\u0006\u0010\"\u001a\u00020\u0014\u0012\b\u0010#\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010$\u001a\u0004\u0018\u00010\u0003\u0012\u000e\u0010%\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010&\u0012\u000e\u0010\'\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010&\u0012\u0006\u0010(\u001a\u00020\b\u0012\u0006\u0010)\u001a\u00020\b\u00a2\u0006\u0002\u0010*J\t\u0010U\u001a\u00020\u0003H\u00c6\u0003J\t\u0010V\u001a\u00020\u000fH\u00c6\u0003J\u000b\u0010W\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010X\u001a\u0004\u0018\u00010\bH\u00c6\u0003J\u000b\u0010Y\u001a\u0004\u0018\u00010\bH\u00c6\u0003J\t\u0010Z\u001a\u00020\u0014H\u00c6\u0003J\t\u0010[\u001a\u00020\u0014H\u00c6\u0003J\t\u0010\\\u001a\u00020\u0014H\u00c6\u0003J\t\u0010]\u001a\u00020\u0014H\u00c6\u0003J\t\u0010^\u001a\u00020\u0014H\u00c6\u0003J\u000b\u0010_\u001a\u0004\u0018\u00010\bH\u00c6\u0003J\t\u0010`\u001a\u00020\u0003H\u00c6\u0003J\t\u0010a\u001a\u00020\u001bH\u00c6\u0003J\t\u0010b\u001a\u00020\u001bH\u00c6\u0003J\u0010\u0010c\u001a\u0004\u0018\u00010\u001bH\u00c6\u0003\u00a2\u0006\u0002\u0010<J\u0010\u0010d\u001a\u0004\u0018\u00010\u001bH\u00c6\u0003\u00a2\u0006\u0002\u0010<J\u0010\u0010e\u001a\u0004\u0018\u00010\u001bH\u00c6\u0003\u00a2\u0006\u0002\u0010<J\t\u0010f\u001a\u00020\u0014H\u00c6\u0003J\t\u0010g\u001a\u00020\u0014H\u00c6\u0003J\t\u0010h\u001a\u00020\u0014H\u00c6\u0003J\u000b\u0010i\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010j\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010k\u001a\u00020\u0003H\u00c6\u0003J\u0011\u0010l\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010&H\u00c6\u0003J\u0011\u0010m\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010&H\u00c6\u0003J\t\u0010n\u001a\u00020\bH\u00c6\u0003J\t\u0010o\u001a\u00020\bH\u00c6\u0003J\t\u0010p\u001a\u00020\u0003H\u00c6\u0003J\t\u0010q\u001a\u00020\bH\u00c6\u0003J\u000b\u0010r\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010s\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010t\u001a\u00020\fH\u00c6\u0003J\t\u0010u\u001a\u00020\fH\u00c6\u0003J\u00fe\u0002\u0010v\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\f2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\u0013\u001a\u00020\u00142\b\b\u0002\u0010\u0015\u001a\u00020\u00142\b\b\u0002\u0010\u0016\u001a\u00020\u00142\b\b\u0002\u0010\u0017\u001a\u00020\u00142\b\b\u0002\u0010\u0018\u001a\u00020\u00142\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\u001a\u001a\u00020\u001b2\b\b\u0002\u0010\u001c\u001a\u00020\u001b2\n\b\u0002\u0010\u001d\u001a\u0004\u0018\u00010\u001b2\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u001b2\n\b\u0002\u0010\u001f\u001a\u0004\u0018\u00010\u001b2\b\b\u0002\u0010 \u001a\u00020\u00142\b\b\u0002\u0010!\u001a\u00020\u00142\b\b\u0002\u0010\"\u001a\u00020\u00142\n\b\u0002\u0010#\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010$\u001a\u0004\u0018\u00010\u00032\u0010\b\u0002\u0010%\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010&2\u0010\b\u0002\u0010\'\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010&2\b\b\u0002\u0010(\u001a\u00020\b2\b\b\u0002\u0010)\u001a\u00020\bH\u00c6\u0001\u00a2\u0006\u0002\u0010wJ\u0013\u0010x\u001a\u00020\f2\b\u0010y\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010z\u001a\u00020\u0014H\u00d6\u0001J\t\u0010{\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0018\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010,R\u0011\u0010!\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010,R\u0011\u0010 \u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010,R\u0013\u0010\u0010\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u00100R\u0013\u0010\u0011\u001a\u0004\u0018\u00010\b\u00a2\u0006\b\n\u0000\u001a\u0004\b1\u00102R\u0013\u0010\u0012\u001a\u0004\u0018\u00010\b\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u00102R\u0011\u0010\r\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b4\u00105R\u0013\u0010#\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u00100R\u0013\u0010$\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b7\u00100R\u0011\u0010(\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u00102R\u0019\u0010%\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010&\u00a2\u0006\b\n\u0000\u001a\u0004\b9\u0010:R\u0015\u0010\u001d\u001a\u0004\u0018\u00010\u001b\u00a2\u0006\n\n\u0002\u0010=\u001a\u0004\b;\u0010<R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b>\u00102R\u0015\u0010\u001f\u001a\u0004\u0018\u00010\u001b\u00a2\u0006\n\n\u0002\u0010=\u001a\u0004\b?\u0010<R\u0015\u0010\u001e\u001a\u0004\u0018\u00010\u001b\u00a2\u0006\n\n\u0002\u0010=\u001a\u0004\b@\u0010<R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bA\u00100R\u0013\u0010\u0019\u001a\u0004\u0018\u00010\b\u00a2\u0006\b\n\u0000\u001a\u0004\bB\u00102R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bC\u00100R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bD\u00100R\u0011\u0010\u001c\u001a\u00020\u001b\u00a2\u0006\b\n\u0000\u001a\u0004\bE\u0010FR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bG\u00100R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bH\u00100R\u0019\u0010\'\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010&\u00a2\u0006\b\n\u0000\u001a\u0004\bI\u0010:R\u0013\u0010\t\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bJ\u00100R\u0011\u0010\u0016\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\bK\u0010,R\u0011\u0010\u0017\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\bL\u0010,R\u0011\u0010\u0013\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\bM\u0010,R\u0011\u0010\u0015\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\bN\u0010,R\u0011\u0010\"\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\bO\u0010,R\u0011\u0010)\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\bP\u00102R\u0011\u0010\u001a\u001a\u00020\u001b\u00a2\u0006\b\n\u0000\u001a\u0004\bQ\u0010FR\u0011\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\bR\u0010SR\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\bT\u00105\u00a8\u0006|"}, d2 = {"Lcom/example/rooster/feature/farm/domain/model/FarmDetails;", "", "id", "", "ownerId", "name", "location", "establishedDate", "Ljava/util/Date;", "registrationNumber", "licenseNumber", "verified", "", "certified", "verificationLevel", "Lcom/example/rooster/feature/farm/domain/model/VerificationLevel;", "certificationAgency", "certificationDate", "certificationExpiryDate", "totalFowls", "", "totalHens", "totalBreeders", "totalChicks", "activeFlocks", "lastHealthCheck", "vaccinationCompliance", "", "mortalityRate", "eggProductionRate", "hatchingSuccessRate", "feedConversionRatio", "biosecurityScore", "animalWelfareScore", "traceabilityScore", "contactEmail", "contactPhone", "documents", "", "photos", "createdAt", "updatedAt", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/Date;Ljava/lang/String;Ljava/lang/String;ZZLcom/example/rooster/feature/farm/domain/model/VerificationLevel;Ljava/lang/String;Ljava/util/Date;Ljava/util/Date;IIIIILjava/util/Date;DDLjava/lang/Double;Ljava/lang/Double;Ljava/lang/Double;IIILjava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/util/Date;Ljava/util/Date;)V", "getActiveFlocks", "()I", "getAnimalWelfareScore", "getBiosecurityScore", "getCertificationAgency", "()Ljava/lang/String;", "getCertificationDate", "()Ljava/util/Date;", "getCertificationExpiryDate", "getCertified", "()Z", "getContactEmail", "getContactPhone", "getCreatedAt", "getDocuments", "()Ljava/util/List;", "getEggProductionRate", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getEstablishedDate", "getFeedConversionRatio", "getHatchingSuccessRate", "getId", "getLastHealthCheck", "getLicenseNumber", "getLocation", "getMortalityRate", "()D", "getName", "getOwnerId", "getPhotos", "getRegistrationNumber", "getTotalBreeders", "getTotalChicks", "getTotalFowls", "getTotalHens", "getTraceabilityScore", "getUpdatedAt", "getVaccinationCompliance", "getVerificationLevel", "()Lcom/example/rooster/feature/farm/domain/model/VerificationLevel;", "getVerified", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component20", "component21", "component22", "component23", "component24", "component25", "component26", "component27", "component28", "component29", "component3", "component30", "component31", "component32", "component33", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/Date;Ljava/lang/String;Ljava/lang/String;ZZLcom/example/rooster/feature/farm/domain/model/VerificationLevel;Ljava/lang/String;Ljava/util/Date;Ljava/util/Date;IIIIILjava/util/Date;DDLjava/lang/Double;Ljava/lang/Double;Ljava/lang/Double;IIILjava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/util/Date;Ljava/util/Date;)Lcom/example/rooster/feature/farm/domain/model/FarmDetails;", "equals", "other", "hashCode", "toString", "feature-farm_debug"})
public final class FarmDetails {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String ownerId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String name = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String location = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Date establishedDate = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String registrationNumber = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String licenseNumber = null;
    private final boolean verified = false;
    private final boolean certified = false;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.model.VerificationLevel verificationLevel = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String certificationAgency = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.Date certificationDate = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.Date certificationExpiryDate = null;
    private final int totalFowls = 0;
    private final int totalHens = 0;
    private final int totalBreeders = 0;
    private final int totalChicks = 0;
    private final int activeFlocks = 0;
    @org.jetbrains.annotations.Nullable()
    private final java.util.Date lastHealthCheck = null;
    private final double vaccinationCompliance = 0.0;
    private final double mortalityRate = 0.0;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double eggProductionRate = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double hatchingSuccessRate = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double feedConversionRatio = null;
    private final int biosecurityScore = 0;
    private final int animalWelfareScore = 0;
    private final int traceabilityScore = 0;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String contactEmail = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String contactPhone = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.List<java.lang.String> documents = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.List<java.lang.String> photos = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Date createdAt = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Date updatedAt = null;
    
    public FarmDetails(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String ownerId, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String location, @org.jetbrains.annotations.NotNull()
    java.util.Date establishedDate, @org.jetbrains.annotations.Nullable()
    java.lang.String registrationNumber, @org.jetbrains.annotations.Nullable()
    java.lang.String licenseNumber, boolean verified, boolean certified, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.VerificationLevel verificationLevel, @org.jetbrains.annotations.Nullable()
    java.lang.String certificationAgency, @org.jetbrains.annotations.Nullable()
    java.util.Date certificationDate, @org.jetbrains.annotations.Nullable()
    java.util.Date certificationExpiryDate, int totalFowls, int totalHens, int totalBreeders, int totalChicks, int activeFlocks, @org.jetbrains.annotations.Nullable()
    java.util.Date lastHealthCheck, double vaccinationCompliance, double mortalityRate, @org.jetbrains.annotations.Nullable()
    java.lang.Double eggProductionRate, @org.jetbrains.annotations.Nullable()
    java.lang.Double hatchingSuccessRate, @org.jetbrains.annotations.Nullable()
    java.lang.Double feedConversionRatio, int biosecurityScore, int animalWelfareScore, int traceabilityScore, @org.jetbrains.annotations.Nullable()
    java.lang.String contactEmail, @org.jetbrains.annotations.Nullable()
    java.lang.String contactPhone, @org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> documents, @org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> photos, @org.jetbrains.annotations.NotNull()
    java.util.Date createdAt, @org.jetbrains.annotations.NotNull()
    java.util.Date updatedAt) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOwnerId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getLocation() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date getEstablishedDate() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getRegistrationNumber() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getLicenseNumber() {
        return null;
    }
    
    public final boolean getVerified() {
        return false;
    }
    
    public final boolean getCertified() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.VerificationLevel getVerificationLevel() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCertificationAgency() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date getCertificationDate() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date getCertificationExpiryDate() {
        return null;
    }
    
    public final int getTotalFowls() {
        return 0;
    }
    
    public final int getTotalHens() {
        return 0;
    }
    
    public final int getTotalBreeders() {
        return 0;
    }
    
    public final int getTotalChicks() {
        return 0;
    }
    
    public final int getActiveFlocks() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date getLastHealthCheck() {
        return null;
    }
    
    public final double getVaccinationCompliance() {
        return 0.0;
    }
    
    public final double getMortalityRate() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getEggProductionRate() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getHatchingSuccessRate() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getFeedConversionRatio() {
        return null;
    }
    
    public final int getBiosecurityScore() {
        return 0;
    }
    
    public final int getAnimalWelfareScore() {
        return 0;
    }
    
    public final int getTraceabilityScore() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getContactEmail() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getContactPhone() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.String> getDocuments() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.String> getPhotos() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date getCreatedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date getUpdatedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.VerificationLevel component10() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component11() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date component12() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date component13() {
        return null;
    }
    
    public final int component14() {
        return 0;
    }
    
    public final int component15() {
        return 0;
    }
    
    public final int component16() {
        return 0;
    }
    
    public final int component17() {
        return 0;
    }
    
    public final int component18() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date component19() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    public final double component20() {
        return 0.0;
    }
    
    public final double component21() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component22() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component23() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component24() {
        return null;
    }
    
    public final int component25() {
        return 0;
    }
    
    public final int component26() {
        return 0;
    }
    
    public final int component27() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component28() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component29() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.String> component30() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.String> component31() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date component32() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date component33() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component7() {
        return null;
    }
    
    public final boolean component8() {
        return false;
    }
    
    public final boolean component9() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.FarmDetails copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String ownerId, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String location, @org.jetbrains.annotations.NotNull()
    java.util.Date establishedDate, @org.jetbrains.annotations.Nullable()
    java.lang.String registrationNumber, @org.jetbrains.annotations.Nullable()
    java.lang.String licenseNumber, boolean verified, boolean certified, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.VerificationLevel verificationLevel, @org.jetbrains.annotations.Nullable()
    java.lang.String certificationAgency, @org.jetbrains.annotations.Nullable()
    java.util.Date certificationDate, @org.jetbrains.annotations.Nullable()
    java.util.Date certificationExpiryDate, int totalFowls, int totalHens, int totalBreeders, int totalChicks, int activeFlocks, @org.jetbrains.annotations.Nullable()
    java.util.Date lastHealthCheck, double vaccinationCompliance, double mortalityRate, @org.jetbrains.annotations.Nullable()
    java.lang.Double eggProductionRate, @org.jetbrains.annotations.Nullable()
    java.lang.Double hatchingSuccessRate, @org.jetbrains.annotations.Nullable()
    java.lang.Double feedConversionRatio, int biosecurityScore, int animalWelfareScore, int traceabilityScore, @org.jetbrains.annotations.Nullable()
    java.lang.String contactEmail, @org.jetbrains.annotations.Nullable()
    java.lang.String contactPhone, @org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> documents, @org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> photos, @org.jetbrains.annotations.NotNull()
    java.util.Date createdAt, @org.jetbrains.annotations.NotNull()
    java.util.Date updatedAt) {
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