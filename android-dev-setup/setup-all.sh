#!/bin/bash
# Complete Android Development Environment Setup Script
# This script sets up a complete Android development environment

set -e  # Exit on error

# Configuration variables
INSTALL_DIR="${1:-$HOME/android-dev}"

echo "=== Android Development Environment Setup ==="
echo "Installation directory: $INSTALL_DIR"
echo "Starting setup at $(date)"
echo

# Create installation directory
mkdir -p "$INSTALL_DIR"
cd "$INSTALL_DIR"

# Make all scripts executable
chmod +x /workspace/android-dev-setup/*.sh

# Install JDK
echo "=== Step 1: Installing JDK ==="
/workspace/android-dev-setup/install-jdk.sh "$INSTALL_DIR"
source "$INSTALL_DIR/java-env.sh"
echo

# Install Android SDK
echo "=== Step 2: Installing Android SDK ==="
/workspace/android-dev-setup/install-android-sdk.sh "$INSTALL_DIR"
source "$INSTALL_DIR/android-env.sh"
echo

# Install Android NDK
echo "=== Step 3: Installing Android NDK ==="
/workspace/android-dev-setup/install-android-ndk.sh "$INSTALL_DIR"
source "$INSTALL_DIR/ndk-env.sh"
echo

# Install Gradle
echo "=== Step 4: Installing Gradle ==="
/workspace/android-dev-setup/install-gradle.sh "$INSTALL_DIR"
source "$INSTALL_DIR/gradle-env.sh"
echo

# Install Android Studio
echo "=== Step 5: Installing Android Studio ==="
/workspace/android-dev-setup/install-android-studio.sh "$INSTALL_DIR"
echo

# Create AVD
echo "=== Step 6: Creating Android Virtual Device ==="
/workspace/android-dev-setup/create-avd.sh "$INSTALL_DIR"
echo

# Combine environment scripts
echo "=== Combining environment scripts ==="
cat > "$INSTALL_DIR/env-setup.sh" << EOL
#!/bin/bash
# Android Development Environment Variables

# Java
$(cat "$INSTALL_DIR/java-env.sh" | grep -v "#!/bin/bash")

# Android SDK
$(cat "$INSTALL_DIR/android-env.sh" | grep -v "#!/bin/bash")

# Android NDK
$(cat "$INSTALL_DIR/ndk-env.sh" | grep -v "#!/bin/bash")

# Gradle
$(cat "$INSTALL_DIR/gradle-env.sh" | grep -v "#!/bin/bash")

# Add to PATH (avoid duplicates)
export PATH="\$JAVA_HOME/bin:\$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:\$ANDROID_SDK_ROOT/platform-tools:\$ANDROID_SDK_ROOT/emulator:\$ANDROID_NDK_HOME:\$GRADLE_HOME/bin:\$PATH"
EOL

chmod +x "$INSTALL_DIR/env-setup.sh"

# Add to shell profile
if [[ -f "$HOME/.bashrc" ]]; then
    echo "source \"$INSTALL_DIR/env-setup.sh\"" >> "$HOME/.bashrc"
    echo "Environment variables added to .bashrc"
fi

if [[ -f "$HOME/.zshrc" ]]; then
    echo "source \"$INSTALL_DIR/env-setup.sh\"" >> "$HOME/.zshrc"
    echo "Environment variables added to .zshrc"
fi

if [[ -f "$HOME/.bash_profile" ]]; then
    echo "source \"$INSTALL_DIR/env-setup.sh\"" >> "$HOME/.bash_profile"
    echo "Environment variables added to .bash_profile"
fi

# Source the environment variables for the current session
source "$INSTALL_DIR/env-setup.sh"

# Validate installation
echo "=== Validating installation ==="

# Check Java
if "$JAVA_HOME/bin/java" -version; then
    echo "✓ Java is installed and working."
else
    echo "✗ Java installation failed."
fi

# Check Android SDK
if [[ -f "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" ]]; then
    echo "✓ Android SDK is installed."
else
    echo "✗ Android SDK installation failed."
fi

# Check Android NDK
if [[ -d "$ANDROID_NDK_HOME" ]]; then
    echo "✓ Android NDK is installed."
else
    echo "✗ Android NDK installation failed."
fi

# Check Gradle
if "$GRADLE_HOME/bin/gradle" --version; then
    echo "✓ Gradle is installed and working."
else
    echo "✗ Gradle installation failed."
fi

# Check Android Studio
if [[ -d "$ANDROID_STUDIO_DIR" ]]; then
    echo "✓ Android Studio is installed."
else
    echo "✗ Android Studio installation failed."
fi

# Check environment variables
if [[ -n "$ANDROID_SDK_ROOT" && -n "$JAVA_HOME" && -n "$GRADLE_HOME" ]]; then
    echo "✓ Environment variables are set."
else
    echo "✗ Environment variables are not set correctly."
fi

# Generate summary report
echo "=== Android Development Environment Summary ==="
echo "Installation directory: $INSTALL_DIR"
echo "JDK: $JAVA_HOME"
echo "Android SDK: $ANDROID_SDK_ROOT"
echo "Android NDK: $ANDROID_NDK_HOME"
echo "Gradle: $GRADLE_HOME"
echo "Android Studio: $ANDROID_STUDIO_DIR"
echo
echo "Environment setup script: $INSTALL_DIR/env-setup.sh"
echo
echo "To activate the environment in a new terminal, run:"
echo "source \"$INSTALL_DIR/env-setup.sh\""
echo
echo "To launch Android Studio, run:"
echo "$INSTALL_DIR/studio.sh"
echo
echo "To launch the Android emulator, run:"
echo "$INSTALL_DIR/launch-emulator.sh"
echo
echo "Setup completed at $(date)"

echo "=== Android Development Environment Setup Completed ==="
echo "Please restart your terminal or run 'source \"$INSTALL_DIR/env-setup.sh\"' to apply the environment variables."