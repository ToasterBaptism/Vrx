#!/bin/bash
# Android Development Environment Validation Script
# This script validates the Android development environment

set -e  # Exit on error

# Configuration variables
INSTALL_DIR="${1:-$HOME/android-dev}"
JAVA_HOME="$INSTALL_DIR/jdk"
ANDROID_SDK_ROOT="$INSTALL_DIR/android-sdk"
ANDROID_HOME="$ANDROID_SDK_ROOT"
ANDROID_NDK_HOME="$ANDROID_SDK_ROOT/ndk/25.2.9519653"
GRADLE_HOME="$INSTALL_DIR/gradle"
ANDROID_STUDIO_DIR="$INSTALL_DIR/android-studio"

echo "=== Android Development Environment Validation ==="
echo "Installation directory: $INSTALL_DIR"
echo "Starting validation at $(date)"
echo

# Source environment variables if available
if [[ -f "$INSTALL_DIR/env-setup.sh" ]]; then
    source "$INSTALL_DIR/env-setup.sh"
    echo "Environment variables loaded from $INSTALL_DIR/env-setup.sh"
else
    echo "Warning: Environment setup script not found at $INSTALL_DIR/env-setup.sh"
    echo "Using default environment variables."
fi

# Validate JDK
echo "=== Validating JDK ==="
if [[ -d "$JAVA_HOME" ]]; then
    echo "JDK directory exists at $JAVA_HOME"
    
    if "$JAVA_HOME/bin/java" -version; then
        echo "✓ Java is installed and working."
    else
        echo "✗ Java installation failed or not in PATH."
    fi
else
    echo "✗ JDK directory not found at $JAVA_HOME"
fi
echo

# Validate Android SDK
echo "=== Validating Android SDK ==="
if [[ -d "$ANDROID_SDK_ROOT" ]]; then
    echo "Android SDK directory exists at $ANDROID_SDK_ROOT"
    
    if [[ -f "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" ]]; then
        echo "✓ Android SDK Command Line Tools are installed."
        
        echo "Installed packages:"
        "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" --list_installed | grep -v "Available"
    else
        echo "✗ Android SDK Command Line Tools not found."
    fi
    
    if [[ -d "$ANDROID_SDK_ROOT/platform-tools" ]]; then
        echo "✓ Platform tools are installed."
        
        if [[ -f "$ANDROID_SDK_ROOT/platform-tools/adb" ]]; then
            echo "ADB version:"
            "$ANDROID_SDK_ROOT/platform-tools/adb" --version
        fi
    else
        echo "✗ Platform tools not found."
    fi
    
    if [[ -d "$ANDROID_SDK_ROOT/build-tools" ]]; then
        echo "✓ Build tools are installed."
        echo "Available build tools versions:"
        ls -1 "$ANDROID_SDK_ROOT/build-tools"
    else
        echo "✗ Build tools not found."
    fi
    
    if [[ -d "$ANDROID_SDK_ROOT/platforms" ]]; then
        echo "✓ Android platforms are installed."
        echo "Available platform versions:"
        ls -1 "$ANDROID_SDK_ROOT/platforms"
    else
        echo "✗ Android platforms not found."
    fi
else
    echo "✗ Android SDK directory not found at $ANDROID_SDK_ROOT"
fi
echo

# Validate Android NDK
echo "=== Validating Android NDK ==="
if [[ -d "$ANDROID_NDK_HOME" ]]; then
    echo "✓ Android NDK directory exists at $ANDROID_NDK_HOME"
    
    if [[ -f "$ANDROID_NDK_HOME/ndk-build" ]]; then
        echo "✓ NDK build tool is available."
    else
        echo "✗ NDK build tool not found."
    fi
    
    if [[ -f "$ANDROID_NDK_HOME/source.properties" ]]; then
        echo "NDK version:"
        cat "$ANDROID_NDK_HOME/source.properties" | grep "Pkg.Revision"
    fi
else
    echo "✗ Android NDK directory not found at $ANDROID_NDK_HOME"
fi
echo

# Validate Gradle
echo "=== Validating Gradle ==="
if [[ -d "$GRADLE_HOME" ]]; then
    echo "✓ Gradle directory exists at $GRADLE_HOME"
    
    if "$GRADLE_HOME/bin/gradle" --version; then
        echo "✓ Gradle is installed and working."
    else
        echo "✗ Gradle installation failed or not in PATH."
    fi
else
    echo "✗ Gradle directory not found at $GRADLE_HOME"
fi
echo

