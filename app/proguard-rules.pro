# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/renemtz/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep rules here:

# Retrofit / OkHttp / Gson
-keepattributes Signature, InnerClasses, AnnotationDefault, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleAnnotations, RuntimeInvisibleParameterAnnotations
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class retrofit2.** { *; }
-keepnames class com.google.gson.** { *; }

## Keep DTOs to avoid issues with JSON serialization TEST RELEASE IF THIS IS REQUIRED FOR
#-keep class com.reneprojects.core.feature.products.remote.dto.** { *; }
#-keep class com.reneprojects.core.common.cachemanager.entity.** { *; }
#-keep class com.reneprojects.core.feature.products.local.entity.** { *; }
