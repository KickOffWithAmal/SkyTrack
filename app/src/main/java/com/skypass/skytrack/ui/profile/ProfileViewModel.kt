package com.skypass.skytrack.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skypass.skytrack.data.local.Session
import com.skypass.skytrack.data.local.SessionDataStore
import com.skypass.skytrack.data.repository.LeaveRepository
import com.skypass.skytrack.data.repository.MonthlyStatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val attendancePercent: String = "100.0%",
    val leaveQuota: String = "18 Days",
    val overtimeWork: String = "0h 00m",
    val reportingManager: String = "Not Assigned",
    val department: String = "Operations",
    val workShift: String = "Standard Shift (09:00 AM - 06:00 PM)"
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val monthlyRepository: MonthlyStatisticsRepository,
    private val leaveRepository: LeaveRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfileData(session: Session) {
        val currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

        // Extract and clean session organization values, stripping old hardcoded defaults
        val manager = session.managerName
            .takeIf { it.isNotBlank() && !it.contains("Dinesh Pillai", ignoreCase = true) }
            ?: "Not Assigned"

        val dept = session.departmentName
            .takeIf { it.isNotBlank() && !it.contains("Schengen & UK", ignoreCase = true) }
            ?: "Operations"

        val shift = session.workShiftName
            .takeIf { it.isNotBlank() && !it.contains("General (09:00", ignoreCase = true) }
            ?: "Standard Shift (09:00 AM - 06:00 PM)"

        val initialAttendance = session.attendancePercent
            .takeIf { it.isNotBlank() && it != "96.4%" }
            ?: "100.0%"

        val initialLeaveQuota = session.leaveQuota
            .takeIf { it.isNotBlank() && it != "11 Days" }
            ?: "18 Days"

        val initialOvertime = session.ytdOvertime
            .takeIf { it.isNotBlank() && it != "+14h 30m" }
            ?: "0h 00m"

        _uiState.value = ProfileUiState(
            isLoading = true,
            attendancePercent = initialAttendance,
            leaveQuota = initialLeaveQuota,
            overtimeWork = initialOvertime,
            reportingManager = manager,
            department = dept,
            workShift = shift
        )

        viewModelScope.launch {
            var finalAttendance = initialAttendance
            var finalOvertime = initialOvertime
            var finalLeaveQuota = initialLeaveQuota

            // 1. Calculate live Attendance % and Overtime from Monthly Attendance records
            try {
                val monthlyResult = monthlyRepository.getMonthlyAttendance(currentMonth)
                monthlyResult.onSuccess { response ->
                    val summary = response.actualSummary
                    if (summary != null) {
                        // Attendance % = presentPercent or (presentDays / workingDays) * 100
                        if (summary.presentPercent != null && summary.presentPercent > 0) {
                            finalAttendance = "${summary.presentPercent}%"
                        } else if (summary.workingDays > 0) {
                            val pct = (summary.presentDays.toDouble() / summary.workingDays.toDouble()) * 100.0
                            finalAttendance = String.format(Locale.US, "%.1f%%", pct)
                        } else if (summary.presentDays > 0) {
                            finalAttendance = "100.0%"
                        }

                        // Overtime = worked minutes exceeding standard 8h (480 mins) per present day
                        val expectedMinutes = summary.presentDays * 480
                        val totalWorked = summary.totalWorkedMinutes ?: 0
                        if (totalWorked > expectedMinutes) {
                            val otMinutes = totalWorked - expectedMinutes
                            val h = otMinutes / 60
                            val m = otMinutes % 60
                            finalOvertime = "+${h}h ${String.format(Locale.US, "%02dm", m)}"
                        } else {
                            finalOvertime = "0h 00m"
                        }
                    }
                }
            } catch (_: Exception) {
                // Keep fallback on error
            }

            // 2. Calculate live Leave Quota from balance or history
            try {
                val balanceResult = leaveRepository.balance(session.token)
                if (balanceResult.isSuccess && balanceResult.getOrNull() != null) {
                    val b = balanceResult.getOrNull()!!
                    val casualLeft = b.casual?.balance ?: 0f
                    val sickLeft = b.sick?.balance ?: 0f
                    val totalLeft = casualLeft + sickLeft
                    finalLeaveQuota = "${totalLeft.toInt()} Days"
                } else {
                    val historyResult = leaveRepository.history(session.token)
                    historyResult.onSuccess { leaves ->
                        val approvedDays = leaves
                            .filter { it.status.equals("approved", ignoreCase = true) }
                            .sumOf { (it.leaveDays ?: 1).toDouble() }
                        val annualQuota = 18.0
                        val remaining = maxOf(0.0, annualQuota - approvedDays)
                        finalLeaveQuota = "${remaining.toInt()} Days"
                    }
                }
            } catch (_: Exception) {
                // Keep fallback on error
            }

            // Update UI State with computed dynamic values
            _uiState.value = ProfileUiState(
                isLoading = false,
                attendancePercent = finalAttendance,
                leaveQuota = finalLeaveQuota,
                overtimeWork = finalOvertime,
                reportingManager = manager,
                department = dept,
                workShift = shift
            )

            // Cache updated dynamic metrics into SessionDataStore
            try {
                sessionDataStore.saveSession(
                    session.copy(
                        managerName = manager,
                        departmentName = dept,
                        workShiftName = shift,
                        attendancePercent = finalAttendance,
                        leaveQuota = finalLeaveQuota,
                        ytdOvertime = finalOvertime
                    )
                )
            } catch (_: Exception) {
                // Ignore cache write error
            }
        }
    }
}
