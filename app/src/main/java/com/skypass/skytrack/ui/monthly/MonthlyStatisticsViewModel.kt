package com.skypass.skytrack.ui.monthly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skypass.skytrack.data.local.Session
import com.skypass.skytrack.data.remote.MonthlyAttendanceDto
import com.skypass.skytrack.data.remote.MonthlyAttendanceSummary
import com.skypass.skytrack.data.repository.MonthlyStatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MonthlyStatisticsUiState(
    val isLoading: Boolean = false,
    val summary: MonthlyAttendanceSummary? = null,
    val attendance: List<MonthlyAttendanceDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class MonthlyStatisticsViewModel @Inject constructor(
    private val repository: MonthlyStatisticsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MonthlyStatisticsUiState())
    val uiState: StateFlow<MonthlyStatisticsUiState> = _uiState.asStateFlow()

    private var selectedMonth = LocalDate.now().withDayOfMonth(1)

    fun load(session: Session) {
        val month = selectedMonth.toString().substring(0, 7)
        viewModelScope.launch {
            _uiState.value = MonthlyStatisticsUiState(isLoading = true)
            repository.getMonthlyAttendance(session.token, month)
                .onSuccess { response ->
                    _uiState.value = MonthlyStatisticsUiState(
                        summary = response.summary,
                        attendance = response.attendance
                    )
                }
                .onFailure { error ->
                    _uiState.value = MonthlyStatisticsUiState(error = error.message ?: "Unable to load attendance.")
                }
        }
    }

    fun loadPreviousMonth(session: Session) {
        selectedMonth = selectedMonth.minusMonths(1)
        load(session)
    }

    fun loadNextMonth(session: Session) {
        val current = LocalDate.now().withDayOfMonth(1)
        if (selectedMonth.isBefore(current)) {
            selectedMonth = selectedMonth.plusMonths(1)
            load(session)
        }
    }
}
