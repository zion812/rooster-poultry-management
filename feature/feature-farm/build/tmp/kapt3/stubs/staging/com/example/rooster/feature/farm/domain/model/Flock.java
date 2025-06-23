package com.example.rooster.feature.farm.domain.model;

/**
 * Enhanced Flock model with enterprise-grade traceability and comprehensive metadata
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000|\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\bc\b\u0087\b\u0018\u00002\u00020\u0001B\u00db\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\f\u0012\b\u0010\r\u001a\u0004\u0018\u00010\f\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010\u0012\u0006\u0010\u0011\u001a\u00020\u0012\u0012\u0006\u0010\u0013\u001a\u00020\u0012\u0012\u0006\u0010\u0014\u001a\u00020\u0015\u0012\u0006\u0010\u0016\u001a\u00020\u0012\u0012\u0006\u0010\u0017\u001a\u00020\u0018\u0012\b\u0010\u0019\u001a\u0004\u0018\u00010\u001a\u0012\b\u0010\u001b\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u001c\u001a\u0004\u0018\u00010\u001d\u0012\u0006\u0010\u001e\u001a\u00020\u001f\u0012\b\u0010 \u001a\u0004\u0018\u00010\u001a\u0012\u0006\u0010!\u001a\u00020\"\u0012\b\u0010#\u001a\u0004\u0018\u00010\u001a\u0012\b\u0010$\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010%\u001a\u0004\u0018\u00010\u0003\u0012\u000e\u0010&\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\'\u0012\b\u0010(\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010)\u001a\u0004\u0018\u00010\u001d\u0012\b\u0010*\u001a\u0004\u0018\u00010+\u0012\b\u0010,\u001a\u0004\u0018\u00010+\u0012\u0006\u0010-\u001a\u00020.\u0012\u0006\u0010/\u001a\u00020\u0012\u0012\b\u00100\u001a\u0004\u0018\u00010+\u0012\u000e\u00101\u001a\n\u0012\u0004\u0012\u000202\u0018\u00010\'\u0012\u0006\u00103\u001a\u00020\u001a\u0012\u0006\u00104\u001a\u00020\u001a\u00a2\u0006\u0002\u00105J\t\u0010k\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010l\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010m\u001a\u0004\u0018\u00010\u0010H\u00c6\u0003J\t\u0010n\u001a\u00020\u0012H\u00c6\u0003J\t\u0010o\u001a\u00020\u0012H\u00c6\u0003J\t\u0010p\u001a\u00020\u0015H\u00c6\u0003J\t\u0010q\u001a\u00020\u0012H\u00c6\u0003J\t\u0010r\u001a\u00020\u0018H\u00c6\u0003J\u000b\u0010s\u001a\u0004\u0018\u00010\u001aH\u00c6\u0003J\u000b\u0010t\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0010\u0010u\u001a\u0004\u0018\u00010\u001dH\u00c6\u0003\u00a2\u0006\u0002\u0010@J\t\u0010v\u001a\u00020\u0003H\u00c6\u0003J\t\u0010w\u001a\u00020\u001fH\u00c6\u0003J\u000b\u0010x\u001a\u0004\u0018\u00010\u001aH\u00c6\u0003J\t\u0010y\u001a\u00020\"H\u00c6\u0003J\u000b\u0010z\u001a\u0004\u0018\u00010\u001aH\u00c6\u0003J\u000b\u0010{\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010|\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0011\u0010}\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\'H\u00c6\u0003J\u000b\u0010~\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0010\u0010\u007f\u001a\u0004\u0018\u00010\u001dH\u00c6\u0003\u00a2\u0006\u0002\u0010@J\u0011\u0010\u0080\u0001\u001a\u0004\u0018\u00010+H\u00c6\u0003\u00a2\u0006\u0002\u0010EJ\f\u0010\u0081\u0001\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0011\u0010\u0082\u0001\u001a\u0004\u0018\u00010+H\u00c6\u0003\u00a2\u0006\u0002\u0010EJ\n\u0010\u0083\u0001\u001a\u00020.H\u00c6\u0003J\n\u0010\u0084\u0001\u001a\u00020\u0012H\u00c6\u0003J\u0011\u0010\u0085\u0001\u001a\u0004\u0018\u00010+H\u00c6\u0003\u00a2\u0006\u0002\u0010EJ\u0012\u0010\u0086\u0001\u001a\n\u0012\u0004\u0012\u000202\u0018\u00010\'H\u00c6\u0003J\n\u0010\u0087\u0001\u001a\u00020\u001aH\u00c6\u0003J\n\u0010\u0088\u0001\u001a\u00020\u001aH\u00c6\u0003J\f\u0010\u0089\u0001\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\n\u0010\u008a\u0001\u001a\u00020\bH\u00c6\u0003J\n\u0010\u008b\u0001\u001a\u00020\u0003H\u00c6\u0003J\f\u0010\u008c\u0001\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0011\u0010\u008d\u0001\u001a\u0004\u0018\u00010\fH\u00c6\u0003\u00a2\u0006\u0002\u0010NJ\u0011\u0010\u008e\u0001\u001a\u0004\u0018\u00010\fH\u00c6\u0003\u00a2\u0006\u0002\u0010NJ\u00ae\u0003\u0010\u008f\u0001\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\f2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u00122\b\b\u0002\u0010\u0013\u001a\u00020\u00122\b\b\u0002\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00122\b\b\u0002\u0010\u0017\u001a\u00020\u00182\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u001a2\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\b\b\u0002\u0010\u001e\u001a\u00020\u001f2\n\b\u0002\u0010 \u001a\u0004\u0018\u00010\u001a2\b\b\u0002\u0010!\u001a\u00020\"2\n\b\u0002\u0010#\u001a\u0004\u0018\u00010\u001a2\n\b\u0002\u0010$\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010%\u001a\u0004\u0018\u00010\u00032\u0010\b\u0002\u0010&\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\'2\n\b\u0002\u0010(\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010)\u001a\u0004\u0018\u00010\u001d2\n\b\u0002\u0010*\u001a\u0004\u0018\u00010+2\n\b\u0002\u0010,\u001a\u0004\u0018\u00010+2\b\b\u0002\u0010-\u001a\u00020.2\b\b\u0002\u0010/\u001a\u00020\u00122\n\b\u0002\u00100\u001a\u0004\u0018\u00010+2\u0010\b\u0002\u00101\u001a\n\u0012\u0004\u0012\u000202\u0018\u00010\'2\b\b\u0002\u00103\u001a\u00020\u001a2\b\b\u0002\u00104\u001a\u00020\u001aH\u00c6\u0001\u00a2\u0006\u0003\u0010\u0090\u0001J\u0015\u0010\u0091\u0001\u001a\u00020\u00122\t\u0010\u0092\u0001\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\n\u0010\u0093\u0001\u001a\u00020\u001dH\u00d6\u0001J\n\u0010\u0094\u0001\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0017\u001a\u00020\u0018\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u00107R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u00109R\u0011\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b:\u0010;R\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b<\u00109R\u0011\u00103\u001a\u00020\u001a\u00a2\u0006\b\n\u0000\u001a\u0004\b=\u0010>R\u0015\u0010\u001c\u001a\u0004\u0018\u00010\u001d\u00a2\u0006\n\n\u0002\u0010A\u001a\u0004\b?\u0010@R\u0013\u0010\u0019\u001a\u0004\u0018\u00010\u001a\u00a2\u0006\b\n\u0000\u001a\u0004\bB\u0010>R\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bC\u00109R\u0015\u0010,\u001a\u0004\u0018\u00010+\u00a2\u0006\n\n\u0002\u0010F\u001a\u0004\bD\u0010ER\u0011\u0010/\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\bG\u0010;R\u0013\u0010\u000f\u001a\u0004\u0018\u00010\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\bH\u0010IR\u0015\u0010*\u001a\u0004\u0018\u00010+\u00a2\u0006\n\n\u0002\u0010F\u001a\u0004\bJ\u0010ER\u0011\u0010!\u001a\u00020\"\u00a2\u0006\b\n\u0000\u001a\u0004\bK\u0010LR\u0015\u0010\r\u001a\u0004\u0018\u00010\f\u00a2\u0006\n\n\u0002\u0010O\u001a\u0004\bM\u0010NR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bP\u00109R\u0013\u0010$\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bQ\u00109R\u0013\u0010#\u001a\u0004\u0018\u00010\u001a\u00a2\u0006\b\n\u0000\u001a\u0004\bR\u0010>R\u0013\u0010 \u001a\u0004\u0018\u00010\u001a\u00a2\u0006\b\n\u0000\u001a\u0004\bS\u0010>R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bT\u00109R\u0011\u0010\t\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bU\u00109R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bV\u00109R\u0013\u0010\u001b\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bW\u00109R\u0015\u00100\u001a\u0004\u0018\u00010+\u00a2\u0006\n\n\u0002\u0010F\u001a\u0004\bX\u0010ER\u0015\u0010)\u001a\u0004\u0018\u00010\u001d\u00a2\u0006\n\n\u0002\u0010A\u001a\u0004\bY\u0010@R\u0019\u0010&\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\'\u00a2\u0006\b\n\u0000\u001a\u0004\bZ\u0010[R\u0019\u00101\u001a\n\u0012\u0004\u0012\u000202\u0018\u00010\'\u00a2\u0006\b\n\u0000\u001a\u0004\b\\\u0010[R\u0013\u0010%\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b]\u00109R\u0013\u0010(\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b^\u00109R\u0011\u0010-\u001a\u00020.\u00a2\u0006\b\n\u0000\u001a\u0004\b_\u0010`R\u0011\u0010\u0016\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\ba\u0010;R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\bb\u0010cR\u0011\u00104\u001a\u00020\u001a\u00a2\u0006\b\n\u0000\u001a\u0004\bd\u0010>R\u0011\u0010\u001e\u001a\u00020\u001f\u00a2\u0006\b\n\u0000\u001a\u0004\be\u0010fR\u0011\u0010\u0014\u001a\u00020\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\bg\u0010hR\u0011\u0010\u0013\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\bi\u0010;R\u0015\u0010\u000b\u001a\u0004\u0018\u00010\f\u00a2\u0006\n\n\u0002\u0010O\u001a\u0004\bj\u0010N\u00a8\u0006\u0095\u0001"}, d2 = {"Lcom/example/rooster/feature/farm/domain/model/Flock;", "", "id", "", "ownerId", "fatherId", "motherId", "type", "Lcom/example/rooster/feature/farm/domain/model/FlockType;", "name", "breed", "weight", "", "height", "color", "gender", "Lcom/example/rooster/feature/farm/domain/model/Gender;", "certified", "", "verified", "verificationLevel", "Lcom/example/rooster/feature/farm/domain/model/VerificationLevel;", "traceable", "ageGroup", "Lcom/example/rooster/feature/farm/domain/model/AgeGroup;", "dateOfBirth", "Ljava/util/Date;", "placeOfBirth", "currentAge", "", "vaccinationStatus", "Lcom/example/rooster/feature/farm/domain/model/VaccinationStatus;", "lastVaccinationDate", "healthStatus", "Lcom/example/rooster/feature/farm/domain/model/HealthStatus;", "lastHealthCheck", "identification", "registryNumber", "proofs", "", "specialty", "productivityScore", "growthRate", "", "feedConversionRatio", "status", "Lcom/example/rooster/feature/farm/domain/model/FlockStatus;", "forSale", "price", "purpose", "Lcom/example/rooster/feature/farm/domain/model/Purpose;", "createdAt", "updatedAt", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/example/rooster/feature/farm/domain/model/FlockType;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Float;Ljava/lang/Float;Ljava/lang/String;Lcom/example/rooster/feature/farm/domain/model/Gender;ZZLcom/example/rooster/feature/farm/domain/model/VerificationLevel;ZLcom/example/rooster/feature/farm/domain/model/AgeGroup;Ljava/util/Date;Ljava/lang/String;Ljava/lang/Integer;Lcom/example/rooster/feature/farm/domain/model/VaccinationStatus;Ljava/util/Date;Lcom/example/rooster/feature/farm/domain/model/HealthStatus;Ljava/util/Date;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Double;Ljava/lang/Double;Lcom/example/rooster/feature/farm/domain/model/FlockStatus;ZLjava/lang/Double;Ljava/util/List;Ljava/util/Date;Ljava/util/Date;)V", "getAgeGroup", "()Lcom/example/rooster/feature/farm/domain/model/AgeGroup;", "getBreed", "()Ljava/lang/String;", "getCertified", "()Z", "getColor", "getCreatedAt", "()Ljava/util/Date;", "getCurrentAge", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getDateOfBirth", "getFatherId", "getFeedConversionRatio", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getForSale", "getGender", "()Lcom/example/rooster/feature/farm/domain/model/Gender;", "getGrowthRate", "getHealthStatus", "()Lcom/example/rooster/feature/farm/domain/model/HealthStatus;", "getHeight", "()Ljava/lang/Float;", "Ljava/lang/Float;", "getId", "getIdentification", "getLastHealthCheck", "getLastVaccinationDate", "getMotherId", "getName", "getOwnerId", "getPlaceOfBirth", "getPrice", "getProductivityScore", "getProofs", "()Ljava/util/List;", "getPurpose", "getRegistryNumber", "getSpecialty", "getStatus", "()Lcom/example/rooster/feature/farm/domain/model/FlockStatus;", "getTraceable", "getType", "()Lcom/example/rooster/feature/farm/domain/model/FlockType;", "getUpdatedAt", "getVaccinationStatus", "()Lcom/example/rooster/feature/farm/domain/model/VaccinationStatus;", "getVerificationLevel", "()Lcom/example/rooster/feature/farm/domain/model/VerificationLevel;", "getVerified", "getWeight", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component20", "component21", "component22", "component23", "component24", "component25", "component26", "component27", "component28", "component29", "component3", "component30", "component31", "component32", "component33", "component34", "component35", "component36", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/example/rooster/feature/farm/domain/model/FlockType;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Float;Ljava/lang/Float;Ljava/lang/String;Lcom/example/rooster/feature/farm/domain/model/Gender;ZZLcom/example/rooster/feature/farm/domain/model/VerificationLevel;ZLcom/example/rooster/feature/farm/domain/model/AgeGroup;Ljava/util/Date;Ljava/lang/String;Ljava/lang/Integer;Lcom/example/rooster/feature/farm/domain/model/VaccinationStatus;Ljava/util/Date;Lcom/example/rooster/feature/farm/domain/model/HealthStatus;Ljava/util/Date;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Double;Ljava/lang/Double;Lcom/example/rooster/feature/farm/domain/model/FlockStatus;ZLjava/lang/Double;Ljava/util/List;Ljava/util/Date;Ljava/util/Date;)Lcom/example/rooster/feature/farm/domain/model/Flock;", "equals", "other", "hashCode", "toString", "feature-farm_staging"})
public final class Flock {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String ownerId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String fatherId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String motherId = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.model.FlockType type = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String name = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String breed = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Float weight = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Float height = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String color = null;
    @org.jetbrains.annotations.Nullable()
    private final com.example.rooster.feature.farm.domain.model.Gender gender = null;
    private final boolean certified = false;
    private final boolean verified = false;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.model.VerificationLevel verificationLevel = null;
    private final boolean traceable = false;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.model.AgeGroup ageGroup = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.Date dateOfBirth = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String placeOfBirth = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer currentAge = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.model.VaccinationStatus vaccinationStatus = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.Date lastVaccinationDate = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.model.HealthStatus healthStatus = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.Date lastHealthCheck = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String identification = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String registryNumber = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.List<java.lang.String> proofs = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String specialty = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer productivityScore = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double growthRate = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double feedConversionRatio = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.feature.farm.domain.model.FlockStatus status = null;
    private final boolean forSale = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Double price = null;
    @org.jetbrains.annotations.Nullable()
    private final java.util.List<com.example.rooster.feature.farm.domain.model.Purpose> purpose = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Date createdAt = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Date updatedAt = null;
    
    public Flock(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String ownerId, @org.jetbrains.annotations.Nullable()
    java.lang.String fatherId, @org.jetbrains.annotations.Nullable()
    java.lang.String motherId, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.FlockType type, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String breed, @org.jetbrains.annotations.Nullable()
    java.lang.Float weight, @org.jetbrains.annotations.Nullable()
    java.lang.Float height, @org.jetbrains.annotations.Nullable()
    java.lang.String color, @org.jetbrains.annotations.Nullable()
    com.example.rooster.feature.farm.domain.model.Gender gender, boolean certified, boolean verified, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.VerificationLevel verificationLevel, boolean traceable, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.AgeGroup ageGroup, @org.jetbrains.annotations.Nullable()
    java.util.Date dateOfBirth, @org.jetbrains.annotations.Nullable()
    java.lang.String placeOfBirth, @org.jetbrains.annotations.Nullable()
    java.lang.Integer currentAge, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.VaccinationStatus vaccinationStatus, @org.jetbrains.annotations.Nullable()
    java.util.Date lastVaccinationDate, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.HealthStatus healthStatus, @org.jetbrains.annotations.Nullable()
    java.util.Date lastHealthCheck, @org.jetbrains.annotations.Nullable()
    java.lang.String identification, @org.jetbrains.annotations.Nullable()
    java.lang.String registryNumber, @org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> proofs, @org.jetbrains.annotations.Nullable()
    java.lang.String specialty, @org.jetbrains.annotations.Nullable()
    java.lang.Integer productivityScore, @org.jetbrains.annotations.Nullable()
    java.lang.Double growthRate, @org.jetbrains.annotations.Nullable()
    java.lang.Double feedConversionRatio, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.FlockStatus status, boolean forSale, @org.jetbrains.annotations.Nullable()
    java.lang.Double price, @org.jetbrains.annotations.Nullable()
    java.util.List<? extends com.example.rooster.feature.farm.domain.model.Purpose> purpose, @org.jetbrains.annotations.NotNull()
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
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getFatherId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getMotherId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.FlockType getType() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getName() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getBreed() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float getWeight() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float getHeight() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getColor() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.rooster.feature.farm.domain.model.Gender getGender() {
        return null;
    }
    
    public final boolean getCertified() {
        return false;
    }
    
    public final boolean getVerified() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.VerificationLevel getVerificationLevel() {
        return null;
    }
    
    public final boolean getTraceable() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.AgeGroup getAgeGroup() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date getDateOfBirth() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPlaceOfBirth() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getCurrentAge() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.VaccinationStatus getVaccinationStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date getLastVaccinationDate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.HealthStatus getHealthStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date getLastHealthCheck() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getIdentification() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getRegistryNumber() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.String> getProofs() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSpecialty() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getProductivityScore() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getGrowthRate() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getFeedConversionRatio() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.FlockStatus getStatus() {
        return null;
    }
    
    public final boolean getForSale() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double getPrice() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.example.rooster.feature.farm.domain.model.Purpose> getPurpose() {
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
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component10() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.rooster.feature.farm.domain.model.Gender component11() {
        return null;
    }
    
    public final boolean component12() {
        return false;
    }
    
    public final boolean component13() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.VerificationLevel component14() {
        return null;
    }
    
    public final boolean component15() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.AgeGroup component16() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date component17() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component18() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component19() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.VaccinationStatus component20() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date component21() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.HealthStatus component22() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Date component23() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component24() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component25() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<java.lang.String> component26() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component27() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component28() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component29() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component30() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.FlockStatus component31() {
        return null;
    }
    
    public final boolean component32() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Double component33() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.example.rooster.feature.farm.domain.model.Purpose> component34() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date component35() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Date component36() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.FlockType component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float component8() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Float component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.feature.farm.domain.model.Flock copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String ownerId, @org.jetbrains.annotations.Nullable()
    java.lang.String fatherId, @org.jetbrains.annotations.Nullable()
    java.lang.String motherId, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.FlockType type, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String breed, @org.jetbrains.annotations.Nullable()
    java.lang.Float weight, @org.jetbrains.annotations.Nullable()
    java.lang.Float height, @org.jetbrains.annotations.Nullable()
    java.lang.String color, @org.jetbrains.annotations.Nullable()
    com.example.rooster.feature.farm.domain.model.Gender gender, boolean certified, boolean verified, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.VerificationLevel verificationLevel, boolean traceable, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.AgeGroup ageGroup, @org.jetbrains.annotations.Nullable()
    java.util.Date dateOfBirth, @org.jetbrains.annotations.Nullable()
    java.lang.String placeOfBirth, @org.jetbrains.annotations.Nullable()
    java.lang.Integer currentAge, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.VaccinationStatus vaccinationStatus, @org.jetbrains.annotations.Nullable()
    java.util.Date lastVaccinationDate, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.HealthStatus healthStatus, @org.jetbrains.annotations.Nullable()
    java.util.Date lastHealthCheck, @org.jetbrains.annotations.Nullable()
    java.lang.String identification, @org.jetbrains.annotations.Nullable()
    java.lang.String registryNumber, @org.jetbrains.annotations.Nullable()
    java.util.List<java.lang.String> proofs, @org.jetbrains.annotations.Nullable()
    java.lang.String specialty, @org.jetbrains.annotations.Nullable()
    java.lang.Integer productivityScore, @org.jetbrains.annotations.Nullable()
    java.lang.Double growthRate, @org.jetbrains.annotations.Nullable()
    java.lang.Double feedConversionRatio, @org.jetbrains.annotations.NotNull()
    com.example.rooster.feature.farm.domain.model.FlockStatus status, boolean forSale, @org.jetbrains.annotations.Nullable()
    java.lang.Double price, @org.jetbrains.annotations.Nullable()
    java.util.List<? extends com.example.rooster.feature.farm.domain.model.Purpose> purpose, @org.jetbrains.annotations.NotNull()
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