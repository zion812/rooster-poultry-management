package com.example.rooster.core.common.di;

import android.content.Context;
import com.example.rooster.core.common.connectivity.ConnectivityRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ConnectivityModule_ProvideConnectivityRepositoryFactory implements Factory<ConnectivityRepository> {
  private final Provider<Context> contextProvider;

  public ConnectivityModule_ProvideConnectivityRepositoryFactory(
      Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ConnectivityRepository get() {
    return provideConnectivityRepository(contextProvider.get());
  }

  public static ConnectivityModule_ProvideConnectivityRepositoryFactory create(
      Provider<Context> contextProvider) {
    return new ConnectivityModule_ProvideConnectivityRepositoryFactory(contextProvider);
  }

  public static ConnectivityRepository provideConnectivityRepository(Context context) {
    return Preconditions.checkNotNullFromProvides(ConnectivityModule.INSTANCE.provideConnectivityRepository(context));
  }
}
