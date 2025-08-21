# ================================================================================================
# LINAGE ISP - OPTIMIZED PROGUARD RULES FOR MAXIMUM PERFORMANCE
# ================================================================================================

# ------------------------------------------------------------------------------------------------
# GENERAL OPTIMIZATIONS
# ------------------------------------------------------------------------------------------------

# Enable aggressive optimization
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*,!code/allocation/variable
-optimizationpasses 5
-allowaccessmodification
-dontpreverify
-repackageclasses ''
-mergeinterfacesaggressively
-overloadaggressively

# Keep line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ------------------------------------------------------------------------------------------------
# KOTLIN OPTIMIZATIONS
# ------------------------------------------------------------------------------------------------

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.flow.**

# Kotlin serialization
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-keepclassmembers class **.*$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclasseswithmembers class **.*$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}

# ------------------------------------------------------------------------------------------------
# COMPOSE OPTIMIZATIONS
# ------------------------------------------------------------------------------------------------

# Compose Runtime
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.animation.** { *; }

# Keep Compose Stable and Immutable annotations
-keepattributes RuntimeVisibleAnnotations
-keep @androidx.compose.runtime.Stable class ** { *; }
-keep @androidx.compose.runtime.Immutable class ** { *; }
-keep class androidx.compose.runtime.Stable
-keep class androidx.compose.runtime.Immutable

# Compose compiler optimizations
-assumenosideeffects public class androidx.compose.runtime.ComposerKt {
    boolean isTraceInProgress();
    void traceEventStart(int,int,int,java.lang.String);
    void traceEventEnd();
}

# ------------------------------------------------------------------------------------------------
# LINAGE ISP SPECIFIC RULES
# ------------------------------------------------------------------------------------------------

# Keep all data classes used in UI state
-keep @androidx.compose.runtime.Stable class com.example.Linageisp.presentation.home.** { *; }
-keep @androidx.compose.runtime.Immutable class com.example.Linageisp.presentation.home.** { *; }

# Keep ViewModels
-keep class com.example.Linageisp.viewmodel.** { *; }

# Keep performance monitoring classes
-keep class com.example.Linageisp.core.performance.** { *; }
-keep class com.example.Linageisp.core.animation.** { *; }

# Keep enum classes
-keepclassmembers enum com.example.Linageisp.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ------------------------------------------------------------------------------------------------
# FIREBASE OPTIMIZATIONS
# ------------------------------------------------------------------------------------------------

# Firebase Crashlytics
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**

# Firebase Performance
-keep class com.google.firebase.perf.** { *; }
-keep class com.google.android.gms.** { *; }

# Firebase Analytics
-keep class com.google.firebase.analytics.** { *; }
-keep class com.google.android.gms.measurement.** { *; }

# ------------------------------------------------------------------------------------------------
# COIL IMAGE LOADING OPTIMIZATIONS
# ------------------------------------------------------------------------------------------------

# Coil
-keep class coil.** { *; }
-keep interface coil.** { *; }
-dontwarn coil.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# ------------------------------------------------------------------------------------------------
# NETWORKING AND HTTP
# ------------------------------------------------------------------------------------------------

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions*

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# JSoup
-keep class org.jsoup.** { *; }
-keep interface org.jsoup.** { *; }

# ------------------------------------------------------------------------------------------------
# AI AND GENERATIVE AI
# ------------------------------------------------------------------------------------------------

# Google Generative AI
-keep class com.google.ai.client.generativeai.** { *; }
-dontwarn com.google.ai.client.generativeai.**

# ------------------------------------------------------------------------------------------------
# PERFORMANCE AND MEMORY OPTIMIZATION
# ------------------------------------------------------------------------------------------------

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Remove debug code
-assumenosideeffects class java.io.PrintStream {
    public void println(%);
    public void println(**);
}

# Optimize string concatenation
-assumenosideeffects class java.lang.StringBuilder {
    public java.lang.StringBuilder();
    public java.lang.StringBuilder(int);
    public java.lang.StringBuilder(java.lang.String);
    public java.lang.StringBuilder append(java.lang.Object);
    public java.lang.StringBuilder append(java.lang.String);
    public java.lang.StringBuilder append(java.lang.StringBuffer);
    public java.lang.StringBuilder append(char[]);
    public java.lang.StringBuilder append(char[], int, int);
    public java.lang.StringBuilder append(boolean);
    public java.lang.StringBuilder append(char);
    public java.lang.StringBuilder append(int);
    public java.lang.StringBuilder append(long);
    public java.lang.StringBuilder append(float);
    public java.lang.StringBuilder append(double);
    public java.lang.String toString();
}

# ------------------------------------------------------------------------------------------------
# REMOVE UNUSED CODE
# ------------------------------------------------------------------------------------------------

# Remove unused resources
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

# Remove unused Kotlin metadata
-dontwarn kotlin.**
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkExpressionValueIsNotNull(java.lang.Object, java.lang.String);
    public static void checkNotNullExpressionValue(java.lang.Object, java.lang.String);
    public static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String);
    public static void checkFieldIsNotNull(java.lang.Object, java.lang.String);
    public static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

# ------------------------------------------------------------------------------------------------
# FINAL OPTIMIZATIONS
# ------------------------------------------------------------------------------------------------

# Aggressive dead code elimination
-dontwarn java.lang.invoke.**
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# Keep class names for crash reports
-keepnames class com.example.Linageisp.** { *; }

# ------------------------------------------------------------------------------------------------
# 16KB ALIGNMENT COMPATIBILITY RULES
# ------------------------------------------------------------------------------------------------

# Remove problematic trace processor and tracebox files
-dontwarn assets.trace_processor_shell_aarch64
-dontwarn assets.trace_processor_shell_x86_64
-dontwarn assets.tracebox_aarch64
-dontwarn assets.tracebox_x86_64

# Exclude problematic performance monitoring tools
-dontwarn androidx.benchmark.**
-dontwarn androidx.tracing.perfetto.**

# Additional 16KB alignment optimizations
-assumenosideeffects class androidx.benchmark.** {
    public static *** *(...);
}

-assumenosideeffects class androidx.tracing.** {
    public static *** *(...);
}