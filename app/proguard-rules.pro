# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# Room persistence library
-keep class com.br444n.unitwise.app.data.local.entity.** { *; }
-keep interface com.br444n.unitwise.app.data.local.dao.** { *; }

# Lottie animations
-keep class com.airbnb.lottie.model.** { *; }

# ML Kit Common
-keep class com.google.mlkit.common.** { *; }

# ML Kit Text Recognition
-keep class com.google.mlkit.vision.text.** { *; }
-keep class com.google.mlkit.vision.common.** { *; }

# Firebase Component Framework
-keep class com.google.firebase.components.** { *; }
-keep class com.google.firebase.provider.** { *; }
-keep class * implements com.google.firebase.components.ComponentRegistrar

# GMS internal
-keep class com.google.android.gms.internal.mlkit_vision_text_bundled.** { *; }

# GMS Tasks API
-keep class com.google.android.gms.tasks.** { *; }

# Suprimir warnings de ML Kit y Firebase internals
-dontwarn com.google.mlkit.**
-dontwarn com.google.firebase.**

# Kotlin Serialization
-keepattributes *Annotation*, EnclosingMethod, InnerClasses, Signature
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
    @kotlinx.serialization.SerialName *;
}
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers class * extends kotlinx.serialization.KSerializer {
    public static ** INSTANCE;
}

# Ktor & Supabase
-keep class io.ktor.** { *; }
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.ktor.**
-dontwarn io.github.jan.supabase.**
-dontwarn kotlinx.coroutines.debug.DebugProbes
-dontwarn okio.Okio