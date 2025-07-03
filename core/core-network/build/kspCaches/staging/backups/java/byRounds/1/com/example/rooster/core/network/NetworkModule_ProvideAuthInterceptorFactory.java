package com.example.rooster.core.network;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.Interceptor;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.example.rooster.core.network.AuthInterceptor")
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
public final class NetworkModule_ProvideAuthInterceptorFactory implements Factory<Interceptor> {
  private final Provider<TokenProvider> tokenProvider;

  public NetworkModule_ProvideAuthInterceptorFactory(Provider<TokenProvider> tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  @Override
  public Interceptor get() {
    return provideAuthInterceptor(tokenProvider.get());
  }

  public static NetworkModule_ProvideAuthInterceptorFactory create(
      Provider<TokenProvider> tokenProvider) {
    return new NetworkModule_ProvideAuthInterceptorFactory(tokenProvider);
  }

  public static Interceptor provideAuthInterceptor(TokenProvider tokenProvider) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideAuthInterceptor(tokenProvider));
  }
}
