/**
 * SkyTrack Phase 5 — Leave Management backend additions
 *
 * Add the two cases below to the existing doPost() switch:
 *
 *   case 'leave_submit':
 *     return handleLeaveSubmit(body);
 *
 *   case 'leave_history':
 *     return handleLeaveHistory(body);
 *
 * These functions reuse the Phase 1/2 authenticateToken(token) helper.
 * Do not replace your existing authentication/attendance code.
 */

// ---------------------------------------------------------------------------
// Add these cases inside doPost() switch(action):
//
// case 'leave_submit':
//   return handleLeaveSubmit(body);
//
// case 'leave_history':
//   return handleLeaveHistory(body);
// ---------------------------------------------------------------------------

function handleLeaveSubmit(body) {
  const token = String(body.token || '').trim();
  const employee = authenticateToken(token);

  const from = String(body.from || '').trim();
  const to = String(body.to || '').trim();
  const type = String(body.type || '').trim();
  const reason = String(body.reason || '').trim();

  if (!isValidIsoDate(from) || !isValidIsoDate(to) || from > to) {
    return jsonResponse({
      success: false,
      error: 'INVALID_DATE',
      message: 'Leave dates must be valid YYYY-MM-DD dates and From cannot be after To.'
    });
  }

  const allowedTypes = ['Casual', 'Sick', 'Earned', 'Other'];
  if (allowedTypes.indexOf(type) === -1) {
    return jsonResponse({
      success: false,
      error: 'INVALID_LEAVE_TYPE',
      message: 'Invalid leave type.'
    });
  }

  if (reason.length < 3 || reason.length > 500) {
    return jsonResponse({
      success: false,
      error: 'INVALID_REASON',
      message: 'Reason must be between 3 and 500 characters.'
    });
  }

  const lock = LockService.getScriptLock();
  lock.waitLock(10000);

  try {
    const sheet = getSpreadsheet().getSheetByName(CONFIG.LEAVES_SHEET);

    if (!sheet) {
      throw new Error('Leaves sheet not found.');
    }

    const values = sheet.getDataRange().getDisplayValues();

    if (values.length === 0) {
      throw new Error('Leaves sheet must contain a header row.');
    }

    const headers = values[0].map(String);
    const index = getColumnIndexes(headers, [
      'Request ID',
      'Employee ID',
      'Name',
      'From',
      'To',
      'Type',
      'Reason',
      'Status'
    ]);

    // Prevent overlapping Pending or Approved requests for this employee.
    for (let row = 1; row < values.length; row++) {
      const employeeId = String(values[row][index['Employee ID']] || '')
        .trim()
        .toUpperCase();

      const status = String(values[row][index['Status']] || '').trim();

      if (employeeId !== employee.employeeId) continue;
      if (status !== 'Pending' && status !== 'Approved') continue;

      const existingFrom = String(values[row][index['From']] || '').trim();
      const existingTo = String(values[row][index['To']] || '').trim();

      if (isValidIsoDate(existingFrom) &&
          isValidIsoDate(existingTo) &&
          datesOverlap(from, to, existingFrom, existingTo)) {
        return jsonResponse({
          success: false,
          error: 'OVERLAPPING_LEAVE',
          message: 'These dates overlap an existing leave request.'
        });
      }
    }

    const requestId =
      'LV-' +
      Utilities.formatDate(new Date(), Session.getScriptTimeZone(), 'yyyyMMddHHmmss') +
      '-' +
      Utilities.getUuid().substring(0, 8).toUpperCase();

    sheet.appendRow([
      requestId,
      employee.employeeId,
      employee.name,
      from,
      to,
      type,
      reason,
      'Pending'
    ]);

    return jsonResponse({
      success: true,
      message: 'Leave request submitted.',
      leave: {
        requestId: requestId,
        employeeId: employee.employeeId,
        name: employee.name,
        from: from,
        to: to,
        type: type,
        reason: reason,
        status: 'Pending'
      }
    });
  } finally {
    lock.releaseLock();
  }
}

function handleLeaveHistory(body) {
  const token = String(body.token || '').trim();
  const employee = authenticateToken(token);

  const sheet = getSpreadsheet().getSheetByName(CONFIG.LEAVES_SHEET);

  if (!sheet) {
    throw new Error('Leaves sheet not found.');
  }

  const values = sheet.getDataRange().getDisplayValues();

  if (values.length < 2) {
    return jsonResponse({
      success: true,
      message: 'No leave requests found.',
      leaves: []
    });
  }

  const headers = values[0].map(String);
  const index = getColumnIndexes(headers, [
    'Request ID',
    'Employee ID',
    'Name',
    'From',
    'To',
    'Type',
    'Reason',
    'Status'
  ]);

  const leaves = [];

  for (let row = 1; row < values.length; row++) {
    const employeeId = String(values[row][index['Employee ID']] || '')
      .trim()
      .toUpperCase();

    if (employeeId !== employee.employeeId) continue;

    leaves.push({
      requestId: String(values[row][index['Request ID']] || '').trim(),
      employeeId: employee.employeeId,
      name: employee.name,
      from: String(values[row][index['From']] || '').trim(),
      to: String(values[row][index['To']] || '').trim(),
      type: String(values[row][index['Type']] || '').trim(),
      reason: String(values[row][index['Reason']] || '').trim(),
      status: String(values[row][index['Status']] || 'Pending').trim()
    });
  }

  leaves.reverse();

  return jsonResponse({
    success: true,
    message: 'Leave history loaded.',
    leaves: leaves
  });
}

function isValidIsoDate(value) {
  if (!/^\d{4}-\d{2}-\d{2}$/.test(value)) return false;

  const parts = value.split('-').map(Number);
  const year = parts[0];
  const month = parts[1];
  const day = parts[2];

  const date = new Date(Date.UTC(year, month - 1, day));

  return date.getUTCFullYear() === year &&
         date.getUTCMonth() === month - 1 &&
         date.getUTCDate() === day;
}

function datesOverlap(fromA, toA, fromB, toB) {
  return fromA <= toB && fromB <= toA;
}
