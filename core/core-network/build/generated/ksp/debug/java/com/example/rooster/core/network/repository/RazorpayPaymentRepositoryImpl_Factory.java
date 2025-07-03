package com.example.rooster.core.network.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
    "KotlinInternalInJava",
    "cast"
})
public final class RazorpayPaymentRepositoryImpl_Factory implements Factory<RazorpayPaymentRepositoryImpl> {
  @Override
  public RazorpayPaymentRepositoryImpl get() {
    return newInstance();
  }

  public static RazorpayPaymentRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RazorpayPaymentRepositoryImpl newInstance() {
    return new RazorpayPaymentRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final RazorpayPaymentRepositoryImpl_Factory INSTANCE = new RazorpayPaymentRepositoryImpl_Factory();
  }
}
