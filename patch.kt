data class EmployeeDto(
    val employeeId: String,
    val name: String,
    val role: String,
    val managerName: String? = null,
    val departmentName: String? = null,
    val workShiftName: String? = null,
    val attendancePercent: String? = null,
    val leaveQuota: String? = null,
    val ytdOvertime: String? = null
)
