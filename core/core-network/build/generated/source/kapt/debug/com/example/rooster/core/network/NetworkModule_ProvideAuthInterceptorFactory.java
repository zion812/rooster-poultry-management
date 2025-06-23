package com.example.rooster.core.network;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
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
  @Override
  public Interceptor get() {
    return provideAuthInterceptor();
  }

  public static NetworkModule_ProvideAuthInterceptorFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static Interceptor provideAuthInterceptor() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideAuthInterceptor());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideAuthInterceptorFactory INSTANCE = new NetworkModule_ProvideAuthInterceptorFactory();
  }
}
