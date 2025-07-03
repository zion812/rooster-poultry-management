package com.example.rooster.core.network;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.serialization.json.Json;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.example.rooster.core.network.GeneralRetrofit")
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
public final class NetworkModule_ProvideGeneralRetrofitFactory implements Factory<Retrofit> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  private final Provider<Json> jsonProvider;

  public NetworkModule_ProvideGeneralRetrofitFactory(Provider<OkHttpClient> okHttpClientProvider,
      Provider<Json> jsonProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
    this.jsonProvider = jsonProvider;
  }

  @Override
  public Retrofit get() {
    return provideGeneralRetrofit(okHttpClientProvider.get(), jsonProvider.get());
  }

  public static NetworkModule_ProvideGeneralRetrofitFactory create(
      Provider<OkHttpClient> okHttpClientProvider, Provider<Json> jsonProvider) {
    return new NetworkModule_ProvideGeneralRetrofitFactory(okHttpClientProvider, jsonProvider);
  }

  public static Retrofit provideGeneralRetrofit(OkHttpClient okHttpClient, Json json) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideGeneralRetrofit(okHttpClient, json));
  }
}
