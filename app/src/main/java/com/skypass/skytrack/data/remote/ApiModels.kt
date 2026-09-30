package com.skypass.skytrack.data.remote

import com.google.gson.annotations.SerializedName

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
    @com.google.gson.annotations.SerializedName("checkIn")
    val login: String? = null,
    @com.google.gson.annotations.SerializedName("checkOut")
    val logout: String? = null,
    val hours: String? = null,
    val status: String? = null,
    val breakStartedAt: String? = null,
    val totalBreakSeconds: Long? = null,
    val workedMinutes: Int? = null,
    val breakMinutes: Int? = null,
    val isOnBreak: Boolean? = null
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
    @SerializedName("id") val id: String? = null,
    @SerializedName("requestId", alternate = ["request_id"]) val requestId: String? = null,
    @SerializedName("employeeId", alternate = ["employee_id"]) val employeeId: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("from", alternate = ["from_date", "fromDate"]) val from: String? = null,
    @SerializedName("to", alternate = ["to_date", "toDate"]) val to: String? = null,
    @SerializedName("type", alternate = ["leave_type", "leaveType"]) val type: String? = null,
    @SerializedName("reason") val reason: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("leaveDays", alternate = ["leave_days"]) val leaveDays: Float? = null,
    @SerializedName("paidLeaveDays", alternate = ["paid_leave_days"]) val paidLeaveDays: Float? = null,
    @SerializedName("lopDays", alternate = ["lop_days"]) val lopDays: Float? = null,
    @SerializedName("managerComment", alternate = ["manager_comment"]) val managerComment: String? = null
)

data class LeaveBalanceRequest(
    val action: String = "leave_balance"
)

data class LeaveHistoryRequest(
    val action: String = "leave_history"
)

data class LeaveBalanceItemDto(
    val allocated: Float? = null,
    val adjusted: Float? = null,
    val used: Float? = null,
    val balance: Float? = null
)

data class LeaveBalanceDataDto(
    val casual: LeaveBalanceItemDto? = null,
    val sick: LeaveBalanceItemDto? = null
)

data class LeaveBalanceResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null,
    val balance: LeaveBalanceDataDto? = null
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
    val attendance: List<MonthlyAttendanceDto>? = null,
    val data: MonthlyAttendanceDataDto? = null
) {
    val actualSummary: MonthlyAttendanceSummary? get() = data?.summary ?: summary
    val actualAttendance: List<MonthlyAttendanceDto> get() = data?.attendance ?: attendance ?: emptyList()
}

data class MonthlyAttendanceDataDto(
    val summary: MonthlyAttendanceSummary? = null,
    val attendance: List<MonthlyAttendanceDto> = emptyList()
)

data class MonthlyAttendanceSummary(
    val month: String,
    @SerializedName("workingDays", alternate = ["working_days"]) val workingDays: Int,
    @SerializedName("presentDays", alternate = ["present_days"]) val presentDays: Int,
    @SerializedName("absentDays", alternate = ["absent_days"]) val absentDays: Int,
    @SerializedName("incompleteDays", alternate = ["incomplete_days"]) val incompleteDays: Int,
    @SerializedName("totalHours", alternate = ["total_hours", "total_active_formatted"]) val totalHours: String,
    @SerializedName("averageHours", alternate = ["average_hours", "average_active_formatted"]) val averageHours: String,
    @SerializedName("targetHoursFormatted", alternate = ["target_hours_formatted", "required_hours_formatted"]) val targetHoursFormatted: String? = null,
    @SerializedName("totalBreakFormatted", alternate = ["total_break_formatted"]) val totalBreakFormatted: String? = null,
    @SerializedName("presentPercent", alternate = ["present_percent"]) val presentPercent: Int? = null,
    val totalWorkedMinutes: Int? = null,
    val totalBreakMinutes: Int? = null
)

data class MonthlyAttendanceDto(
    val date: String,
    @SerializedName("employeeId", alternate = ["employee_id"]) val employeeId: String? = null,
    val name: String? = null,
    @SerializedName("login", alternate = ["check_in"]) val login: String? = null,
    @SerializedName("logout", alternate = ["check_out"]) val logout: String? = null,
    @SerializedName("hours", alternate = ["active_formatted"]) val hours: String? = null,
    @SerializedName("breakFormatted", alternate = ["break_formatted"]) val breakFormatted: String? = null,
    val status: String,
    val workedMinutes: Int? = null,
    val breakMinutes: Int? = null
)

data class HolidayDto(
    val id: String? = null,
    val date: String? = null,
    val name: String? = null,
    val category: String? = null
)

data class HolidayResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null,
    val holidays: List<HolidayDto>? = null
)

data class PublicHoliday(
    val date: java.time.LocalDate,
    val name: String,
    val category: String = "Public Holiday"
)


