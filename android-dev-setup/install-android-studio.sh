#!/bin/bash
# Android Studio Installation Script
# This script installs Android Studio for development

set -e  # Exit on error

# Configuration variables
INSTALL_DIR="${1:-$HOME/android-dev}"
ANDROID_STUDIO_DIR="$INSTALL_DIR/android-studio"
STUDIO_VERSION="2023.1.1.26"

# Create installation directory
mkdir -p "$INSTALL_DIR"
mkdir -p "$ANDROID_STUDIO_DIR"

echo "=== Installing Android Studio ==="
echo "Installation directory: $INSTALL_DIR"
echo "Android Studio version: $STUDIO_VERSION"
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

# Install Android Studio
echo "=== Downloading Android Studio $STUDIO_VERSION ==="

if [[ "$OS_TYPE" == "linux" ]]; then
    wget -O android-studio.tar.gz "https://redirector.gvt1.com/edgedl/android/studio/ide-zips/$STUDIO_VERSION/android-studio-$STUDIO_VERSION-linux.tar.gz"
    
    echo "=== Extracting Android Studio ==="
    tar -xzf android-studio.tar.gz -C "$INSTALL_DIR"
    mv "$INSTALL_DIR/android-studio"/* "$ANDROID_STUDIO_DIR"
    rmdir "$INSTALL_DIR/android-studio"
    rm android-studio.tar.gz
    
    # Create desktop entry
    echo "=== Creating desktop entry ==="
    mkdir -p "$HOME/.local/share/applications"
    cat > "$HOME/.local/share/applications/android-studio.desktop" << EOL
[Desktop Entry]
Version=1.0
Type=Application
Name=Android Studio
Icon=$ANDROID_STUDIO_DIR/bin/studio.png
Exec="$ANDROID_STUDIO_DIR/bin/studio.sh" %f
Comment=Integrated Development Environment for Android
Categories=Development;IDE;
Terminal=false
StartupWMClass=jetbrains-studio
EOL
    
    echo "Desktop entry created at $HOME/.local/share/applications/android-studio.desktop"
    
elif [[ "$OS_TYPE" == "macos" ]]; then
    wget -O android-studio.dmg "https://redirector.gvt1.com/edgedl/android/studio/install/$STUDIO_VERSION/android-studio-$STUDIO_VERSION-mac.dmg"
    echo "Please manually install Android Studio from the downloaded DMG file:"
    echo "$INSTALL_DIR/android-studio.dmg"
    echo "After mounting the DMG, drag Android Studio to your Applications folder."
fi

# Create launcher script
echo "=== Creating launcher script ==="
cat > "$INSTALL_DIR/studio.sh" << EOL
#!/bin/bash
# Android Studio Launcher

if [[ "$OS_TYPE" == "linux" ]]; then
    "$ANDROID_STUDIO_DIR/bin/studio.sh" "\$@"
elif [[ "$OS_TYPE" == "macos" ]]; then
    open -a "Android Studio" "\$@"
fi
EOL

chmod +x "$INSTALL_DIR/studio.sh"

echo "Launcher script created at $INSTALL_DIR/studio.sh"
echo "To launch Android Studio, run:"
echo "$INSTALL_DIR/studio.sh"

echo "=== Android Studio Installation Completed ==="
if [[ "$OS_TYPE" == "linux" ]]; then
    echo "Android Studio is now installed at $ANDROID_STUDIO_DIR"
    echo "You can launch it from the application menu or by running $INSTALL_DIR/studio.sh"
elif [[ "$OS_TYPE" == "macos" ]]; then
    echo "Please complete the installation by mounting the DMG and dragging Android Studio to your Applications folder."
    echo "After installation, you can launch it from the Applications folder or by running $INSTALL_DIR/studio.sh"
fi