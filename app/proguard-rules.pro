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

# Preserve line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# Room persistence library
-keep class com.br444n.unitwise.app.data.local.entity.** { *; }
-keep interface com.br444n.unitwise.app.data.local.dao.** { *; }

# Lottie animations - Narrow to model classes
-keepclassmembers class com.airbnb.lottie.model.** { *; }

# ML Kit Text Recognition — API pública de text recognition y common (InputImage, etc.)
-keep class com.google.mlkit.vision.text.** { *; }
-keep class com.google.mlkit.vision.common.** { *; }
-dontwarn com.google.mlkit.**
# GMS internal — clases internas generadas para JNI/modelo nativo; debe quedar amplia
-keep class com.google.android.gms.internal.mlkit_vision_text_bundled.** { *; }
# GMS Tasks API — usado por ML Kit para retornar resultados asincrónicos (Task<T>)
-keep class com.google.android.gms.tasks.** { *; }