# Room Entity
-keep class com.lifelog.app.data.local.entity.** { *; }
-keep class com.lifelog.app.data.local.dao.** { *; }

# Gson models
-keep class com.lifelog.app.data.model.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Compose
-dontwarn androidx.compose.**

# Coroutines
-dontwarn kotlinx.coroutines.**

# Kotlin
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
