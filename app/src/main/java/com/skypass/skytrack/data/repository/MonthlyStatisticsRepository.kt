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

            val response = api.attendanceMonthly(
                endpoint = BuildConfig.SKYTRACK_API_URL,
                authorization = "Bearer ${session.token}",
                request = MonthlyAttendanceRequest(
                    token = session.token,
                    month = month
                )
            )

            if (response.success) {
                Result.success(response)
            } else {
                Result.failure(
                    MonthlyApiException(
                        code = response.error,
                        message = response.message
                            ?: response.error
                            ?: "Unable to load monthly attendance."
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class MonthlyApiException(
    val code: String?,
    override val message: String
) : Exception(message)
