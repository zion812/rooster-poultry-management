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
public final class NetworkAwareFarmRepository_Factory implements Factory<NetworkAwareFarmRepository> {
  private final Provider<FlockDao> localDaoProvider;

  private final Provider<FarmRemoteDataSource> remoteDataSourceProvider;

  public NetworkAwareFarmRepository_Factory(Provider<FlockDao> localDaoProvider,
      Provider<FarmRemoteDataSource> remoteDataSourceProvider) {
    this.localDaoProvider = localDaoProvider;
    this.remoteDataSourceProvider = remoteDataSourceProvider;
  }

  @Override
  public NetworkAwareFarmRepository get() {
    return newInstance(localDaoProvider.get(), remoteDataSourceProvider.get());
  }

  public static NetworkAwareFarmRepository_Factory create(Provider<FlockDao> localDaoProvider,
      Provider<FarmRemoteDataSource> remoteDataSourceProvider) {
    return new NetworkAwareFarmRepository_Factory(localDaoProvider, remoteDataSourceProvider);
  }

  public static NetworkAwareFarmRepository newInstance(FlockDao localDao,
      FarmRemoteDataSource remoteDataSource) {
    return new NetworkAwareFarmRepository(localDao, remoteDataSource);
  }
}
