#!/bin/bash
# Android Virtual Device (AVD) Creation Script
# This script creates AVDs for testing Android applications

set -e  # Exit on error

# Configuration variables
INSTALL_DIR="${1:-$HOME/android-dev}"
ANDROID_SDK_ROOT="$INSTALL_DIR/android-sdk"
ANDROID_HOME="$ANDROID_SDK_ROOT"
AVD_NAME="${2:-test_avd}"
API_LEVEL="${3:-34}"
DEVICE="${4:-pixel_5}"
SYSTEM_IMAGE="system-images;android-$API_LEVEL;google_apis_playstore;x86_64"
FALLBACK_SYSTEM_IMAGE="system-images;android-$API_LEVEL;google_apis;x86_64"

# Check if Android SDK is installed
if [[ ! -d "$ANDROID_SDK_ROOT/cmdline-tools/latest" ]]; then
    echo "Error: Android SDK not found at $ANDROID_SDK_ROOT"
    echo "Please install Android SDK first using install-android-sdk.sh"
    exit 1
fi

echo "=== Creating Android Virtual Device (AVD) ==="
echo "SDK directory: $ANDROID_SDK_ROOT"
echo "AVD name: $AVD_NAME"
echo "API level: $API_LEVEL"
echo "Device: $DEVICE"
echo "System image: $SYSTEM_IMAGE"
echo "Starting at $(date)"
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

# Check if system image is installed
echo "=== Checking system image ==="
if ! "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" --list | grep -q "$SYSTEM_IMAGE"; then
    echo "System image $SYSTEM_IMAGE not found. Installing..."
    "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" "$SYSTEM_IMAGE"
    
    if [ $? -ne 0 ]; then
        echo "Failed to install $SYSTEM_IMAGE. Trying fallback image..."
        "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" "$FALLBACK_SYSTEM_IMAGE"
        
        if [ $? -eq 0 ]; then
            SYSTEM_IMAGE="$FALLBACK_SYSTEM_IMAGE"
            echo "Fallback system image installed successfully."
        else
            echo "Error: Failed to install system images."
            exit 1
        fi
    fi
fi

# Check if emulator is installed
echo "=== Checking emulator ==="
if ! "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" --list | grep -q "emulator"; then
    echo "Emulator not found. Installing..."
    "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" "emulator"
    
    if [ $? -ne 0 ]; then
        echo "Error: Failed to install emulator."
        exit 1
    fi
fi

# Create AVD
echo "=== Creating AVD $AVD_NAME ==="
echo "no" | "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/avdmanager" create avd \
    -n "$AVD_NAME" \
    -k "$SYSTEM_IMAGE" \
    -d "$DEVICE"

if [ $? -ne 0 ]; then
    echo "Error: Failed to create AVD."
    exit 1
fi

# Configure AVD for better performance
echo "=== Configuring AVD for better performance ==="
AVD_DIR="$HOME/.android/avd/${AVD_NAME}.avd"
CONFIG_FILE="$AVD_DIR/config.ini"

if [[ -f "$CONFIG_FILE" ]]; then
    # Backup original config
    cp "$CONFIG_FILE" "${CONFIG_FILE}.backup"
    
    # Update config for better performance
    sed -i 's/hw.lcd.density=.*/hw.lcd.density=420/' "$CONFIG_FILE"
    sed -i 's/hw.ramSize=.*/hw.ramSize=2048/' "$CONFIG_FILE"
    
    # Add performance settings
    echo "hw.gpu.enabled=yes" >> "$CONFIG_FILE"
    echo "hw.gpu.mode=auto" >> "$CONFIG_FILE"
    echo "hw.keyboard=yes" >> "$CONFIG_FILE"
    
    echo "AVD configuration updated for better performance."
else
    echo "Warning: Could not find AVD config file at $CONFIG_FILE"
fi

# Create launcher script
echo "=== Creating emulator launcher script ==="
cat > "$INSTALL_DIR/launch-emulator.sh" << EOL
#!/bin/bash
# Android Emulator Launcher

export ANDROID_SDK_ROOT="$ANDROID_SDK_ROOT"
export ANDROID_HOME="$ANDROID_HOME"
export PATH="\$ANDROID_SDK_ROOT/emulator:\$ANDROID_SDK_ROOT/platform-tools:\$PATH"

# Launch emulator
echo "Launching emulator with AVD: $AVD_NAME"
"\$ANDROID_SDK_ROOT/emulator/emulator" -avd "$AVD_NAME" -gpu auto "\$@"
EOL

chmod +x "$INSTALL_DIR/launch-emulator.sh"

echo "Launcher script created at $INSTALL_DIR/launch-emulator.sh"
echo "To launch the emulator, run:"
echo "$INSTALL_DIR/launch-emulator.sh"

echo "=== AVD Creation Completed ==="
echo "AVD '$AVD_NAME' is now ready for use."
echo "You can launch it using the emulator command or the launcher script."