<div align="center">

# ⛩️ AniDiary

**Anime Journey Tracker**

[![Built with HTML](https://img.shields.io/badge/Frontend-HTML%2FCSS%2FJavaScript-E34F26?logo=html5&logoColor=white)](https://developer.mozilla.org/en-US/docs/Web/HTML)
[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Capacitor](https://img.shields.io/badge/Build-Capacitor-119EFF?logo=capacitor&logoColor=white)](https://capacitorjs.com)
[![GitHub Actions](https://img.shields.io/badge/Build-GitHub_Actions-2088FF?logo=githubactions&logoColor=white)](https://github.com/features/actions)
[![Version](https://img.shields.io/badge/Version-1.4.2-blue)](https://github.com/MinecadeXD/AniDiary/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

### [📦 Download Latest Release](https://github.com/MinecadeXD/AniDiary/releases/latest)

*An anime journey tracker for organizing your anime library, tracking watch progress, recording journey events, and managing personal anime data.*

</div>

---

## 🌐 Live Demo

Try AniDiary directly in your browser:

👉 **[Open AniDiary Live Demo](https://minecadexd.github.io/AniDiary/)**

No installation required.

## Features

### 📊 Dashboard & Library
- Dashboard overview with **Total Anime, Unwatched, Watching, and Completed** counts.
- Continue Watching section for quickly returning to active anime.
- Anime library with search, filtering, sorting, and custom tags.
- Movie and Series filtering.
- Dashboard visibility controls for individual anime entries.

### 🎬 Anime Tracking
- Track anime as **Unwatched, Watching, or Completed**.
- Organize series into seasons and parts.
- Add movies separately from series seasons.
- Add OVAs between seasons and anime details.
- Record ratings and ongoing release status.
- Store anime posters and personal tracking information.
- Track the latest update date for library entries.

### 🗺️ Journey Tracking
- Record personal anime journey events.
- Supported events include:
  - **Started New**
  - **Started Again**
  - **Completed Available Seasons**
  - **Completed Full Anime**
  - **Watched**
- Record the month and year for journey events.
- View journey history in a chronological timeline.

### 💾 Data & Backup
- Store anime diary data locally on the device.
- Export AniDiary data as a JSON backup file.
- Import previously saved JSON backups.
- Restore anime entries from backup files.
- Clear all app data with a confirmation step.

### 📱 App Experience
- Dedicated Settings page for app and data management.
- Android back-button navigation between views, details, settings, and dialogs.
- Responsive mobile and landscape layouts.
- Fixed visual background across long-scrolling views.
- Lightweight single-page architecture.
- No account is required for normal anime tracking.
- UI dependencies are loaded from CDN services, so internet access may be required when the app first loads or when those resources are not cached.

## Requirements

For using AniDiary:

- **Android 7.0 (API 24) or newer.**
- Internet access may be required for CDN-hosted UI dependencies and externally hosted anime poster images.

For development and Android builds:

- Node.js 22
- Java 21 (Temurin recommended)
- npm
- Git
- Android SDK compatible with the project's configured Android/Capacitor versions

## Installation

### Android release

1. Open the [latest AniDiary release](https://github.com/MinecadeXD/AniDiary/releases/latest).
2. Download the release APK.
3. Install the APK on your Android device.
4. If Android asks for permission to install apps from the source you used, allow it and continue the installation.

AniDiary does not require an account for normal use. Your anime diary data is stored locally on the device.

## Current Version

**1.4.2**

## Technology

- HTML, CSS and JavaScript
- Capacitor for Android packaging
- Tailwind CSS via CDN
- Lucide Icons via CDN
- GitHub Actions for release APK builds

The main application is intentionally kept in a single `www/index.html` file to keep the project straightforward and easy to maintain.

## Project Structure

```text
AniDiary/
├── .github/
│   └── workflows/
│       └── android.yml
├── android/
│   ├── app/
│   ├── gradle/
│   ├── gradlew
│   ├── gradlew.bat
│   └── ...
├── resources/
│   └── icon.png
├── www/
│   └── index.html
├── .gitignore
├── capacitor.config.json
├── LICENSE
├── package.json
├── package-lock.json
└── README.md
```

## Android Build

AniDiary's Android release APK is built automatically with GitHub Actions using `.github/workflows/android.yml`.

### Build process

The workflow runs on pushes to the `main` branch or can be started manually and:

1. Checks out the repository.
2. Sets up Node.js 22.
3. Sets up Java 21.
4. Installs the project dependencies with `npm ci`.
5. Installs Capacitor Assets temporarily.
6. Generates Android launcher icons from the repository resources.
7. Synchronizes the Capacitor Android project.
8. Applies the AniDiary Android version configuration.
9. Decodes the release keystore from a GitHub Actions secret.
10. Configures release signing using GitHub Actions secrets.
11. Builds the signed Android release APK with Gradle.
12. Copies the APK to `AniDiary-1.4.2-release.apk`.
13. Removes the temporary signing keystore.
14. Uploads the release APK as the `AniDiary-1.4.2-release` GitHub Actions artifact.

### Required GitHub Actions secrets

The workflow expects these repository secrets:

- `KEYSTORE_BASE64`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

The signing keystore and passwords are not stored in the repository. The keystore is created temporarily during the workflow and removed after the build.

The generated APK can be downloaded from the workflow's **Artifacts** section after a successful build.

See [`.github/workflows/android.yml`](.github/workflows/android.yml) for the exact implementation.

## Development

Install the project dependencies:

```bash
npm ci
```

Synchronize the Android project:

```bash
npx cap sync android
```

Most application changes can be made directly in:

```text
www/index.html
```

The Android project can be opened in Android Studio or built using the included Gradle wrapper.

## Data & Privacy

AniDiary stores anime diary data locally on the device for normal use. Export and import functionality allows users to create and restore their own JSON backups.

Anime poster images may use externally hosted image URLs, and the application loads its UI dependencies from CDN services.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for the full license text.

---

<div align="center">

### Made with ♥️ by [Minecade](https://github.com/MinecadeXD)

</div>