# Validate Android Studio
echo "=== Validating Android Studio ==="
if [[ -d "$ANDROID_STUDIO_DIR" ]]; then
    echo "✓ Android Studio directory exists at $ANDROID_STUDIO_DIR"
    
    if [[ -f "$ANDROID_STUDIO_DIR/bin/studio.sh" ]]; then
        echo "✓ Android Studio launcher is available."
    else
        echo "✗ Android Studio launcher not found."
    fi
else
    echo "✗ Android Studio directory not found at $ANDROID_STUDIO_DIR"
fi
echo

# Validate AVD
echo "=== Validating Android Virtual Devices ==="
if [[ -d "$HOME/.android/avd" ]]; then
    echo "✓ AVD directory exists at $HOME/.android/avd"
    
    if [[ -f "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/avdmanager" ]]; then
        echo "Available AVDs:"
        "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/avdmanager" list avd
    else
        echo "✗ AVD manager not found."
    fi
else
    echo "✗ AVD directory not found at $HOME/.android/avd"
fi
echo

# Validate environment variables
echo "=== Validating Environment Variables ==="
if [[ -n "$JAVA_HOME" ]]; then
    echo "✓ JAVA_HOME is set to $JAVA_HOME"
else
    echo "✗ JAVA_HOME is not set."
fi

if [[ -n "$ANDROID_SDK_ROOT" ]]; then
    echo "✓ ANDROID_SDK_ROOT is set to $ANDROID_SDK_ROOT"
else
    echo "✗ ANDROID_SDK_ROOT is not set."
fi

if [[ -n "$ANDROID_HOME" ]]; then
    echo "✓ ANDROID_HOME is set to $ANDROID_HOME"
else
    echo "✗ ANDROID_HOME is not set."
fi

if [[ -n "$ANDROID_NDK_HOME" ]]; then
    echo "✓ ANDROID_NDK_HOME is set to $ANDROID_NDK_HOME"
else
    echo "✗ ANDROID_NDK_HOME is not set."
fi

if [[ -n "$GRADLE_HOME" ]]; then
    echo "✓ GRADLE_HOME is set to $GRADLE_HOME"
else
    echo "✗ GRADLE_HOME is not set."
fi
echo

# Check PATH
echo "=== Validating PATH ==="
if echo "$PATH" | grep -q "$JAVA_HOME/bin"; then
    echo "✓ JAVA_HOME/bin is in PATH"
else
    echo "✗ JAVA_HOME/bin is not in PATH."
fi

if echo "$PATH" | grep -q "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin"; then
    echo "✓ Android SDK Command Line Tools are in PATH"
else
    echo "✗ Android SDK Command Line Tools are not in PATH."
fi

if echo "$PATH" | grep -q "$ANDROID_SDK_ROOT/platform-tools"; then
    echo "✓ Android Platform Tools are in PATH"
else
    echo "✗ Android Platform Tools are not in PATH."
fi

if echo "$PATH" | grep -q "$ANDROID_NDK_HOME"; then
    echo "✓ Android NDK is in PATH"
else
    echo "✗ Android NDK is not in PATH."
fi

if echo "$PATH" | grep -q "$GRADLE_HOME/bin"; then
    echo "✓ Gradle is in PATH"
else
    echo "✗ Gradle is not in PATH."
fi
echo

# Summary
echo "=== Validation Summary ==="
echo "JDK: $(if [[ -d "$JAVA_HOME" && -f "$JAVA_HOME/bin/java" ]]; then echo "✓"; else echo "✗"; fi)"
echo "Android SDK: $(if [[ -d "$ANDROID_SDK_ROOT" && -f "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" ]]; then echo "✓"; else echo "✗"; fi)"
echo "Android NDK: $(if [[ -d "$ANDROID_NDK_HOME" ]]; then echo "✓"; else echo "✗"; fi)"
echo "Gradle: $(if [[ -d "$GRADLE_HOME" && -f "$GRADLE_HOME/bin/gradle" ]]; then echo "✓"; else echo "✗"; fi)"
echo "Android Studio: $(if [[ -d "$ANDROID_STUDIO_DIR" ]]; then echo "✓"; else echo "✗"; fi)"
echo "Environment Variables: $(if [[ -n "$JAVA_HOME" && -n "$ANDROID_SDK_ROOT" && -n "$ANDROID_HOME" && -n "$ANDROID_NDK_HOME" && -n "$GRADLE_HOME" ]]; then echo "✓"; else echo "✗"; fi)"
echo

echo "=== Validation Completed at $(date) ==="