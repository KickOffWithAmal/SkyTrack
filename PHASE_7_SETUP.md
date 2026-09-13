# SkyTrack Phase 7 — Stitch UI implementation

This project is based on the working Phase 6 Android project and implements the supplied Google Stitch visual design in Jetpack Compose.

## Included
- Red/white SkyTrack visual system based on the Stitch design.
- Animated custom splash screen.
- Placeholder logo at `app/src/main/res/drawable/skytrack_logo.png`.
- Swipe-right login interaction plus fallback button.
- Redesigned Home dashboard.
- Redesigned Leave Management screen.
- Redesigned Monthly Statistics screen.
- Floating rounded bottom navigation with animated selected-item magnification.
- Profile dialog and sign-out confirmation.
- Existing Phase 1–6 API/repository/ViewModel functionality retained.
- Monday–Saturday working-day logic remains in the Phase 6 backend.

## Replace the placeholder logo
Replace:
`app/src/main/res/drawable/skytrack_logo.png`
with your company PNG using the same filename, or update the resource reference if you prefer another name.

## Backend
No new backend endpoint is required for the visual redesign. Keep the Phase 6 `attendance_monthly` backend deployed.

## API URL
The existing Apps Script `/exec` URL remains in `app/build.gradle.kts`.

## Build
Open the `SkyTrack` folder in Android Studio and sync Gradle. If Android Studio asks to update Gradle/plugin versions, do not change them unless necessary; this project retains the versions from the supplied Phase 6 project.
