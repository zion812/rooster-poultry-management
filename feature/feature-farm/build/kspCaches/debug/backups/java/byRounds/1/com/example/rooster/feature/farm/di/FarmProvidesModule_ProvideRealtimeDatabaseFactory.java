package com.example.rooster.feature.farm.di;

import com.google.firebase.database.DatabaseReference;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class FarmProvidesModule_ProvideRealtimeDatabaseFactory implements Factory<DatabaseReference> {
  @Override
  public DatabaseReference get() {
    return provideRealtimeDatabase();
  }

  public static FarmProvidesModule_ProvideRealtimeDatabaseFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DatabaseReference provideRealtimeDatabase() {
    return Preconditions.checkNotNullFromProvides(FarmProvidesModule.INSTANCE.provideRealtimeDatabase());
  }

  private static final class InstanceHolder {
    private static final FarmProvidesModule_ProvideRealtimeDatabaseFactory INSTANCE = new FarmProvidesModule_ProvideRealtimeDatabaseFactory();
  }
}
