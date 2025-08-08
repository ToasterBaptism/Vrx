#!/bin/bash
# CI/CD Android Development Environment Setup Script
# This script sets up a minimal Android development environment for CI/CD pipelines

set -e  # Exit on error

# Configuration variables
INSTALL_DIR="${1:-$HOME/android-dev}"
ANDROID_SDK_ROOT="$INSTALL_DIR/android-sdk"
ANDROID_HOME="$ANDROID_SDK_ROOT"
ANDROID_NDK_HOME="$ANDROID_SDK_ROOT/ndk/25.2.9519653"
GRADLE_HOME="$INSTALL_DIR/gradle"
JAVA_HOME="$INSTALL_DIR/jdk"
CMDLINE_TOOLS_VERSION="11.0"
BUILD_TOOLS_VERSION="34.0.0"
PLATFORM_VERSION="34"
PLATFORM_VERSION_FALLBACK="33"
NDK_VERSION="25.2.9519653"
GRADLE_VERSION="8.4"
JDK_VERSION="17"
JDK_FALLBACK_VERSION="11"

# Create installation directory
mkdir -p "$INSTALL_DIR"
cd "$INSTALL_DIR"

echo "=== CI/CD Android Development Environment Setup ==="
echo "Installation directory: $INSTALL_DIR"
echo "Starting setup at $(date)"
echo

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

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

# Install required packages
echo "=== Installing system dependencies ==="
if [[ "$OS_TYPE" == "linux" ]]; then
    if command_exists apt-get; then
        apt-get update
        apt-get install -y wget unzip zip curl git build-essential
    elif command_exists dnf; then
        dnf install -y wget unzip zip curl git gcc gcc-c++ make
    else
        echo "Unsupported Linux distribution. Please install the following packages manually:"
        echo "wget, unzip, zip, curl, git, build tools"
    fi
elif [[ "$OS_TYPE" == "macos" ]]; then
    if ! command_exists brew; then
        echo "Installing Homebrew..."
        /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
    fi
    
    brew install wget unzip zip curl git
fi
echo "System dependencies installed."

# Install JDK
echo "=== Installing JDK $JDK_VERSION ==="
mkdir -p "$JAVA_HOME"

if [[ "$OS_TYPE" == "linux" ]]; then
    # Try to install JDK 17 first, fall back to JDK 11 if needed
    if wget -q --spider "https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.tar.gz"; then
        wget -O jdk.tar.gz "https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.tar.gz"
        JDK_VERSION="17"
    else
        echo "JDK 17 not available, falling back to JDK 11"
        wget -O jdk.tar.gz "https://download.oracle.com/java/11/latest/jdk-11_linux-x64_bin.tar.gz"
        JDK_VERSION="11"
    fi
    
    tar -xzf jdk.tar.gz -C "$JAVA_HOME" --strip-components=1
    rm jdk.tar.gz
elif [[ "$OS_TYPE" == "macos" ]]; then
    if wget -q --spider "https://download.oracle.com/java/17/latest/jdk-17_macos-x64_bin.tar.gz"; then
        wget -O jdk.tar.gz "https://download.oracle.com/java/17/latest/jdk-17_macos-x64_bin.tar.gz"
        JDK_VERSION="17"
    else
        echo "JDK 17 not available, falling back to JDK 11"
        wget -O jdk.tar.gz "https://download.oracle.com/java/11/latest/jdk-11_macos-x64_bin.tar.gz"
        JDK_VERSION="11"
    fi
    
    tar -xzf jdk.tar.gz -C "$JAVA_HOME" --strip-components=1
    rm jdk.tar.gz
fi

echo "JDK $JDK_VERSION installed at $JAVA_HOME"
"$JAVA_HOME/bin/java" -version

# Install Android SDK
echo "=== Installing Android SDK ==="
mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools"

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
export JAVA_HOME="$JAVA_HOME"
export ANDROID_SDK_ROOT="$ANDROID_SDK_ROOT"
export ANDROID_HOME="$ANDROID_HOME"
export PATH="$JAVA_HOME/bin:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$PATH"

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

# Install Android NDK
echo "=== Installing Android NDK $NDK_VERSION ==="
"$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" "ndk;$NDK_VERSION"
echo "Android NDK $NDK_VERSION installed at $ANDROID_NDK_HOME"

# Install Gradle
echo "=== Installing Gradle $GRADLE_VERSION ==="
mkdir -p "$GRADLE_HOME"

wget -O gradle.zip "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
unzip -q gradle.zip -d "$INSTALL_DIR"
mv "$INSTALL_DIR/gradle-$GRADLE_VERSION"/* "$GRADLE_HOME"
rmdir "$INSTALL_DIR/gradle-$GRADLE_VERSION"
rm gradle.zip

echo "Gradle $GRADLE_VERSION installed at $GRADLE_HOME"
"$GRADLE_HOME/bin/gradle" --version

# Create environment setup script
echo "=== Creating environment setup script ==="
cat > "$INSTALL_DIR/env-setup.sh" << EOL
#!/bin/bash
# Android Development Environment Variables for CI/CD

export JAVA_HOME="$JAVA_HOME"
export ANDROID_SDK_ROOT="$ANDROID_SDK_ROOT"
export ANDROID_HOME="$ANDROID_HOME"
export ANDROID_NDK_HOME="$ANDROID_NDK_HOME"
export GRADLE_HOME="$GRADLE_HOME"
export PATH="\$JAVA_HOME/bin:\$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:\$ANDROID_SDK_ROOT/platform-tools:\$ANDROID_NDK_HOME:\$GRADLE_HOME/bin:\$PATH"
EOL

chmod +x "$INSTALL_DIR/env-setup.sh"

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

# Generate summary report
echo "=== CI/CD Android Development Environment Summary ==="
echo "Installation directory: $INSTALL_DIR"
echo "JDK version: $JDK_VERSION"
echo "Android SDK: $ANDROID_SDK_ROOT"
echo "Android NDK: $ANDROID_NDK_HOME"
echo "Gradle: $GRADLE_HOME"
echo
echo "Environment setup script: $INSTALL_DIR/env-setup.sh"
echo
echo "To activate the environment in a new terminal, run:"
echo "source \"$INSTALL_DIR/env-setup.sh\""
echo
echo "Setup completed at $(date)"

echo "=== CI/CD Android Development Environment Setup Completed ==="