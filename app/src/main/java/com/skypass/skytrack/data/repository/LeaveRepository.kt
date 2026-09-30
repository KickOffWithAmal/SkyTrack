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
            val errorBody = httpException.response()?.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                try {
                    val parsed = com.google.gson.JsonParser.parseString(errorBody).asJsonObject
                    val errCode = parsed.get("error")?.asString
                    val errMsg = parsed.get("message")?.asString
                    throw LeaveApiException(errCode, errMsg ?: "Leave request failed.")
                } catch (e: LeaveApiException) {
                    throw e
                } catch (_: Exception) {
                    throw httpException
                }
            } else {
                throw httpException
            }
        }
    }

    suspend fun history(token: String): Result<List<LeaveDto>> = runCatching {
        val response = api.leaveHistory(
            BuildConfig.SKYTRACK_API_URL,
            "Bearer $token",
            LeaveHistoryRequest("leave_history")
        )

        if (!response.success) {
            throw LeaveApiException(
                response.error,
                response.message ?: "Unable to load leave history."
            )
        }

        response.leaves.orEmpty()
    }

    suspend fun balance(token: String): Result<LeaveBalanceDataDto?> = runCatching {
        val response = api.leaveBalance(
            BuildConfig.SKYTRACK_API_URL,
            "Bearer $token",
            LeaveBalanceRequest("leave_balance")
        )

        if (!response.success) {
            throw LeaveApiException(
                response.error,
                response.message ?: "Unable to load leave balance."
            )
        }

        response.balance
    }

    suspend fun publicHolidays(token: String): Result<List<PublicHoliday>> = runCatching {
        val response = api.publicHolidays(
            BuildConfig.SKYTRACK_API_URL,
            "Bearer $token",
            AttendanceRequest("public_holidays", token)
        )

        if (!response.success) {
            throw LeaveApiException(
                response.error,
                response.message ?: "Unable to load public holidays."
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
