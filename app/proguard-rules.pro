# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep important classes for VR rendering
-keep class org.rajawali3d.** { *; }
-keep class com.google.vr.** { *; }

# Keep controller input classes
-keep class android.hardware.usb.** { *; }
-keep class android.bluetooth.** { *; }

# Keep sensor classes
-keep class android.hardware.Sensor
-keep class android.hardware.SensorEvent
-keep class android.hardware.SensorManager
-keep class android.hardware.SensorEventListener { *; }

# Keep OpenGL classes
-keep class javax.microedition.khronos.** { *; }
-keep class android.opengl.** { *; }

# Keep annotation processors
-keep class androidx.hilt.** { *; }
-keep class dagger.hilt.** { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }

# Keep USB serial classes
-keep class com.hoho.android.usbserial.** { *; }

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile