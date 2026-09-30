# SkyTrack Phase 6 — Monthly Attendance Statistics

This phase adds monthly attendance statistics to the existing Phase 5 Android app.

## 1. Backend

Open your existing Google Apps Script `Code.gs`.

Add this to the existing `doPost()` switch:

```javascript
case 'attendance_monthly':
  return handleAttendanceMonthly(body);
```

Then copy the functions from:

`PHASE_6_BACKEND_MONTHLY_STATS_PATCH.gs`

Do not create duplicate helper functions if your current `Code.gs` already has the same helper names. In that case, keep one compatible version.

### Working days

SkyTrack uses:

- Monday = working day
- Tuesday = working day
- Wednesday = working day
- Thursday = working day
- Friday = working day
- Saturday = working day
- Sunday = weekly off

Company holidays are not deducted in this MVP.

## 2. Redeploy Apps Script

Apps Script:
Deploy → Manage deployments → Edit → New version → Deploy.

Keep using the same `/exec` Web App URL.

## 3. Backend test

Use:

```json
{
  "action": "attendance_monthly",
  "token": "YOUR_SESSION_TOKEN",
  "month": "2026-09"
}
```

## 4. Android

The project already contains:

- MonthlyAttendance models
- MonthlyStatisticsRepository
- MonthlyStatisticsViewModel
- MonthlyStatisticsScreen
- Home → Monthly Statistics navigation

The repository uses the existing `BuildConfig.SKYTRACK_API_URL`, so it does not need a second endpoint.

## 5. Run

Login → Home → Monthly Statistics.

Use Previous/Next to change the month.

