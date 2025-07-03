package com.example.rooster.core.common.model;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b(\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u00a5\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00030\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u0003\u0012\b\b\u0002\u0010\r\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000b\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\b\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00030\u000b\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0014\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u0016\u00a2\u0006\u0002\u0010\u0017J\t\u0010.\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010/\u001a\u0004\u0018\u00010\bH\u00c6\u0003\u00a2\u0006\u0002\u0010\"J\t\u00100\u001a\u00020\u0003H\u00c6\u0003J\u000f\u00101\u001a\b\u0012\u0004\u0012\u00020\u00030\u000bH\u00c6\u0003J\t\u00102\u001a\u00020\u0014H\u00c6\u0003J\t\u00103\u001a\u00020\u0016H\u00c6\u0003J\t\u00104\u001a\u00020\u0003H\u00c6\u0003J\t\u00105\u001a\u00020\u0006H\u00c6\u0003J\t\u00106\u001a\u00020\bH\u00c6\u0003J\t\u00107\u001a\u00020\u0003H\u00c6\u0003J\u000f\u00108\u001a\b\u0012\u0004\u0012\u00020\u00030\u000bH\u00c6\u0003J\t\u00109\u001a\u00020\u0003H\u00c6\u0003J\t\u0010:\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010;\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000bH\u00c6\u0003J\u00ae\u0001\u0010<\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\u00032\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00030\u000b2\b\b\u0002\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u00032\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000b2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\u0011\u001a\u00020\u00032\u000e\b\u0002\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00030\u000b2\b\b\u0002\u0010\u0013\u001a\u00020\u00142\b\b\u0002\u0010\u0015\u001a\u00020\u0016H\u00c6\u0001\u00a2\u0006\u0002\u0010=J\u0013\u0010>\u001a\u00020?2\b\u0010@\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010A\u001a\u00020BH\u00d6\u0001J\t\u0010C\u001a\u00020\u0003H\u00d6\u0001R\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00030\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u0015\u001a\u00020\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001bR\u0015\u0010\u0010\u001a\u0004\u0018\u00010\b\u00a2\u0006\n\n\u0002\u0010#\u001a\u0004\b!\u0010\"R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001bR\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u0019R\u0011\u0010\u0011\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u001bR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010(R\u0011\u0010\u0013\u001a\u00020\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00030\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\u0019R\u0011\u0010\r\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010\u001bR\u0011\u0010\t\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010\u001b\u00a8\u0006D"}, d2 = {"Lcom/example/rooster/core/common/model/HealthRecord;", "", "id", "", "birdId", "recordType", "Lcom/example/rooster/core/common/model/HealthRecordType;", "date", "", "veterinarianId", "symptoms", "", "diagnosis", "treatment", "medication", "Lcom/example/rooster/core/common/model/Medication;", "followUpDate", "notes", "attachments", "severity", "Lcom/example/rooster/core/common/model/HealthSeverity;", "cost", "", "(Ljava/lang/String;Ljava/lang/String;Lcom/example/rooster/core/common/model/HealthRecordType;JLjava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/lang/Long;Ljava/lang/String;Ljava/util/List;Lcom/example/rooster/core/common/model/HealthSeverity;D)V", "getAttachments", "()Ljava/util/List;", "getBirdId", "()Ljava/lang/String;", "getCost", "()D", "getDate", "()J", "getDiagnosis", "getFollowUpDate", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getId", "getMedication", "getNotes", "getRecordType", "()Lcom/example/rooster/core/common/model/HealthRecordType;", "getSeverity", "()Lcom/example/rooster/core/common/model/HealthSeverity;", "getSymptoms", "getTreatment", "getVeterinarianId", "component1", "component10", "component11", "component12", "component13", "component14", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;Lcom/example/rooster/core/common/model/HealthRecordType;JLjava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/lang/Long;Ljava/lang/String;Ljava/util/List;Lcom/example/rooster/core/common/model/HealthSeverity;D)Lcom/example/rooster/core/common/model/HealthRecord;", "equals", "", "other", "hashCode", "", "toString", "core-common_debug"})
public final class HealthRecord {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String birdId = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.HealthRecordType recordType = null;
    private final long date = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String veterinarianId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> symptoms = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String diagnosis = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String treatment = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.example.rooster.core.common.model.Medication> medication = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long followUpDate = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String notes = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> attachments = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.rooster.core.common.model.HealthSeverity severity = null;
    private final double cost = 0.0;
    
    public HealthRecord(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String birdId, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.HealthRecordType recordType, long date, @org.jetbrains.annotations.NotNull()
    java.lang.String veterinarianId, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> symptoms, @org.jetbrains.annotations.NotNull()
    java.lang.String diagnosis, @org.jetbrains.annotations.NotNull()
    java.lang.String treatment, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.Medication> medication, @org.jetbrains.annotations.Nullable()
    java.lang.Long followUpDate, @org.jetbrains.annotations.NotNull()
    java.lang.String notes, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> attachments, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.HealthSeverity severity, double cost) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getBirdId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.HealthRecordType getRecordType() {
        return null;
    }
    
    public final long getDate() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getVeterinarianId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getSymptoms() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDiagnosis() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTreatment() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.Medication> getMedication() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getFollowUpDate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getNotes() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getAttachments() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.HealthSeverity getSeverity() {
        return null;
    }
    
    public final double getCost() {
        return 0.0;
    }
    
    public HealthRecord() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component10() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component11() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component12() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.HealthSeverity component13() {
        return null;
    }
    
    public final double component14() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.HealthRecordType component3() {
        return null;
    }
    
    public final long component4() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.rooster.core.common.model.Medication> component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.rooster.core.common.model.HealthRecord copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String birdId, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.HealthRecordType recordType, long date, @org.jetbrains.annotations.NotNull()
    java.lang.String veterinarianId, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> symptoms, @org.jetbrains.annotations.NotNull()
    java.lang.String diagnosis, @org.jetbrains.annotations.NotNull()
    java.lang.String treatment, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.rooster.core.common.model.Medication> medication, @org.jetbrains.annotations.Nullable()
    java.lang.Long followUpDate, @org.jetbrains.annotations.NotNull()
    java.lang.String notes, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> attachments, @org.jetbrains.annotations.NotNull()
    com.example.rooster.core.common.model.HealthSeverity severity, double cost) {
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