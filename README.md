# Task Wheel - Android App

A fun and interactive Android app that helps you decide what to do when you're feeling bored or lost. Simply spin the wheel and let fate decide your next activity!

## Features

- **Beautiful Spinning Wheel**: Smooth animations with 8 customizable segments
- **Fully Customizable**: Edit segment labels and colors through the settings screen
- **Persistent Storage**: Your customizations are saved automatically
- **Material 3 Design**: Modern UI following Google's latest design guidelines
- **Samsung A56 Compatible**: Optimized for latest Android devices
- **Google Play Ready**: Follows all Google Play Store requirements and security guidelines

## Default Activities

The app comes pre-configured with 8 life-enriching activities:

1. 🏢 Work
2. 📚 Reading books
3. 🖨️ 3D printing
4. 🕉️ Vedanta
5. 🏍️ Bike Rides/Travel
6. 💰 Exploring Finance
7. 🎵 Tabla/Hindustani music
8. 💼 Freelancing work

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Architecture**: MVVM with Repository pattern
- **Data Persistence**: DataStore Preferences
- **Navigation**: Jetpack Navigation Compose

## Building the App

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK with API 34

### Build Instructions

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd task-wheel-app
   ```

2. Open the project in Android Studio

3. Sync Gradle files (Android Studio should prompt you automatically)

4. Build the project:
   ```bash
   ./gradlew build
   ```

5. Run on device or emulator:
   ```bash
   ./gradlew installDebug
   ```

### Building Release APK

To build a release APK for distribution:

```bash
./gradlew assembleRelease
```

The APK will be generated at: `app/build/outputs/apk/release/app-release-unsigned.apk`

For Google Play Store submission, you'll need to:
1. Create a keystore for signing
2. Configure signing in `app/build.gradle.kts`
3. Build a signed AAB (Android App Bundle):
   ```bash
   ./gradlew bundleRelease
   ```

## Project Structure

```
app/src/main/
├── java/com/taskwheel/app/
│   ├── MainActivity.kt              # Entry point
│   ├── data/
│   │   ├── WheelSegment.kt         # Data model
│   │   └── WheelRepository.kt      # Data persistence
│   └── ui/
│       ├── WheelViewModel.kt       # Business logic
│       ├── SpinningWheel.kt        # Wheel component
│       ├── MainScreen.kt           # Home screen
│       ├── SettingsScreen.kt       # Settings UI
│       └── theme/                   # Material 3 theming
└── res/
    ├── values/                      # Strings, colors, themes
    ├── drawable/                    # Vector assets
    └── mipmap-*/                    # App icons
```

## Security & Privacy

- ✅ No internet permissions required
- ✅ No data collection or analytics
- ✅ All data stored locally on device
- ✅ ProGuard enabled for release builds
- ✅ No third-party dependencies with security concerns
- ✅ Compliant with Google Play security requirements

## Customization

### Changing Segments

1. Open the app and tap the Settings icon
2. Tap on any segment to edit its label and color
3. Choose from 12 predefined colors
4. Save your changes

### Resetting to Defaults

Tap the refresh icon in the Settings screen to restore default segments.

## License

This project is open source and available under the MIT License.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For issues or feature requests, please create an issue in the repository.

---

Made with ❤️ for making life decisions more fun!
