package com.example.rooster.core.common.event;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppEventBus_Factory implements Factory<AppEventBus> {
  @Override
  public AppEventBus get() {
    return newInstance();
  }

  public static AppEventBus_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AppEventBus newInstance() {
    return new AppEventBus();
  }

  private static final class InstanceHolder {
    private static final AppEventBus_Factory INSTANCE = new AppEventBus_Factory();
  }
}
