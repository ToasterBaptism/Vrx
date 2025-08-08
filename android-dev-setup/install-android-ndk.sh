#!/bin/bash
# Android NDK Installation Script
# This script installs the Android NDK for development

set -e  # Exit on error

# Configuration variables
INSTALL_DIR="${1:-$HOME/android-dev}"
ANDROID_SDK_ROOT="$INSTALL_DIR/android-sdk"
ANDROID_HOME="$ANDROID_SDK_ROOT"
NDK_VERSION="25.2.9519653"
ANDROID_NDK_HOME="$ANDROID_SDK_ROOT/ndk/$NDK_VERSION"

# Check if Android SDK is installed
if [[ ! -d "$ANDROID_SDK_ROOT/cmdline-tools/latest" ]]; then
    echo "Error: Android SDK not found at $ANDROID_SDK_ROOT"
    echo "Please install Android SDK first using install-android-sdk.sh"
    exit 1
fi

echo "=== Installing Android NDK $NDK_VERSION ==="
echo "SDK directory: $ANDROID_SDK_ROOT"
echo "Starting installation at $(date)"
echo

# Source Android SDK environment variables
if [[ -f "$INSTALL_DIR/android-env.sh" ]]; then
    source "$INSTALL_DIR/android-env.sh"
else
    echo "Warning: Android SDK environment setup script not found."
    echo "Setting environment variables manually."
    export ANDROID_SDK_ROOT="$ANDROID_SDK_ROOT"
    export ANDROID_HOME="$ANDROID_HOME"
    export PATH="$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$PATH"
fi

# Install Android NDK
echo "=== Installing Android NDK $NDK_VERSION ==="
"$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" "ndk;$NDK_VERSION"

# Verify installation
if [[ -d "$ANDROID_NDK_HOME" ]]; then
    echo "Android NDK $NDK_VERSION installed successfully at $ANDROID_NDK_HOME"
else
    echo "Error: Android NDK installation failed."
    exit 1
fi

# Configure environment variables
echo "=== Configuring environment variables ==="

# Create environment setup script
cat > "$INSTALL_DIR/ndk-env.sh" << EOL
#!/bin/bash
# Android NDK Environment Variables

export ANDROID_NDK_HOME="$ANDROID_NDK_HOME"
export PATH="\$ANDROID_NDK_HOME:\$PATH"
EOL

chmod +x "$INSTALL_DIR/ndk-env.sh"

echo "Environment setup script created at $INSTALL_DIR/ndk-env.sh"
echo "To activate Android NDK in your current shell, run:"
echo "source \"$INSTALL_DIR/ndk-env.sh\""

# Source the environment variables for the current session
source "$INSTALL_DIR/ndk-env.sh"

echo "=== Android NDK Installation Completed ==="
echo "Android NDK $NDK_VERSION is now installed and configured."
echo "NDK location: $ANDROID_NDK_HOME"