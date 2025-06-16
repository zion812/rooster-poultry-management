package com.example.rooster.feature.farm.di;

import com.example.rooster.feature.farm.data.local.FarmDatabase;
import com.example.rooster.feature.farm.data.local.MortalityDao;
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
public final class FarmProvidesModule_ProvideMortalityDaoFactory implements Factory<MortalityDao> {
  private final Provider<FarmDatabase> dbProvider;

  public FarmProvidesModule_ProvideMortalityDaoFactory(Provider<FarmDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public MortalityDao get() {
    return provideMortalityDao(dbProvider.get());
  }

  public static FarmProvidesModule_ProvideMortalityDaoFactory create(
      Provider<FarmDatabase> dbProvider) {
    return new FarmProvidesModule_ProvideMortalityDaoFactory(dbProvider);
  }

  public static MortalityDao provideMortalityDao(FarmDatabase db) {
    return Preconditions.checkNotNullFromProvides(FarmProvidesModule.INSTANCE.provideMortalityDao(db));
  }
}
