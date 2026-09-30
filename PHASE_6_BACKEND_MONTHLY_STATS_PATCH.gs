/*
 * SkyTrack Phase 6 - Monthly Attendance Statistics
 *
 * Add this switch case to doPost():
 *
 * case 'attendance_monthly':
 *   return handleAttendanceMonthly(body);
 *
 * Monday through Saturday are working days.
 * Sunday is the weekly off day.
 */

function handleAttendanceMonthly(body) {
  try {
    var token = body.token;
    var month = body.month;

    if (!token) {
      return jsonResponse({
        success: false,
        error: 'MISSING_TOKEN',
        message: 'Session token is required.'
      });
    }

    var employee = authenticateToken(token);

    if (!employee) {
      return jsonResponse({
        success: false,
        error: 'INVALID_SESSION',
        message: 'Session expired or invalid.'
      });
    }

    if (!month || !/^\d{4}-\d{2}$/.test(month)) {
      return jsonResponse({
        success: false,
        error: 'INVALID_MONTH',
        message: 'Month must be in YYYY-MM format.'
      });
    }

    var sheet = SpreadsheetApp.getActiveSpreadsheet()
      .getSheetByName(CONFIG.ATTENDANCE_SHEET);

    if (!sheet) {
      return jsonResponse({
        success: false,
        error: 'ATTENDANCE_SHEET_NOT_FOUND',
        message: 'Attendance sheet was not found.'
      });
    }

    var values = sheet.getDataRange().getValues();

    if (values.length <= 1) {
      return jsonResponse({
        success: true,
        summary: {
          month: month,
          workingDays: countWorkingDaysMondayToSaturday(month),
          presentDays: 0,
          absentDays: countWorkingDaysMondayToSaturday(month),
          incompleteDays: 0,
          totalHours: '00:00',
          averageHours: '00:00'
        },
        attendance: []
      });
    }

    var headers = values[0];

    var dateIndex = headers.indexOf('Date');
    var employeeIdIndex = headers.indexOf('Employee ID');
    var nameIndex = headers.indexOf('Name');
    var loginIndex = headers.indexOf('Login');
    var logoutIndex = headers.indexOf('Logout');
    var hoursIndex = headers.indexOf('Hours');
    var statusIndex = headers.indexOf('Status');

    if (dateIndex === -1 || employeeIdIndex === -1) {
      return jsonResponse({
        success: false,
        error: 'INVALID_ATTENDANCE_HEADERS',
        message: 'Attendance sheet headers are missing.'
      });
    }

    var attendance = [];

    for (var i = 1; i < values.length; i++) {
      var row = values[i];

      if (String(row[employeeIdIndex] || '').trim() !== employee.employeeId) {
        continue;
      }

      if (!row[dateIndex]) {
        continue;
      }

      var dateString = formatAttendanceDate(row[dateIndex]);

      if (dateString.substring(0, 7) !== month) {
        continue;
      }

      attendance.push({
        date: dateString,
        employeeId: String(row[employeeIdIndex] || ''),
        name: String(row[nameIndex] || ''),
        login: formatAttendanceTime(row[loginIndex]),
        logout: formatAttendanceTime(row[logoutIndex]),
        hours: String(row[hoursIndex] || ''),
        status: String(row[statusIndex] || '')
      });
    }

    attendance.sort(function(a, b) {
      return a.date.localeCompare(b.date);
    });

    var presentDays = 0;
    var incompleteDays = 0;
    var totalMinutes = 0;

    attendance.forEach(function(record) {
      var status = record.status.toLowerCase();

      if (status === 'present') {
        presentDays++;
      }

      if (
        status === 'incomplete' ||
        status === 'missing checkout' ||
        (!record.logout && record.login)
      ) {
        incompleteDays++;
      }

      totalMinutes += parseHoursToMinutes(record.hours);
    });

    var workingDays = countWorkingDaysMondayToSaturday(month);
    var absentDays = Math.max(
      0,
      workingDays - presentDays - incompleteDays
    );

    var averageMinutes = attendance.length > 0
      ? Math.round(totalMinutes / attendance.length)
      : 0;

    return jsonResponse({
      success: true,
      summary: {
        month: month,
        workingDays: workingDays,
        presentDays: presentDays,
        absentDays: absentDays,
        incompleteDays: incompleteDays,
        totalHours: minutesToHours(totalMinutes),
        averageHours: minutesToHours(averageMinutes)
      },
      attendance: attendance
    });

  } catch (error) {
    return jsonResponse({
      success: false,
      error: 'SERVER_ERROR',
      message: error.message
    });
  }
}

function countWorkingDaysMondayToSaturday(month) {
  var parts = month.split('-');
  var year = parseInt(parts[0], 10);
  var monthNumber = parseInt(parts[1], 10);

  var daysInMonth = new Date(year, monthNumber, 0).getDate();
  var workingDays = 0;

  for (var day = 1; day <= daysInMonth; day++) {
    var date = new Date(year, monthNumber - 1, day);
    var dayOfWeek = date.getDay();

    // Sunday = 0. Monday-Saturday = working days.
    if (dayOfWeek !== 0) {
      workingDays++;
    }
  }

  return workingDays;
}

function formatAttendanceDate(value) {
  if (Object.prototype.toString.call(value) === '[object Date]') {
    return Utilities.formatDate(
      value,
      Session.getScriptTimeZone(),
      'yyyy-MM-dd'
    );
  }

  return String(value).substring(0, 10);
}

function formatAttendanceTime(value) {
  if (!value) {
    return '';
  }

  if (Object.prototype.toString.call(value) === '[object Date]') {
    return Utilities.formatDate(
      value,
      Session.getScriptTimeZone(),
      'HH:mm'
    );
  }

  return String(value);
}

function parseHoursToMinutes(hours) {
  if (!hours) {
    return 0;
  }

  var match = String(hours).trim().match(/^(\d+):(\d{2})$/);

  if (!match) {
    return 0;
  }

  return (
    parseInt(match[1], 10) * 60 +
    parseInt(match[2], 10)
  );
}

function minutesToHours(minutes) {
  var hours = Math.floor(minutes / 60);
  var mins = minutes % 60;

  return String(hours).padStart(2, '0') +
    ':' +
    String(mins).padStart(2, '0');
}
