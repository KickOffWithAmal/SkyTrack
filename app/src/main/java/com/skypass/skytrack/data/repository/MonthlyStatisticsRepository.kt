package com.skypass.skytrack.data.repository

import com.skypass.skytrack.BuildConfig
import com.skypass.skytrack.data.local.SessionDataStore
import com.skypass.skytrack.data.remote.MonthlyAttendanceRequest
import com.skypass.skytrack.data.remote.MonthlyAttendanceResponse
import com.skypass.skytrack.data.remote.SkyTrackApi
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class MonthlyStatisticsRepository @Inject constructor(
    private val api: SkyTrackApi,
    private val sessionDataStore: SessionDataStore
) {

    suspend fun getMonthlyAttendance(
        month: String
    ): Result<MonthlyAttendanceResponse> {
        return try {
            val session = sessionDataStore.session.first()

            if (session == null || session.token.isBlank()) {
                return Result.failure(
                    MonthlyApiException(
                        code = "invalid_session",
                        message = "Session expired. Please sign in again."
                    )
                )
            }

            val response = try {
                api.attendanceMonthly(
                    endpoint = BuildConfig.SKYTRACK_API_URL,
                    authorization = "Bearer ${session.token}",
                    request = MonthlyAttendanceRequest(
                        token = session.token,
                        month = month
                    )
                )
            } catch (httpException: retrofit2.HttpException) {
                val friendly = com.skypass.skytrack.data.remote.ApiErrorExtractor.getMessage(
                    httpException,
                    "Unable to load monthly attendance."
                )
                return Result.failure(
                    MonthlyApiException(
                        code = "HTTP_${httpException.code()}",
                        message = friendly
                    )
                )
            }

            if (response.success) {
                Result.success(response)
            } else {
                val cleanMsg = com.skypass.skytrack.data.remote.ApiErrorExtractor.cleanErrorString(
                    response.message ?: response.error ?: "Unable to load monthly attendance.",
                    "Unable to load monthly attendance."
                )
                Result.failure(
                    MonthlyApiException(
                        code = response.error,
                        message = cleanMsg
                    )
                )
            }
        } catch (e: Exception) {
            val friendly = com.skypass.skytrack.data.remote.ApiErrorExtractor.getMessage(
                e,
                "Unable to load monthly attendance."
            )
            Result.failure(MonthlyApiException(code = "ERROR", message = friendly))
        }
    }
}

class MonthlyApiException(
    val code: String?,
    override val message: String
) : Exception(message)
