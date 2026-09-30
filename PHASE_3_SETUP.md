# SkyTrack Phase 3 — Android Login

## 1. Configure the Apps Script URL

Open:

`app/build.gradle.kts`

Find `SKYTRACK_API_URL` and replace the placeholder with the stable Apps Script Web App URL ending in `/exec`.

Use the `script.google.com/macros/s/.../exec` Web App URL from Apps Script Deploy > Manage deployments. Do not use a temporary `script.googleusercontent.com/macros/echo?...` redirect URL.

## 2. Sync and run

Open the project in Android Studio, allow Gradle sync, then run the `app` configuration.

## 3. Test

Use:
- Employee ID: your employee ID, e.g. `SK001`
- Password: the password already validated against Phase 2

A successful login stores the returned session token in Android DataStore and shows the temporary Phase 3 Home screen.

The token is not hard-coded into the app.

## 4. Phase 3 scope

Implemented:
- Retrofit API
- Gson JSON models
- Hilt dependency injection
- Repository
- Login ViewModel + StateFlow
- Preferences DataStore session persistence
- Compose login UI
- Loading/error states
- Temporary authenticated Home screen
- Sign out
