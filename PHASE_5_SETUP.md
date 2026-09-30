# SkyTrack — Phase 5 Leave Management

Phase 5 adds employee leave requests and leave history.

## 1. Google Sheet

Create/verify a tab named:

`Leaves`

The first row must contain exactly:

`Request ID | Employee ID | Name | From | To | Type | Reason | Status`

No Google credentials are placed in Android.

## 2. Update Apps Script

Open the same Apps Script project used by Phase 2.

Open `PHASE_5_BACKEND_LEAVE_PATCH.gs` from this package.

In your existing `doPost()` switch, add:

```javascript
case 'leave_submit':
  return handleLeaveSubmit(body);

case 'leave_history':
  return handleLeaveHistory(body);
```

Then add the functions from the patch file to your existing `Code.gs`.

Do NOT remove your working login or attendance functions.

The leave backend reuses the existing `authenticateToken(token)` helper from Phase 1/2, so the employee is determined from the authenticated session rather than from a client-supplied employee ID.

## 3. Deploy the backend update

After saving the Apps Script changes:

Deploy → Manage deployments → Edit the Web app deployment → New version → Deploy.

Keep the same `/exec` URL if possible.

## 4. Android

The Android project is already configured for the leave endpoints.

Open the project and make sure `SKYTRACK_API_URL` still contains your working:

`https://script.google.com/macros/s/.../exec`

Then Sync Gradle and run.

## 5. Test

Sign in.

From the dashboard:

**Leave Management**

Submit a request with:

- From: `2026-09-15`
- To: `2026-09-16`
- Type: Casual
- Reason: Personal work

Expected result:

- Request appears in Leave History.
- Status is `Pending`.
- A Request ID is generated.
- The request is written to the `Leaves` sheet.

Try submitting another request with overlapping dates. The backend should reject it with `OVERLAPPING_LEAVE`.

## Security behavior

- Android sends the session token.
- Backend authenticates the token.
- Backend obtains the employee from the session.
- Android does not send or control Employee ID for the leave record.
- Dates are validated on both Android and backend.
- Leave type is restricted to Casual, Sick, Earned, Other.
- Reason is limited to 3–500 characters.
- Pending/Approved overlapping leave requests are rejected.
