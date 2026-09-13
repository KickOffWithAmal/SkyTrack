# SkyTrack — Phase 4 Attendance

Phase 4 adds the Android attendance dashboard on top of the working Phase 2 Apps Script backend and Phase 3 login.

## What is included

- Today's attendance lookup
- Start Work
- End Work
- Login/logout/hours/status display
- Loading states
- Error dialogs
- Duplicate-action prevention in the UI
- Session-expiry handling
- Backend refresh after Start Work / End Work

## Backend actions expected

The Android app sends these POST JSON bodies to the same Apps Script `/exec` URL:

### Attendance today

```json
{
  "action": "attendance_today",
  "token": "SESSION_TOKEN"
}
```

### Start work

```json
{
  "action": "start_work",
  "token": "SESSION_TOKEN"
}
```

### End work

```json
{
  "action": "end_work",
  "token": "SESSION_TOKEN"
}
```

The expected successful response contains:

```json
{
  "success": true,
  "message": "...",
  "attendance": {
    "date": "...",
    "employeeId": "...",
    "name": "...",
    "login": "...",
    "logout": "...",
    "hours": "...",
    "status": "..."
  }
}
```

## Setup

1. Open this project in Android Studio.
2. In `app/build.gradle.kts`, set `SKYTRACK_API_URL` to the stable Apps Script Web App URL ending in `/exec`.
3. Sync Gradle.
4. Run the app.
5. Sign in with the same working employee account used in Phase 3.
6. Confirm today's attendance loads.
7. Tap **Start Work** and confirm a Login time appears.
8. Tap **End Work** and confirm Logout and Hours appear.
9. Check the `Attendance` sheet to verify the backend wrote the row.

## Important

Google Sheets remains the source of truth. The Android app does not directly access the spreadsheet.

Do not use a `script.googleusercontent.com/macros/echo?...` URL as the Android endpoint. Use the stable Apps Script deployment URL ending in `/exec`.

If the backend returns an error code not listed in the ViewModel, the server's message is shown to the user.
