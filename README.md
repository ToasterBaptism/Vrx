# VRX Theater

VRX Theater is an Android application that allows users to play their installed Android games in a virtual theater environment using mobile VR phone headsets like Google Cardboard, Daydream, or similar devices.

## Features

- **Virtual Theater Environment**: Play your games on a large virtual screen in a 3D theater environment
- **Game Detection**: Automatically detects installed games on your device
- **Head Tracking**: Uses your phone's sensors for immersive head tracking
- **Controller Support**: Compatible with Bluetooth and USB game controllers
- **Customizable Experience**: Adjust screen size, distance, curvature, and environment settings
- **Lens Calibration**: Fine-tune the VR experience for your specific headset
- **Performance Modes**: Choose between quality, balanced, or battery-saving modes

## Requirements

- Android 7.0 (API level 24) or higher
- Device with gyroscope and accelerometer sensors
- VR headset (Google Cardboard, Daydream, or similar)
- Optional: Bluetooth or USB game controller

## Installation

1. Download the latest APK from the [Releases](https://github.com/ToasterBaptism/Vrx/releases) page
2. Enable installation from unknown sources in your device settings
3. Install the APK
4. Grant the required permissions when prompted

## Usage

1. Launch VRX Theater
2. Browse and select a game from your installed games
3. Insert your phone into your VR headset
4. Use head movements to look around the virtual theater
5. Use the controller to interact with the game

## Calibration

For the best experience, calibrate the app for your specific VR headset:

1. Go to the Calibration section
2. Adjust the IPD (interpupillary distance) to match your eyes
3. Fine-tune lens distortion parameters
4. Adjust screen size and distance for comfort
5. Test the settings in VR mode

## Permissions

VRX Theater requires the following permissions:

- **QUERY_ALL_PACKAGES**: To detect installed games
- **SYSTEM_ALERT_WINDOW**: To display VR content over other apps
- **BLUETOOTH**: To connect to wireless controllers
- **READ_EXTERNAL_STORAGE**: To access game assets
- **INTERNET**: For diagnostic reporting (optional)

## Building from Source

1. Clone the repository:
   ```
   git clone https://github.com/ToasterBaptism/Vrx.git
   ```

2. Open the project in Android Studio

3. Build the project:
   ```
   ./gradlew assembleDebug
   ```

4. Install on your device:
   ```
   ./gradlew installDebug
   ```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [Rajawali](https://github.com/Rajawali/Rajawali) - 3D engine for Android
- [Google VR SDK](https://developers.google.com/vr/develop/android/get-started) - VR utilities
- [Dagger Hilt](https://dagger.dev/hilt/) - Dependency injection
- [USB Serial for Android](https://github.com/mik3y/usb-serial-for-android) - USB controller support