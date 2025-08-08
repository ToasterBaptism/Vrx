#!/bin/bash
# Android SDK Installation Script
# This script installs the Android SDK for development

set -e  # Exit on error

# Configuration variables
INSTALL_DIR="${1:-$HOME/android-dev}"
ANDROID_SDK_ROOT="$INSTALL_DIR/android-sdk"
ANDROID_HOME="$ANDROID_SDK_ROOT"
CMDLINE_TOOLS_VERSION="11.0"
BUILD_TOOLS_VERSION="34.0.0"
PLATFORM_VERSION="34"
PLATFORM_VERSION_FALLBACK="33"

# Create installation directory
mkdir -p "$INSTALL_DIR"
mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools"

echo "=== Installing Android SDK ==="
echo "Installation directory: $INSTALL_DIR"
echo "Starting installation at $(date)"
echo

# Function to detect OS
detect_os() {
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        echo "linux"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        echo "macos"
    else
        echo "unsupported"
    fi
}

OS_TYPE=$(detect_os)
if [[ "$OS_TYPE" == "unsupported" ]]; then
    echo "Unsupported operating system: $OSTYPE"
    exit 1
fi

echo "Detected OS: $OS_TYPE"

# Install Android SDK
echo "=== Installing Android SDK Command Line Tools ==="

if [[ "$OS_TYPE" == "linux" ]]; then
    wget -O cmdline-tools.zip "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_VERSION}_latest.zip"
elif [[ "$OS_TYPE" == "macos" ]]; then
    wget -O cmdline-tools.zip "https://dl.google.com/android/repository/commandlinetools-mac-${CMDLINE_TOOLS_VERSION}_latest.zip"
fi

unzip -q cmdline-tools.zip -d "$ANDROID_SDK_ROOT/cmdline-tools/temp"
mv "$ANDROID_SDK_ROOT/cmdline-tools/temp/cmdline-tools" "$ANDROID_SDK_ROOT/cmdline-tools/latest"
rmdir "$ANDROID_SDK_ROOT/cmdline-tools/temp"
rm cmdline-tools.zip

echo "Android SDK Command Line Tools installed at $ANDROID_SDK_ROOT/cmdline-tools/latest"

# Configure environment variables
echo "=== Configuring environment variables ==="

# Create environment setup script
cat > "$INSTALL_DIR/android-env.sh" << EOL
#!/bin/bash
# Android SDK Environment Variables

export ANDROID_SDK_ROOT="$ANDROID_SDK_ROOT"
export ANDROID_HOME="$ANDROID_HOME"
export PATH="\$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:\$ANDROID_SDK_ROOT/platform-tools:\$PATH"
EOL

chmod +x "$INSTALL_DIR/android-env.sh"

echo "Environment setup script created at $INSTALL_DIR/android-env.sh"
echo "To activate Android SDK in your current shell, run:"
echo "source \"$INSTALL_DIR/android-env.sh\""

# Source the environment variables for the current session
source "$INSTALL_DIR/android-env.sh"

# Accept licenses
echo "=== Accepting SDK licenses ==="
yes | "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" --licenses

# Install platform tools
echo "=== Installing platform tools ==="
"$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" "platform-tools"

# Install build tools
echo "=== Installing build tools $BUILD_TOOLS_VERSION ==="
"$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" "build-tools;$BUILD_TOOLS_VERSION"

# Install platforms
echo "=== Installing platform $PLATFORM_VERSION ==="
"$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" "platforms;android-$PLATFORM_VERSION"

echo "=== Installing platform $PLATFORM_VERSION_FALLBACK (fallback) ==="
"$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" "platforms;android-$PLATFORM_VERSION_FALLBACK"

# Install system images for emulator
echo "=== Installing system images for emulator ==="
"$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" "system-images;android-$PLATFORM_VERSION;google_apis_playstore;x86_64"
"$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" "system-images;android-$PLATFORM_VERSION;google_apis;x86_64"

# Install emulator
echo "=== Installing Android Emulator ==="
"$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" "emulator"

# Install Google Play services
echo "=== Installing Google Play services ==="
"$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" "extras;google;google_play_services"

# Install sources for SDK platforms
echo "=== Installing sources for SDK platforms ==="
"$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" "sources;android-$PLATFORM_VERSION"

echo "=== Android SDK Installation Completed ==="
echo "Android SDK is now installed and configured."
echo "SDK location: $ANDROID_SDK_ROOT"