package com.example.rooster.core.network;

import com.example.rooster.core.network.retrofit.PaymentApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.example.rooster.core.network.PaymentApiRetrofit")
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
public final class NetworkModule_ProvidePaymentApiServiceFactory implements Factory<PaymentApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvidePaymentApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public PaymentApiService get() {
    return providePaymentApiService(retrofitProvider.get());
  }

  public static NetworkModule_ProvidePaymentApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvidePaymentApiServiceFactory(retrofitProvider);
  }

  public static PaymentApiService providePaymentApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.providePaymentApiService(retrofit));
  }
}
