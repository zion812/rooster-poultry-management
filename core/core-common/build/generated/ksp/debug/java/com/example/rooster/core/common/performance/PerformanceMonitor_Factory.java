package com.example.rooster.core.common.performance;

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
public final class PerformanceMonitor_Factory implements Factory<PerformanceMonitor> {
  @Override
  public PerformanceMonitor get() {
    return newInstance();
  }

  public static PerformanceMonitor_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static PerformanceMonitor newInstance() {
    return new PerformanceMonitor();
  }

  private static final class InstanceHolder {
    private static final PerformanceMonitor_Factory INSTANCE = new PerformanceMonitor_Factory();
  }
}
