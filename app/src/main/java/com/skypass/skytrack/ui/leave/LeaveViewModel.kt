package com.skypass.skytrack.ui.leave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skypass.skytrack.data.local.Session
import com.skypass.skytrack.data.remote.LeaveDto
import com.skypass.skytrack.data.repository.AuthRepository
import com.skypass.skytrack.data.repository.LeaveApiException
import com.skypass.skytrack.data.repository.LeaveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LeaveUiState(
    val leaves: List<LeaveDto> = emptyList(),
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null,
    val signedOut: Boolean = false
)

@HiltViewModel
class LeaveViewModel @Inject constructor(
    private val repository: LeaveRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LeaveUiState())
    val state: StateFlow<LeaveUiState> = _state.asStateFlow()

    fun loadHistory(session: Session) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            repository.history(session.token)
                .onSuccess { leaves ->
                    _state.value = _state.value.copy(
                        leaves = leaves,
                        isLoading = false
                    )
                }
                .onFailure { handleFailure(it) }
        }
    }

    fun submit(
        session: Session,
        from: String,
        to: String,
        type: String,
        reason: String
    ) {
        if (!isValidDate(from) || !isValidDate(to)) {
            _state.value = _state.value.copy(
                error = "Use dates in YYYY-MM-DD format."
            )
            return
        }

        if (from > to) {
            _state.value = _state.value.copy(
                error = "From date cannot be after To date."
            )
            return
        }

        if (type.isBlank()) {
            _state.value = _state.value.copy(error = "Select a leave type.")
            return
        }

        if (reason.trim().length < 3) {
            _state.value = _state.value.copy(
                error = "Please enter a short reason."
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isSubmitting = true,
                error = null,
                successMessage = null
            )

            repository.submit(
                token = session.token,
                from = from,
                to = to,
                type = type,
                reason = reason.trim()
            ).onSuccess {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    successMessage = "Leave request submitted.",
                )
                loadHistory(session)
            }.onFailure {
                handleFailure(it, keepLeaves = true)
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(
            error = null,
            successMessage = null
        )
    }

    private fun handleFailure(error: Throwable, keepLeaves: Boolean = false) {
        val apiError = error as? LeaveApiException

        if (apiError?.code == "INVALID_SESSION" ||
            apiError?.code == "AUTH_REQUIRED" ||
            apiError?.code == "SESSION_EXPIRED"
        ) {
            viewModelScope.launch { authRepository.logout() }
            _state.value = _state.value.copy(
                isLoading = false,
                isSubmitting = false,
                error = "Your session has expired. Please sign in again.",
                signedOut = true
            )
            return
        }

        val message = when (apiError?.code) {
            "INVALID_DATE" -> "Please check the leave dates."
            "OVERLAPPING_LEAVE" -> "These dates overlap an existing leave request."
            "INVALID_LEAVE_TYPE" -> "Select a valid leave type."
            "INVALID_REASON" -> "Please provide a valid reason."
            else -> when (error) {
                is UnknownHostException, is ConnectException ->
                    "Unable to connect to SkyTrack server. Check your internet connection."
                else -> apiError?.message ?: error.message ?: "Leave request failed."
            }
        }

        _state.value = _state.value.copy(
            isLoading = false,
            isSubmitting = false,
            error = message,
            leaves = if (keepLeaves) _state.value.leaves else emptyList()
        )
    }

    private fun isValidDate(value: String): Boolean =
        Regex("""^\d{4}-\d{2}-\d{2}$""").matches(value)
}
