# ExerciseDay is persisted as Gson JSON by MovesTypeConverter. Existing installs
# contain the original field names; preserve this small serialization contract.
# Navigation XML also names these Parcelable argument types via reflection.
-keep,allowoptimization class com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
-keepnames class com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
# Hilt 2.51 builds a map keyed by ViewModel class identity. Merging these
# classes produces duplicate map keys at startup; names may still be shortened.
-keep,allowobfuscation @dagger.hilt.android.lifecycle.HiltViewModel class *
-keepclassmembers class com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay {
    <fields>;
}

# Keep source locations useful in retraced Crashlytics/Play reports.
-keepattributes SourceFile,LineNumberTable

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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
