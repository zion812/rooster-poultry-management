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
@QualifierMetadata({
    "com.example.rooster.core.network.PaymentApiRetrofit",
    "com.example.rooster.core.network.qualifiers.PaymentApiBaseUrl"
})
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
public final class NetworkModule_ProvidePaymentApiRetrofitFactory implements Factory<Retrofit> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  private final Provider<String> baseUrlProvider;

  private final Provider<Json> jsonProvider;

  public NetworkModule_ProvidePaymentApiRetrofitFactory(Provider<OkHttpClient> okHttpClientProvider,
      Provider<String> baseUrlProvider, Provider<Json> jsonProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
    this.baseUrlProvider = baseUrlProvider;
    this.jsonProvider = jsonProvider;
  }

  @Override
  public Retrofit get() {
    return providePaymentApiRetrofit(okHttpClientProvider.get(), baseUrlProvider.get(), jsonProvider.get());
  }

  public static NetworkModule_ProvidePaymentApiRetrofitFactory create(
      Provider<OkHttpClient> okHttpClientProvider, Provider<String> baseUrlProvider,
      Provider<Json> jsonProvider) {
    return new NetworkModule_ProvidePaymentApiRetrofitFactory(okHttpClientProvider, baseUrlProvider, jsonProvider);
  }

  public static Retrofit providePaymentApiRetrofit(OkHttpClient okHttpClient, String baseUrl,
      Json json) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.providePaymentApiRetrofit(okHttpClient, baseUrl, json));
  }
}
