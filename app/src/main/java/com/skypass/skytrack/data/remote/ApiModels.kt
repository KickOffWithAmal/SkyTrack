package com.skypass.skytrack.data.remote

data class LoginRequest(
    val action: String = "login",
    val employeeId: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null,
    val session: SessionDto? = null,
    val employee: EmployeeDto? = null
)

data class SessionDto(
    val token: String,
    val expiresInSeconds: Long
)

data class EmployeeDto(
    val employeeId: String,
    val name: String,
    val role: String
)

data class AttendanceRequest(
    val action: String,
    val token: String
)

data class AttendanceResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null,
    val attendance: AttendanceDto? = null
)

data class AttendanceDto(
    val date: String? = null,
    val employeeId: String? = null,
    val name: String? = null,
    val login: String? = null,
    val logout: String? = null,
    val hours: String? = null,
    val status: String? = null
)

data class LeaveSubmitRequest(
    val action: String = "leave_submit",
    val token: String,
    val from: String,
    val to: String,
    val type: String,
    val reason: String
)

data class LeaveResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null,
    val leave: LeaveDto? = null,
    val leaves: List<LeaveDto>? = null
)

data class LeaveDto(
    val requestId: String? = null,
    val employeeId: String? = null,
    val name: String? = null,
    val from: String? = null,
    val to: String? = null,
    val type: String? = null,
    val reason: String? = null,
    val status: String? = null
)

data class MonthlyAttendanceRequest(
    val action: String = "attendance_monthly",
    val token: String,
    val month: String
)

data class MonthlyAttendanceResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null,
    val summary: MonthlyAttendanceSummary? = null,
    val attendance: List<MonthlyAttendanceDto> = emptyList()
)

data class MonthlyAttendanceSummary(
    val month: String,
    val workingDays: Int,
    val presentDays: Int,
    val absentDays: Int,
    val incompleteDays: Int,
    val totalHours: String,
    val averageHours: String
)

data class MonthlyAttendanceDto(
    val date: String,
    val employeeId: String,
    val name: String,
    val login: String,
    val logout: String,
    val hours: String,
    val status: String
)

