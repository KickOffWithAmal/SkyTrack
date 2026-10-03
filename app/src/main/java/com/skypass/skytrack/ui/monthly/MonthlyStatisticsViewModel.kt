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

                    val totalActiveHoursStr = summary?.totalHours ?: "0h 00m"
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
}
