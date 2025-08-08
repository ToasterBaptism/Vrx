#!/bin/bash
# JDK Installation Script
# This script installs JDK 17 (or JDK 11 as fallback) for Android development

set -e  # Exit on error

# Configuration variables
INSTALL_DIR="${1:-$HOME/android-dev}"
JAVA_HOME="$INSTALL_DIR/jdk"
JDK_VERSION="17"
JDK_FALLBACK_VERSION="11"

# Create installation directory
mkdir -p "$INSTALL_DIR"
mkdir -p "$JAVA_HOME"

echo "=== Installing JDK for Android Development ==="
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

# Install JDK
echo "=== Installing JDK $JDK_VERSION ==="

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

# Configure environment variables
echo "=== Configuring environment variables ==="

# Create environment setup script
cat > "$INSTALL_DIR/java-env.sh" << EOL
#!/bin/bash
# Java Environment Variables

export JAVA_HOME="$JAVA_HOME"
export PATH="\$JAVA_HOME/bin:\$PATH"
EOL

chmod +x "$INSTALL_DIR/java-env.sh"

echo "Environment setup script created at $INSTALL_DIR/java-env.sh"
echo "To activate Java in your current shell, run:"
echo "source \"$INSTALL_DIR/java-env.sh\""

# Source the environment variables for the current session
source "$INSTALL_DIR/java-env.sh"

echo "=== JDK Installation Completed ==="
echo "JDK $JDK_VERSION is now installed and configured."