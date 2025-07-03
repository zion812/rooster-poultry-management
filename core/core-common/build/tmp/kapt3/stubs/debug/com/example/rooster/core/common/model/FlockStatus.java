package com.example.rooster.core.common.model;

import kotlinx.serialization.Serializable;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007\u00a8\u0006\b"}, d2 = {"Lcom/example/rooster/core/common/model/FlockStatus;", "", "(Ljava/lang/String;I)V", "ACTIVE", "SOLD", "COMPLETED", "QUARANTINED", "UNDER_TREATMENT", "core-common_debug"})
public enum FlockStatus {
    /*public static final*/ ACTIVE /* = new ACTIVE() */,
    /*public static final*/ SOLD /* = new SOLD() */,
    /*public static final*/ COMPLETED /* = new COMPLETED() */,
    /*public static final*/ QUARANTINED /* = new QUARANTINED() */,
    /*public static final*/ UNDER_TREATMENT /* = new UNDER_TREATMENT() */;
    
    FlockStatus() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.example.rooster.core.common.model.FlockStatus> getEntries() {
        return null;
    }
}