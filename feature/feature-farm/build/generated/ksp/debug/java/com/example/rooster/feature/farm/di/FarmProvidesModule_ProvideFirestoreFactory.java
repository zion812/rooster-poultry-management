package com.example.rooster.feature.farm.di;

import com.google.firebase.firestore.FirebaseFirestore;
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
public final class FarmProvidesModule_ProvideFirestoreFactory implements Factory<FirebaseFirestore> {
  @Override
  public FirebaseFirestore get() {
    return provideFirestore();
  }

  public static FarmProvidesModule_ProvideFirestoreFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirebaseFirestore provideFirestore() {
    return Preconditions.checkNotNullFromProvides(FarmProvidesModule.INSTANCE.provideFirestore());
  }

  private static final class InstanceHolder {
    private static final FarmProvidesModule_ProvideFirestoreFactory INSTANCE = new FarmProvidesModule_ProvideFirestoreFactory();
  }
}
