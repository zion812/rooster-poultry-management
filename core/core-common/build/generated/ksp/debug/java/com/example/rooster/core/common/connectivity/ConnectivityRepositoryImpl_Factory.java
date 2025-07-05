package com.example.rooster.core.common.connectivity;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
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
public final class ConnectivityRepositoryImpl_Factory implements Factory<ConnectivityRepositoryImpl> {
  private final Provider<Context> contextProvider;

  public ConnectivityRepositoryImpl_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ConnectivityRepositoryImpl get() {
    return newInstance(contextProvider.get());
  }

  public static ConnectivityRepositoryImpl_Factory create(Provider<Context> contextProvider) {
    return new ConnectivityRepositoryImpl_Factory(contextProvider);
  }

  public static ConnectivityRepositoryImpl newInstance(Context context) {
    return new ConnectivityRepositoryImpl(context);
  }
}
