package com.skypass.skytrack.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skypass.skytrack.data.local.Session
import com.skypass.skytrack.data.remote.AttendanceDto
import com.skypass.skytrack.data.repository.AttendanceApiException
import com.skypass.skytrack.data.repository.AttendanceRepository
import com.skypass.skytrack.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val attendance: AttendanceDto? = null,
    val isLoading: Boolean = true,
    val isActionLoading: Boolean = false,
    val error: String? = null,
    val signedOut: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    fun loadToday(session: Session) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            attendanceRepository.getToday(session.token)
                .onSuccess { attendance ->
                    _state.value = _state.value.copy(
                        attendance = attendance,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    handleFailure(error)
                }
        }
    }

    fun startWork(session: Session) {
        performAction(session) {
            attendanceRepository.startWork(session.token)
        }
    }

    fun endWork(session: Session) {
        performAction(session) {
            attendanceRepository.endWork(session.token)
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    private fun performAction(
        session: Session,
        action: suspend () -> Result<com.skypass.skytrack.data.remote.AttendanceDto?>
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isActionLoading = true,
                error = null
            )

            action()
                .onSuccess { attendance ->
                    _state.value = _state.value.copy(
                        attendance = attendance ?: _state.value.attendance,
                        isActionLoading = false
                    )

                    // Refresh from the backend so the UI reflects the
                    // spreadsheet/backend source of truth.
                    attendanceRepository.getToday(session.token)
                        .onSuccess { latest ->
                            _state.value = _state.value.copy(attendance = latest)
                        }
                        .onFailure { error -> handleFailure(error, keepAttendance = true) }
                }
                .onFailure { error ->
                    handleFailure(error, keepAttendance = true)
                }
        }
    }

    private fun handleFailure(error: Throwable, keepAttendance: Boolean = false) {
        val apiError = error as? AttendanceApiException

        if (apiError?.code == "INVALID_SESSION" ||
            apiError?.code == "AUTH_REQUIRED" ||
            apiError?.code == "SESSION_EXPIRED"
        ) {
            viewModelScope.launch {
                authRepository.logout()
            }
            _state.value = _state.value.copy(
                isLoading = false,
                isActionLoading = false,
                error = "Your session has expired. Please sign in again.",
                signedOut = true
            )
            return
        }

        val message = when (apiError?.code) {
            "ALREADY_STARTED" -> "Work has already been started for today."
            "NOT_STARTED" -> "You have not started work today."
            "ALREADY_ENDED" -> "Work has already been ended for today."
            "INVALID_REQUEST" -> apiError.message
            else -> when (error) {
                is UnknownHostException, is ConnectException ->
                    "Unable to connect to SkyTrack server. Check your internet connection."
                else -> apiError?.message ?: error.message ?: "Attendance request failed."
            }
        }

        _state.value = _state.value.copy(
            isLoading = false,
            isActionLoading = false,
            error = message,
            attendance = if (keepAttendance) _state.value.attendance else null
        )
    }
}
