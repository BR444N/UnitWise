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

# Lottie animations — clase completa protegida para evitar problemas de reflexión en JSON parser
-keep class com.airbnb.lottie.model.** { *; }

# ML Kit Common — MlKitInitProvider se registra como ContentProvider en el manifest
# y corre ANTES de Application.onCreate(). Si sus clases son eliminadas, la app crashea al startup.
-keep class com.google.mlkit.common.** { *; }

# ML Kit Text Recognition — API pública
-keep class com.google.mlkit.vision.text.** { *; }
-keep class com.google.mlkit.vision.common.** { *; }

# Firebase Component Framework — ML Kit usa Firebase DI internamente aunque no uses Firebase.
# ComponentRegistrar se registra por NOMBRE en el manifest (meta-data), R8 no detecta que son necesarios.
-keep class com.google.firebase.components.** { *; }
-keep class com.google.firebase.provider.** { *; }
-keep class * implements com.google.firebase.components.ComponentRegistrar

# GMS internal — clases nativas JNI del modelo bundled
-keep class com.google.android.gms.internal.mlkit_vision_text_bundled.** { *; }

# GMS Tasks API — usado por ML Kit para resultados asincrónicos (Task<T>)
-keep class com.google.android.gms.tasks.** { *; }

# Suprimir warnings de ML Kit y Firebase internals
-dontwarn com.google.mlkit.**
-dontwarn com.google.firebase.**