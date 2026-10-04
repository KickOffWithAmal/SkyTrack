package com.skypass.skytrack.ui.monthly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skypass.skytrack.data.remote.MonthlyAttendanceDto
import com.skypass.skytrack.data.remote.MonthlyAttendanceSummary
import com.skypass.skytrack.data.repository.AuthRepository
import com.skypass.skytrack.data.repository.MonthlyApiException
import com.skypass.skytrack.data.repository.MonthlyStatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class MonthlyStatisticsUiState(
    val isLoading: Boolean = false,
    val summary: MonthlyAttendanceSummary? = null,
    val attendance: List<MonthlyAttendanceDto> = emptyList(),
    val error: String? = null,
    val totalActiveHoursStr: String = "0h 00m",
    val actualHoursNeededStr: String = "0h",
    val selectedMonthRaw: String = java.time.LocalDate.now().toString().substring(0, 7)
)

@HiltViewModel
class MonthlyStatisticsViewModel @Inject constructor(
    private val repository: MonthlyStatisticsRepository,
    private val authRepository: AuthRepository,
    private val leaveRepository: com.skypass.skytrack.data.repository.LeaveRepository,
    private val sessionDataStore: com.skypass.skytrack.data.local.SessionDataStore
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(MonthlyStatisticsUiState())

    val uiState: StateFlow<MonthlyStatisticsUiState> =
        _uiState.asStateFlow()

    private var selectedMonth: LocalDate =
        LocalDate.now().withDayOfMonth(1)

    init {
        loadMonth()
    }

    fun loadPreviousMonth() {
        selectedMonth = selectedMonth.minusMonths(1)
        loadMonth()
    }

    fun loadNextMonth() {
        val currentMonth = LocalDate.now().withDayOfMonth(1)
        if (selectedMonth.isBefore(currentMonth)) {
            selectedMonth = selectedMonth.plusMonths(1)
            loadMonth()
        }
    }

    fun retry() {
        loadMonth()
    }

    private fun loadMonth() {
        val month = selectedMonth.toString().substring(0, 7)

        viewModelScope.launch {
            _uiState.value = MonthlyStatisticsUiState(isLoading = true, selectedMonthRaw = month)
            android.util.Log.d("MonthlyStats", "Fetching attendance for month: $month")

            repository
                .getMonthlyAttendance(month)
                .onSuccess { response ->
                    android.util.Log.d("MonthlyStats", "API Response Success: $response")
                    
                    val summary = response.actualSummary
                    val attendance = response.actualAttendance

                    val totalActiveHoursStr = calculateTotalActiveHours(summary, attendance)
                    val actualHoursNeededStr = summary?.targetHoursFormatted ?: "${(summary?.workingDays ?: 20) * 8}h"

                    _uiState.value = MonthlyStatisticsUiState(
                        isLoading = false,
                        summary = summary,
                        attendance = attendance,
                        totalActiveHoursStr = totalActiveHoursStr,
                        actualHoursNeededStr = actualHoursNeededStr,
                        selectedMonthRaw = month
                    )
                }
                .onFailure { error ->
                    android.util.Log.e("MonthlyStats", "API Response Error: ", error)
                    val isSessionExpired = (error as? MonthlyApiException)?.code.equals("invalid_session", ignoreCase = true) ||
                        error.message?.contains("session is invalid", ignoreCase = true) == true ||
                        error.message?.contains("session expired", ignoreCase = true) == true ||
                        error.message?.contains("invalid_session", ignoreCase = true) == true

                    if (isSessionExpired) {
                        authRepository.logout()
                    }

                    _uiState.value = MonthlyStatisticsUiState(
                        isLoading = false,
                        error = error.message ?: "Unable to load attendance.",
                        selectedMonthRaw = month
                    )
                }
        }
    }

    private fun calculateTotalActiveHours(
        summary: MonthlyAttendanceSummary?,
        attendance: List<MonthlyAttendanceDto>
    ): String {
        val serverTotal = summary?.totalHours?.trim()
        val isServerZeroOrInvalid = serverTotal.isNullOrBlank() ||
                serverTotal == "00:00" ||
                serverTotal == "0h 00m" ||
                serverTotal == "0h 0m" ||
                serverTotal == "0h" ||
                serverTotal == "0m"

        var totalMinutes = summary?.totalWorkedMinutes ?: 0

        if (totalMinutes <= 0 && attendance.isNotEmpty()) {
            for (item in attendance) {
                val itemMinutes = item.workedMinutes ?: 0
                if (itemMinutes > 0) {
                    totalMinutes += itemMinutes
                } else if (!item.hours.isNullOrBlank() && item.hours != "0h 00m" && item.hours != "00:00") {
                    totalMinutes += parseHoursStringToMinutes(item.hours)
                } else if (!item.login.isNullOrBlank() && !item.logout.isNullOrBlank()) {
                    totalMinutes += calculateMinutesFromTimes(item.login, item.logout, item.breakMinutes ?: 0)
                }
            }
        }

        if (!isServerZeroOrInvalid && serverTotal != null && totalMinutes <= 0) {
            return serverTotal
        }

        if (totalMinutes > 0) {
            val h = totalMinutes / 60
            val m = totalMinutes % 60
            return "${h}h ${String.format(java.util.Locale.ENGLISH, "%02dm", m)}"
        }

        return if (!serverTotal.isNullOrBlank() && serverTotal != "00:00") serverTotal else "0h 00m"
    }

    private fun parseHoursStringToMinutes(hoursStr: String): Int {
        try {
            val hmMatch = Regex("""(\d+)h\s*(\d+)m""").find(hoursStr)
            if (hmMatch != null) {
                val h = hmMatch.groupValues[1].toIntOrNull() ?: 0
                val m = hmMatch.groupValues[2].toIntOrNull() ?: 0
                return (h * 60) + m
            }
            val colonParts = hoursStr.split(":")
            if (colonParts.size == 2) {
                val h = colonParts[0].trim().toIntOrNull() ?: 0
                val m = colonParts[1].trim().toIntOrNull() ?: 0
                return (h * 60) + m
            }
            val hOnlyMatch = Regex("""(\d+)\s*h""").find(hoursStr)
            if (hOnlyMatch != null) {
                return (hOnlyMatch.groupValues[1].toIntOrNull() ?: 0) * 60
            }
            val mOnlyMatch = Regex("""(\d+)\s*m""").find(hoursStr)
            if (mOnlyMatch != null) {
                return mOnlyMatch.groupValues[1].toIntOrNull() ?: 0
            }
        } catch (_: Exception) {}
        return 0
    }

    private fun calculateMinutesFromTimes(login: String, logout: String, breakMinutes: Int): Int {
        try {
            val timeFormat = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.ENGLISH)
            val inDate = timeFormat.parse(login) ?: return 0
            val outDate = timeFormat.parse(logout) ?: return 0
            val diffMs = Math.max(0, outDate.time - inDate.time)
            val grossMinutes = (diffMs / 60000).toInt()
            return Math.max(0, grossMinutes - breakMinutes)
        } catch (_: Exception) {
            try {
                val time24 = java.text.SimpleDateFormat("HH:mm", java.util.Locale.ENGLISH)
                val inDate = time24.parse(login) ?: return 0
                val outDate = time24.parse(logout) ?: return 0
                val diffMs = Math.max(0, outDate.time - inDate.time)
                val grossMinutes = (diffMs / 60000).toInt()
                return Math.max(0, grossMinutes - breakMinutes)
            } catch (_: Exception) {
                return 0
            }
        }
    }
}
