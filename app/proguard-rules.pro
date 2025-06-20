# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Firebase specific rules
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Hilt specific rules
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel
-keep class * extends androidx.lifecycle.ViewModel

# Room specific rules
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Compose specific rules
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Kotlin specific rules
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**

# Remove unused native libraries
-keepclassmembers class * {
    native <methods>;
}

# Keep model classes for Firebase/API
-keep class com.example.rooster.data.models.** { *; }

# Keep enums
-keepclassmembers enum * { *; }

# Optimize and shrink code
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

# Remove logging
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Navigation component
-keep class androidx.navigation.** { *; }

# Telugu localization strings
-keep class com.example.rooster.R$string { *; }

# Razorpay specific rules
# Keep Razorpay SDK classes
-keep class com.razorpay.** { *; }
-keepattributes *Annotation*
-dontwarn com.razorpay.**

# Keep ProGuard annotations used by Razorpay
-keep class proguard.annotation.Keep
-keep class proguard.annotation.KeepClassMembers

# Keep all classes with @Keep annotation
-keep @proguard.annotation.Keep class * { *; }

# Keep all class members with @Keep annotation
-keepclassmembers class * {
    @proguard.annotation.Keep *;
}

# Gson specific classes (used by Razorpay)
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# OkHttp and Retrofit (used by Razorpay)
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
