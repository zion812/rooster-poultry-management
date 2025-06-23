package com.example.rooster.core.network;

/**
 * Enterprise-grade network error handling
 * Converts network exceptions to user-friendly error messages
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0003\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00060\u0001j\u0002`\u0002:\u0005\f\r\u000e\u000f\u0010B\u001b\b\u0004\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\u0007R\u0016\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u0082\u0001\u0005\u0011\u0012\u0013\u0014\u0015\u00a8\u0006\u0016"}, d2 = {"Lcom/example/rooster/core/network/NetworkError;", "Ljava/lang/Exception;", "Lkotlin/Exception;", "message", "", "cause", "", "(Ljava/lang/String;Ljava/lang/Throwable;)V", "getCause", "()Ljava/lang/Throwable;", "getMessage", "()Ljava/lang/String;", "ClientError", "NoInternet", "ServerError", "Timeout", "UnknownError", "Lcom/example/rooster/core/network/NetworkError$ClientError;", "Lcom/example/rooster/core/network/NetworkError$NoInternet;", "Lcom/example/rooster/core/network/NetworkError$ServerError;", "Lcom/example/rooster/core/network/NetworkError$Timeout;", "Lcom/example/rooster/core/network/NetworkError$UnknownError;", "core-network_staging"})
public abstract class NetworkError extends java.lang.Exception {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String message = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Throwable cause = null;
    
    private NetworkError(java.lang.String message, java.lang.Throwable cause) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getMessage() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Throwable getCause() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0003\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\f\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u001f\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001R\u0016\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0015"}, d2 = {"Lcom/example/rooster/core/network/NetworkError$ClientError;", "Lcom/example/rooster/core/network/NetworkError;", "code", "", "cause", "", "(ILjava/lang/Throwable;)V", "getCause", "()Ljava/lang/Throwable;", "getCode", "()I", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "toString", "", "core-network_staging"})
    public static final class ClientError extends com.example.rooster.core.network.NetworkError {
        private final int code = 0;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Throwable cause = null;
        
        public ClientError(int code, @org.jetbrains.annotations.Nullable()
        java.lang.Throwable cause) {
        }
        
        public final int getCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.Nullable()
        public java.lang.Throwable getCause() {
            return null;
        }
        
        public final int component1() {
            return 0;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Throwable component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.rooster.core.network.NetworkError.ClientError copy(int code, @org.jetbrains.annotations.Nullable()
        java.lang.Throwable cause) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0011\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0004J\u000b\u0010\u0007\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0015\u0010\b\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0016\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/example/rooster/core/network/NetworkError$NoInternet;", "Lcom/example/rooster/core/network/NetworkError;", "cause", "", "(Ljava/lang/Throwable;)V", "getCause", "()Ljava/lang/Throwable;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "core-network_staging"})
    public static final class NoInternet extends com.example.rooster.core.network.NetworkError {
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Throwable cause = null;
        
        public NoInternet(@org.jetbrains.annotations.Nullable()
        java.lang.Throwable cause) {
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.Nullable()
        public java.lang.Throwable getCause() {
            return null;
        }
        
        public NoInternet() {
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Throwable component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.rooster.core.network.NetworkError.NoInternet copy(@org.jetbrains.annotations.Nullable()
        java.lang.Throwable cause) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0003\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\f\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u001f\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001R\u0016\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0015"}, d2 = {"Lcom/example/rooster/core/network/NetworkError$ServerError;", "Lcom/example/rooster/core/network/NetworkError;", "code", "", "cause", "", "(ILjava/lang/Throwable;)V", "getCause", "()Ljava/lang/Throwable;", "getCode", "()I", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "toString", "", "core-network_staging"})
    public static final class ServerError extends com.example.rooster.core.network.NetworkError {
        private final int code = 0;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Throwable cause = null;
        
        public ServerError(int code, @org.jetbrains.annotations.Nullable()
        java.lang.Throwable cause) {
        }
        
        public final int getCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.Nullable()
        public java.lang.Throwable getCause() {
            return null;
        }
        
        public final int component1() {
            return 0;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Throwable component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.rooster.core.network.NetworkError.ServerError copy(int code, @org.jetbrains.annotations.Nullable()
        java.lang.Throwable cause) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0011\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0004J\u000b\u0010\u0007\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0015\u0010\b\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0016\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/example/rooster/core/network/NetworkError$Timeout;", "Lcom/example/rooster/core/network/NetworkError;", "cause", "", "(Ljava/lang/Throwable;)V", "getCause", "()Ljava/lang/Throwable;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "core-network_staging"})
    public static final class Timeout extends com.example.rooster.core.network.NetworkError {
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Throwable cause = null;
        
        public Timeout(@org.jetbrains.annotations.Nullable()
        java.lang.Throwable cause) {
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.Nullable()
        public java.lang.Throwable getCause() {
            return null;
        }
        
        public Timeout() {
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Throwable component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.rooster.core.network.NetworkError.Timeout copy(@org.jetbrains.annotations.Nullable()
        java.lang.Throwable cause) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0011\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0004J\u000b\u0010\u0007\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0015\u0010\b\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0016\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/example/rooster/core/network/NetworkError$UnknownError;", "Lcom/example/rooster/core/network/NetworkError;", "cause", "", "(Ljava/lang/Throwable;)V", "getCause", "()Ljava/lang/Throwable;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "core-network_staging"})
    public static final class UnknownError extends com.example.rooster.core.network.NetworkError {
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Throwable cause = null;
        
        public UnknownError(@org.jetbrains.annotations.Nullable()
        java.lang.Throwable cause) {
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.Nullable()
        public java.lang.Throwable getCause() {
            return null;
        }
        
        public UnknownError() {
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Throwable component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.rooster.core.network.NetworkError.UnknownError copy(@org.jetbrains.annotations.Nullable()
        java.lang.Throwable cause) {
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
}