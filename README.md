# 100 Harsh Truths

A minimal, offline-first Android application delivering 99 timeless life truths with persistent reading progress, previous/next quote navigation, local favorites, and daily notifications.

![Android](https://img.shields.io/badge/Android-SDK_34-3DDC84?style=flat-square&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Compose-Material_3-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)
![Min SDK](https://img.shields.io/badge/Min_SDK-26-blue?style=flat-square)

---

## Overview

**100 Harsh Truths** is designed for distraction-free daily reading and reflection. Unlike quote apps that force fixed daily jumps or require accounts and cloud backends, **100 Harsh Truths** keeps you in full control of your reading position with full offline privacy.

### Key Features

- **Persistent Reading Position**: Remembers your exact quote (e.g. *Quote 12 of 99*) across app restarts, reboots, and days.
- **Previous & Next Controls**: Easily browse backward and forward through all 99 entries with boundary protection.
- **Offline & Private**: Zero network permissions, accounts, tracking, or cloud dependencies.
- **Local Favorites**: Bookmark impactful truths using Jetpack DataStore preferences.
- **Daily Notifications**: Configurable WorkManager daily reminder at your preferred time (default 9:00 AM).
- **Minimal Material 3 UI**: Restrained slate blue theme, elegant serif quote typography, and system dark mode support.

---

## Screenshots

| Main Screen | Favorites | Settings |
|:---:|:---:|:---:|
| *(Add MainScreen screenshot here)* | *(Add FavoritesScreen screenshot here)* | *(Add SettingsScreen screenshot here)* |

---

## Tech Stack & Architecture

- **Language**: Kotlin 2.0.0
- **UI Framework**: Jetpack Compose with Material 3 design system
- **State Management**: Kotlin Coroutines, `StateFlow`, and `ViewModel`
- **Persistence**: Preferences DataStore & SharedPreferences
- **Background Tasks**: WorkManager `CoroutineWorker` for daily notifications
- **Serialization**: Kotlinx Serialization JSON parser
- **Testing**: Robolectric, JUnit 4, AndroidX Test

---

## Project Structure

```
app/
├── build.gradle.kts                      # App module configuration
└── src/
    ├── main/
    │   ├── AndroidManifest.xml           # Manifest & permissions
    │   ├── java/com/harshtruths/app/
    │   │   ├── data/
    │   │   │   ├── Quote.kt             # Data model
    │   │   │   ├── QuoteRepository.kt   # Quote loading & reading position logic
    │   │   │   ├── FavoritesDataStore.kt# Favorites persistence (DataStore)
    │   │   │   ├── SettingsDataStore.kt # Notification settings (DataStore)
    │   │   │   └── NotificationScheduler.kt # WorkManager notification worker
    │   │   ├── ui/
    │   │   │   ├── Components.kt        # Shared Compose components (QuoteCard, Progress, Controls)
    │   │   │   ├── MainScreen.kt        # Main quote reading interface
    │   │   │   ├── FavoritesScreen.kt   # Saved quotes screen
    │   │   │   ├── SettingsScreen.kt    # Notification & progress settings
    │   │   │   └── theme/               # Color, Theme, and Typography definitions
    │   │   ├── BootReceiver.kt          # System reboot receiver
    │   │   ├── HarshTruthsApplication.kt# App class & notification channel setup
    │   │   └── MainActivity.kt          # Single activity host & navigation
    │   └── res/
    │       ├── raw/quotes.json          # 99 original entries
    │       ├── mipmap-anydpi-v26/       # Adaptive launcher icons
    │       └── values/strings.xml       # App string resources
    └── test/                            # Robolectric & unit test suite
```

---

## Building and Installation

### Prerequisites

- **Android Studio** (Koala / Ladybug or newer)
- **JDK**: Java 17 or Java 21 (configured in Android Studio → Settings → Build Tools → Gradle → Gradle JDK)
- **Android SDK**: API 34 (Build Tools 34.0.0)

### 1. Build Debug APK

To assemble a debug APK from the command line:

```bash
./gradlew assembleDebug
```

The generated APK will be at:
`app/build/outputs/apk/debug/app-debug.apk`

To use the professional release naming convention, rename or copy the output:
```bash
cp app/build/outputs/apk/debug/app-debug.apk 100-Harsh-Truths-v1.0.0-debug.apk
```

### 2. Build Signed Release APK

To generate an unsigned release APK:
```bash
./gradlew assembleRelease
```

Output path: `app/build/outputs/apk/release/app-release-unsigned.apk`

To generate a signed release APK for GitHub / Play Store:
1. Open Android Studio → **Build → Generate Signed Bundle / APK**.
2. Select **APK**, choose your signing keystore, and enter your key credentials.
3. Select **Release** build type.
4. Name the output file: `100-Harsh-Truths-v1.0.0-release.apk`.

---

## Running Automated Tests

Run unit tests via Gradle:

```bash
./gradlew test
```

---

## License

Personal project — no license specified. Add an open-source license (e.g. MIT, Apache 2.0) if publishing publicly.
