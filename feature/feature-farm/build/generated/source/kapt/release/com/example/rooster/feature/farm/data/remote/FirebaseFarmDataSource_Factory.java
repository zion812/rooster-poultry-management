package com.example.rooster.feature.farm.data.remote;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
    "KotlinInternalInJava"
})
public final class FirebaseFarmDataSource_Factory implements Factory<FirebaseFarmDataSource> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<DatabaseReference> realtimeDatabaseProvider;

  public FirebaseFarmDataSource_Factory(Provider<FirebaseFirestore> firestoreProvider,
      Provider<DatabaseReference> realtimeDatabaseProvider) {
    this.firestoreProvider = firestoreProvider;
    this.realtimeDatabaseProvider = realtimeDatabaseProvider;
  }

  @Override
  public FirebaseFarmDataSource get() {
    return newInstance(firestoreProvider.get(), realtimeDatabaseProvider.get());
  }

  public static FirebaseFarmDataSource_Factory create(Provider<FirebaseFirestore> firestoreProvider,
      Provider<DatabaseReference> realtimeDatabaseProvider) {
    return new FirebaseFarmDataSource_Factory(firestoreProvider, realtimeDatabaseProvider);
  }

  public static FirebaseFarmDataSource newInstance(FirebaseFirestore firestore,
      DatabaseReference realtimeDatabase) {
    return new FirebaseFarmDataSource(firestore, realtimeDatabase);
  }
}
