package com.example.rooster.feature.farm.data.repository;

import com.example.rooster.feature.farm.data.local.FlockDao;
import com.example.rooster.feature.farm.data.remote.FarmRemoteDataSource;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class FarmRepositoryImpl_Factory implements Factory<FarmRepositoryImpl> {
  private final Provider<FlockDao> daoProvider;

  private final Provider<FarmRemoteDataSource> remoteDataSourceProvider;

  public FarmRepositoryImpl_Factory(Provider<FlockDao> daoProvider,
      Provider<FarmRemoteDataSource> remoteDataSourceProvider) {
    this.daoProvider = daoProvider;
    this.remoteDataSourceProvider = remoteDataSourceProvider;
  }

  @Override
  public FarmRepositoryImpl get() {
    return newInstance(daoProvider.get(), remoteDataSourceProvider.get());
  }

  public static FarmRepositoryImpl_Factory create(Provider<FlockDao> daoProvider,
      Provider<FarmRemoteDataSource> remoteDataSourceProvider) {
    return new FarmRepositoryImpl_Factory(daoProvider, remoteDataSourceProvider);
  }

  public static FarmRepositoryImpl newInstance(FlockDao dao,
      FarmRemoteDataSource remoteDataSource) {
    return new FarmRepositoryImpl(dao, remoteDataSource);
  }
}
