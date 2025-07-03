package com.example.rooster.core.analytics;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class FirebaseAnalyticsService_Factory implements Factory<FirebaseAnalyticsService> {
  private final Provider<Context> contextProvider;

  public FirebaseAnalyticsService_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public FirebaseAnalyticsService get() {
    return newInstance(contextProvider.get());
  }

  public static FirebaseAnalyticsService_Factory create(Provider<Context> contextProvider) {
    return new FirebaseAnalyticsService_Factory(contextProvider);
  }

  public static FirebaseAnalyticsService newInstance(Context context) {
    return new FirebaseAnalyticsService(context);
  }
}
