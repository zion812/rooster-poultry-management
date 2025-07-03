package com.example.rooster.core.common.enums;

import kotlinx.serialization.Serializable;

@kotlinx.serialization.Serializable()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0087\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007\u00a8\u0006\b"}, d2 = {"Lcom/example/rooster/core/common/enums/BidStatus;", "", "(Ljava/lang/String;I)V", "ACTIVE", "OUTBID", "WINNING", "CANCELLED", "INVALID", "core-common_debug"})
public enum BidStatus {
    /*public static final*/ ACTIVE /* = new ACTIVE() */,
    /*public static final*/ OUTBID /* = new OUTBID() */,
    /*public static final*/ WINNING /* = new WINNING() */,
    /*public static final*/ CANCELLED /* = new CANCELLED() */,
    /*public static final*/ INVALID /* = new INVALID() */;
    
    BidStatus() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.example.rooster.core.common.enums.BidStatus> getEntries() {
        return null;
    }
}