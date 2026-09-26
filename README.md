# AniDiary

AniDiary is an offline-first anime diary for keeping track of your anime journey, watch status, seasons, movies, ratings, tags, and related personal library data.

## Features

- Track anime and watch status
- Organize series, seasons, parts, movies, and OVAs
- Search, filter, sort, and use custom tags
- Track ongoing/release status and ratings
- Continue watching and journey views
- Export and import app data for backups
- Clear all app data from Settings
- Offline-first single-page app
- Android app powered by Capacitor

## Project Structure

- `www/` — Web application source
- `android/` — Capacitor Android project
- `.github/workflows/` — GitHub Actions Android build workflow
- `resources/` — App resources such as the application icon
- `capacitor.config.json` — Capacitor configuration
- `package.json` / `package-lock.json` — Node.js and Capacitor project metadata

## Development

Install dependencies:

```bash
npm ci
```

Sync the web app with the Android project:

```bash
npx cap sync android
```

The Android project can then be opened in Android Studio or built with the included Gradle wrapper.

## Automated Android Build

The repository includes a GitHub Actions workflow for building the Android release APK. Release signing credentials are supplied through GitHub Actions secrets and are not stored in the repository.

## Data & Privacy

AniDiary is designed to keep the user's anime diary data locally on the device. The app provides export/import functionality so users can create and restore their own backups.

## Credits

Developed by Minecade.

## License

AniDiary is licensed under the MIT License. See [LICENSE](LICENSE) for the full license text.
