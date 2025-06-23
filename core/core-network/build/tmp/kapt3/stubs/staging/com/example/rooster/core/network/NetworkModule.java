package com.example.rooster.core.network;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0007J\u0012\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\bH\u0007J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0007J\b\u0010\r\u001a\u00020\u000eH\u0007J\b\u0010\u000f\u001a\u00020\u0004H\u0007J,\u0010\u0010\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u000e2\b\b\u0001\u0010\u0012\u001a\u00020\u00042\b\b\u0001\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u0006H\u0007J\u0010\u0010\u0015\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0007\u00a8\u0006\u0016"}, d2 = {"Lcom/example/rooster/core/network/NetworkModule;", "", "()V", "provideAuthInterceptor", "Lokhttp3/Interceptor;", "provideCache", "Lokhttp3/Cache;", "context", "Landroid/content/Context;", "provideGeneralRetrofit", "Lretrofit2/Retrofit;", "okHttpClient", "Lokhttp3/OkHttpClient;", "provideHttpLoggingInterceptor", "Lokhttp3/logging/HttpLoggingInterceptor;", "provideNetworkInterceptor", "provideOkHttpClient", "loggingInterceptor", "authInterceptor", "networkInterceptor", "cache", "provideParseRetrofit", "core-network_staging"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class NetworkModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.example.rooster.core.network.NetworkModule INSTANCE = null;
    
    private NetworkModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final okhttp3.logging.HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @AuthInterceptor()
    @org.jetbrains.annotations.NotNull()
    public final okhttp3.Interceptor provideAuthInterceptor() {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @NetworkInterceptor()
    @org.jetbrains.annotations.NotNull()
    public final okhttp3.Interceptor provideNetworkInterceptor() {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final okhttp3.Cache provideCache(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final okhttp3.OkHttpClient provideOkHttpClient(@org.jetbrains.annotations.NotNull()
    okhttp3.logging.HttpLoggingInterceptor loggingInterceptor, @AuthInterceptor()
    @org.jetbrains.annotations.NotNull()
    okhttp3.Interceptor authInterceptor, @NetworkInterceptor()
    @org.jetbrains.annotations.NotNull()
    okhttp3.Interceptor networkInterceptor, @org.jetbrains.annotations.NotNull()
    okhttp3.Cache cache) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @ParseRetrofit()
    @org.jetbrains.annotations.NotNull()
    public final retrofit2.Retrofit provideParseRetrofit(@org.jetbrains.annotations.NotNull()
    okhttp3.OkHttpClient okHttpClient) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @GeneralRetrofit()
    @org.jetbrains.annotations.NotNull()
    public final retrofit2.Retrofit provideGeneralRetrofit(@org.jetbrains.annotations.NotNull()
    okhttp3.OkHttpClient okHttpClient) {
        return null;
    }
}