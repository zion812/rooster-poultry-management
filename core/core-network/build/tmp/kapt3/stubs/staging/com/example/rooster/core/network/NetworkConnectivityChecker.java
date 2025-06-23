package com.example.rooster.core.network;

/**
 * Network connectivity checker
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0005H&J\b\u0010\u0007\u001a\u00020\u0005H&\u00a8\u0006\b"}, d2 = {"Lcom/example/rooster/core/network/NetworkConnectivityChecker;", "", "getConnectionType", "Lcom/example/rooster/core/network/ConnectionType;", "isConnected", "", "isConnectedToMobile", "isConnectedToWifi", "core-network_staging"})
public abstract interface NetworkConnectivityChecker {
    
    public abstract boolean isConnected();
    
    public abstract boolean isConnectedToWifi();
    
    public abstract boolean isConnectedToMobile();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.rooster.core.network.ConnectionType getConnectionType();
}