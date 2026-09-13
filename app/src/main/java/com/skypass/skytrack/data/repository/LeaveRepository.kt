package com.skypass.skytrack.data.repository

import com.skypass.skytrack.BuildConfig
import com.skypass.skytrack.data.remote.AttendanceRequest
import com.skypass.skytrack.data.remote.LeaveDto
import com.skypass.skytrack.data.remote.LeaveSubmitRequest
import com.skypass.skytrack.data.remote.SkyTrackApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaveRepository @Inject constructor(
    private val api: SkyTrackApi
) {
    suspend fun submit(
        token: String,
        from: String,
        to: String,
        type: String,
        reason: String
    ): Result<LeaveDto?> = runCatching {
        val response = api.submitLeave(
            BuildConfig.SKYTRACK_API_URL,
            LeaveSubmitRequest(
                token = token,
                from = from,
                to = to,
                type = type,
                reason = reason
            )
        )

        if (!response.success) {
            throw LeaveApiException(
                response.error,
                response.message ?: "Leave request failed."
            )
        }

        response.leave
    }

    suspend fun history(token: String): Result<List<LeaveDto>> = runCatching {
        val response = api.leaveHistory(
            BuildConfig.SKYTRACK_API_URL,
            AttendanceRequest("leave_history", token)
        )

        if (!response.success) {
            throw LeaveApiException(
                response.error,
                response.message ?: "Unable to load leave history."
            )
        }

        response.leaves.orEmpty()
    }
}

class LeaveApiException(
    val code: String?,
    override val message: String
) : Exception(message)
