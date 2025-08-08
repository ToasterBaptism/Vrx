# Android Development Environment Setup

This repository contains scripts to set up a complete Android development environment, including JDK, Android SDK, NDK, Gradle, and Android Studio. The setup is designed to be production-ready, reproducible, and support a wide range of Android app requirements.

## Features

- **Complete Setup**: Installs and configures all components needed for Android development
- **Production-Ready**: Includes all necessary tools for both local development and CI/CD pipelines
- **Reproducible**: Consistent setup across different machines
- **Flexible**: Individual scripts for each component, or a single script for complete setup
- **Validated**: Includes validation steps to ensure everything is working correctly

## Components

The setup includes the following components:

- **JDK**: Java Development Kit 17 (or 11 as fallback)
- **Android SDK**: Command line tools, platform tools, build tools, platforms, and system images
- **Android NDK**: Native Development Kit for native code
- **Gradle**: Build system for Android projects
- **Android Studio**: IDE for Android development
- **AVD**: Android Virtual Device for testing

## Requirements

- Linux or macOS operating system
- Internet connection
- Basic command-line knowledge

## Installation

### Option 1: Complete Setup

To set up the complete Android development environment, run:

```bash
chmod +x /workspace/android-dev-setup/setup-all.sh
/workspace/android-dev-setup/setup-all.sh [INSTALL_DIR]
```

Where `[INSTALL_DIR]` is the directory where you want to install the Android development environment. If not specified, it defaults to `$HOME/android-dev`.

### Option 2: Individual Components

You can also install individual components using the following scripts:

1. **JDK**:
   ```bash
   chmod +x /workspace/android-dev-setup/install-jdk.sh
   /workspace/android-dev-setup/install-jdk.sh [INSTALL_DIR]
   ```

2. **Android SDK**:
   ```bash
   chmod +x /workspace/android-dev-setup/install-android-sdk.sh
   /workspace/android-dev-setup/install-android-sdk.sh [INSTALL_DIR]
   ```

3. **Android NDK**:
   ```bash
   chmod +x /workspace/android-dev-setup/install-android-ndk.sh
   /workspace/android-dev-setup/install-android-ndk.sh [INSTALL_DIR]
   ```

4. **Gradle**:
   ```bash
   chmod +x /workspace/android-dev-setup/install-gradle.sh
   /workspace/android-dev-setup/install-gradle.sh [INSTALL_DIR]
   ```

5. **Android Studio**:
   ```bash
   chmod +x /workspace/android-dev-setup/install-android-studio.sh
   /workspace/android-dev-setup/install-android-studio.sh [INSTALL_DIR]
   ```

6. **AVD**:
   ```bash
   chmod +x /workspace/android-dev-setup/create-avd.sh
   /workspace/android-dev-setup/create-avd.sh [INSTALL_DIR] [AVD_NAME] [API_LEVEL] [DEVICE]
   ```

## Post-Installation

After installation, you need to activate the environment variables in your current shell:

```bash
source "$INSTALL_DIR/env-setup.sh"
```

Where `$INSTALL_DIR` is the directory where you installed the Android development environment.

## Launching Android Studio

To launch Android Studio, run:

```bash
"$INSTALL_DIR/studio.sh"
```

## Launching the Android Emulator

To launch the Android emulator, run:

```bash
"$INSTALL_DIR/launch-emulator.sh"
```

## Validation

The setup includes validation steps to ensure everything is installed correctly. If you want to validate the installation manually, you can check the following:

- **JDK**: `"$JAVA_HOME/bin/java" -version`
- **Android SDK**: `"$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" --list`
- **Android NDK**: Check if `$ANDROID_NDK_HOME` directory exists
- **Gradle**: `"$GRADLE_HOME/bin/gradle" --version`
- **Android Studio**: Check if `$ANDROID_STUDIO_DIR` directory exists
- **AVD**: `"$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/avdmanager" list avd`

## Environment Variables

The setup sets the following environment variables:

- `JAVA_HOME`: Path to JDK
- `ANDROID_SDK_ROOT`: Path to Android SDK
- `ANDROID_HOME`: Path to Android SDK (same as `ANDROID_SDK_ROOT`)
- `ANDROID_NDK_HOME`: Path to Android NDK
- `GRADLE_HOME`: Path to Gradle

These variables are added to your shell profile (`.bashrc`, `.zshrc`, or `.bash_profile`) and can be activated in the current shell by running `source "$INSTALL_DIR/env-setup.sh"`.

## CI/CD Integration

For CI/CD pipelines, you can use the individual scripts to install only the components you need. For example, if you don't need Android Studio, you can skip the `install-android-studio.sh` script.

You can also use the environment variables in your CI/CD pipeline to configure the build process. For example:

```yaml
# Example GitHub Actions workflow
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up Android development environment
        run: |
          chmod +x /workspace/android-dev-setup/setup-all.sh
          /workspace/android-dev-setup/setup-all.sh $HOME/android-dev
          source $HOME/android-dev/env-setup.sh
      - name: Build Android app
        run: |
          cd your-android-project
          ./gradlew assembleDebug
```

## Troubleshooting

If you encounter any issues during installation, check the following:

- Make sure you have an internet connection
- Check if you have sufficient disk space
- Verify that you have the necessary permissions to install software
- Check if the required dependencies are installed

If you still have issues, you can try installing the components individually to identify the problematic component.

## License

This project is licensed under the MIT License - see the LICENSE file for details.