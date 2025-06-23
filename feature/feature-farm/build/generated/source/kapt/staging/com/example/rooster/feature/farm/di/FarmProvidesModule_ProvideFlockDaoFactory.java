package com.example.rooster.feature.farm.di;

import com.example.rooster.feature.farm.data.local.FarmDatabase;
import com.example.rooster.feature.farm.data.local.FlockDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
    "KotlinInternalInJava"
})
public final class FarmProvidesModule_ProvideFlockDaoFactory implements Factory<FlockDao> {
  private final Provider<FarmDatabase> dbProvider;

  public FarmProvidesModule_ProvideFlockDaoFactory(Provider<FarmDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public FlockDao get() {
    return provideFlockDao(dbProvider.get());
  }

  public static FarmProvidesModule_ProvideFlockDaoFactory create(
      Provider<FarmDatabase> dbProvider) {
    return new FarmProvidesModule_ProvideFlockDaoFactory(dbProvider);
  }

  public static FlockDao provideFlockDao(FarmDatabase db) {
    return Preconditions.checkNotNullFromProvides(FarmProvidesModule.INSTANCE.provideFlockDao(db));
  }
}
