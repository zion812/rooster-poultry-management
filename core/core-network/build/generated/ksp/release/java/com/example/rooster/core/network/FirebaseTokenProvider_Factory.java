package com.example.rooster.core.network;

import com.google.firebase.auth.FirebaseAuth;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class FirebaseTokenProvider_Factory implements Factory<FirebaseTokenProvider> {
  private final Provider<FirebaseAuth> firebaseAuthProvider;

  public FirebaseTokenProvider_Factory(Provider<FirebaseAuth> firebaseAuthProvider) {
    this.firebaseAuthProvider = firebaseAuthProvider;
  }

  @Override
  public FirebaseTokenProvider get() {
    return newInstance(firebaseAuthProvider.get());
  }

  public static FirebaseTokenProvider_Factory create(Provider<FirebaseAuth> firebaseAuthProvider) {
    return new FirebaseTokenProvider_Factory(firebaseAuthProvider);
  }

  public static FirebaseTokenProvider newInstance(FirebaseAuth firebaseAuth) {
    return new FirebaseTokenProvider(firebaseAuth);
  }
}
