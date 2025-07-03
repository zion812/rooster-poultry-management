package com.example.rooster.core.network;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata({
    "com.example.rooster.core.network.AuthInterceptor",
    "com.example.rooster.core.network.NetworkInterceptor"
})
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class NetworkModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<HttpLoggingInterceptor> loggingInterceptorProvider;

  private final Provider<Interceptor> authInterceptorProvider;

  private final Provider<Interceptor> networkInterceptorProvider;

  private final Provider<TokenAuthenticator> tokenAuthenticatorProvider;

  private final Provider<Cache> cacheProvider;

  public NetworkModule_ProvideOkHttpClientFactory(
      Provider<HttpLoggingInterceptor> loggingInterceptorProvider,
      Provider<Interceptor> authInterceptorProvider,
      Provider<Interceptor> networkInterceptorProvider,
      Provider<TokenAuthenticator> tokenAuthenticatorProvider, Provider<Cache> cacheProvider) {
    this.loggingInterceptorProvider = loggingInterceptorProvider;
    this.authInterceptorProvider = authInterceptorProvider;
    this.networkInterceptorProvider = networkInterceptorProvider;
    this.tokenAuthenticatorProvider = tokenAuthenticatorProvider;
    this.cacheProvider = cacheProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(loggingInterceptorProvider.get(), authInterceptorProvider.get(), networkInterceptorProvider.get(), tokenAuthenticatorProvider.get(), cacheProvider.get());
  }

  public static NetworkModule_ProvideOkHttpClientFactory create(
      Provider<HttpLoggingInterceptor> loggingInterceptorProvider,
      Provider<Interceptor> authInterceptorProvider,
      Provider<Interceptor> networkInterceptorProvider,
      Provider<TokenAuthenticator> tokenAuthenticatorProvider, Provider<Cache> cacheProvider) {
    return new NetworkModule_ProvideOkHttpClientFactory(loggingInterceptorProvider, authInterceptorProvider, networkInterceptorProvider, tokenAuthenticatorProvider, cacheProvider);
  }

  public static OkHttpClient provideOkHttpClient(HttpLoggingInterceptor loggingInterceptor,
      Interceptor authInterceptor, Interceptor networkInterceptor,
      TokenAuthenticator tokenAuthenticator, Cache cache) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttpClient(loggingInterceptor, authInterceptor, networkInterceptor, tokenAuthenticator, cache));
  }
}
