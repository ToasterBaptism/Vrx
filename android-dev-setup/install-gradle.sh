#!/bin/bash
# Gradle Installation Script
# This script installs Gradle for Android development

set -e  # Exit on error

# Configuration variables
INSTALL_DIR="${1:-$HOME/android-dev}"
GRADLE_HOME="$INSTALL_DIR/gradle"
GRADLE_VERSION="8.4"

# Create installation directory
mkdir -p "$INSTALL_DIR"
mkdir -p "$GRADLE_HOME"

echo "=== Installing Gradle for Android Development ==="
echo "Installation directory: $INSTALL_DIR"
echo "Gradle version: $GRADLE_VERSION"
echo "Starting installation at $(date)"
echo

# Install Gradle
echo "=== Downloading Gradle $GRADLE_VERSION ==="
wget -O gradle.zip "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"

echo "=== Extracting Gradle ==="
unzip -q gradle.zip -d "$INSTALL_DIR"
mv "$INSTALL_DIR/gradle-$GRADLE_VERSION"/* "$GRADLE_HOME"
rmdir "$INSTALL_DIR/gradle-$GRADLE_VERSION"
rm gradle.zip

# Configure environment variables
echo "=== Configuring environment variables ==="

# Create environment setup script
cat > "$INSTALL_DIR/gradle-env.sh" << EOL
#!/bin/bash
# Gradle Environment Variables

export GRADLE_HOME="$GRADLE_HOME"
export PATH="\$GRADLE_HOME/bin:\$PATH"
EOL

chmod +x "$INSTALL_DIR/gradle-env.sh"

echo "Environment setup script created at $INSTALL_DIR/gradle-env.sh"
echo "To activate Gradle in your current shell, run:"
echo "source \"$INSTALL_DIR/gradle-env.sh\""

# Source the environment variables for the current session
source "$INSTALL_DIR/gradle-env.sh"

# Verify installation
echo "=== Verifying Gradle installation ==="
if gradle --version; then
    echo "Gradle $GRADLE_VERSION installed successfully at $GRADLE_HOME"
else
    echo "Error: Gradle installation verification failed."
    exit 1
fi

echo "=== Gradle Installation Completed ==="
echo "Gradle $GRADLE_VERSION is now installed and configured."