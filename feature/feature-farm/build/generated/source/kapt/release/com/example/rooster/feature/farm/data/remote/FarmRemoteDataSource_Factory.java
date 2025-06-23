package com.example.rooster.feature.farm.data.remote;

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
    "KotlinInternalInJava"
})
public final class FarmRemoteDataSource_Factory implements Factory<FarmRemoteDataSource> {
  @Override
  public FarmRemoteDataSource get() {
    return newInstance();
  }

  public static FarmRemoteDataSource_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FarmRemoteDataSource newInstance() {
    return new FarmRemoteDataSource();
  }

  private static final class InstanceHolder {
    private static final FarmRemoteDataSource_Factory INSTANCE = new FarmRemoteDataSource_Factory();
  }
}
