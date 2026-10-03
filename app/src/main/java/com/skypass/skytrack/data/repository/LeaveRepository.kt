package com.skypass.skytrack.data.repository

import com.skypass.skytrack.BuildConfig
import com.skypass.skytrack.data.remote.AttendanceRequest
import com.skypass.skytrack.data.remote.LeaveDto
import com.skypass.skytrack.data.remote.LeaveHistoryRequest
import com.skypass.skytrack.data.remote.LeaveBalanceRequest
import com.skypass.skytrack.data.remote.LeaveBalanceDataDto
import com.skypass.skytrack.data.remote.LeaveSubmitRequest
import com.skypass.skytrack.data.remote.SkyTrackApi
import com.skypass.skytrack.data.remote.PublicHoliday
import java.time.LocalDate
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
        try {
            val response = api.submitLeave(
                BuildConfig.SKYTRACK_API_URL,
                "Bearer $token",
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
        } catch (httpException: retrofit2.HttpException) {
            val userMsg = com.skypass.skytrack.data.remote.ApiErrorExtractor.getMessage(
                httpException,
                "Leave request failed."
            )
            throw LeaveApiException(null, userMsg)
        }
    }

    suspend fun history(token: String): Result<List<LeaveDto>> = runCatching {
        val response = try {
            api.leaveHistory(
                BuildConfig.SKYTRACK_API_URL,
                "Bearer $token",
                LeaveHistoryRequest("leave_history")
            )
        } catch (httpException: retrofit2.HttpException) {
            val userMsg = com.skypass.skytrack.data.remote.ApiErrorExtractor.getMessage(
                httpException,
                "Unable to load leave history."
            )
            throw LeaveApiException(null, userMsg)
        }

        if (!response.success) {
            val cleanMsg = com.skypass.skytrack.data.remote.ApiErrorExtractor.cleanErrorString(
                response.message ?: "Unable to load leave history.",
                "Unable to load leave history."
            )
            throw LeaveApiException(
                response.error,
                cleanMsg
            )
        }

        response.leaves.orEmpty()
    }

    suspend fun balance(token: String): Result<LeaveBalanceDataDto?> = runCatching {
        val response = try {
            api.leaveBalance(
                BuildConfig.SKYTRACK_API_URL,
                "Bearer $token",
                LeaveBalanceRequest("leave_balance")
            )
        } catch (httpException: retrofit2.HttpException) {
            val userMsg = com.skypass.skytrack.data.remote.ApiErrorExtractor.getMessage(
                httpException,
                "Unable to load leave balance."
            )
            throw LeaveApiException(null, userMsg)
        }

        if (!response.success) {
            val cleanMsg = com.skypass.skytrack.data.remote.ApiErrorExtractor.cleanErrorString(
                response.message ?: "Unable to load leave balance.",
                "Unable to load leave balance."
            )
            throw LeaveApiException(
                response.error,
                cleanMsg
            )
        }

        response.balance
    }

    suspend fun publicHolidays(token: String): Result<List<PublicHoliday>> = runCatching {
        val response = try {
            api.publicHolidays(
                BuildConfig.SKYTRACK_API_URL,
                "Bearer $token",
                AttendanceRequest("public_holidays", token)
            )
        } catch (httpException: retrofit2.HttpException) {
            val userMsg = com.skypass.skytrack.data.remote.ApiErrorExtractor.getMessage(
                httpException,
                "Unable to load public holidays."
            )
            throw LeaveApiException(null, userMsg)
        }

        if (!response.success) {
            val cleanMsg = com.skypass.skytrack.data.remote.ApiErrorExtractor.cleanErrorString(
                response.message ?: "Unable to load public holidays.",
                "Unable to load public holidays."
            )
            throw LeaveApiException(
                response.error,
                cleanMsg
            )
        }

        response.holidays.orEmpty().mapNotNull { dto ->
            val dateStr = dto.date ?: return@mapNotNull null
            try {
                PublicHoliday(
                    date = LocalDate.parse(dateStr),
                    name = dto.name ?: "Holiday",
                    category = dto.category ?: "Public Holiday"
                )
            } catch (_: Exception) {
                null
            }
        }
    }
}

class LeaveApiException(
    val code: String?,
    override val message: String
) : Exception(message)
