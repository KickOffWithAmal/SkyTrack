package com.skypass.skytrack.data.repository

import com.skypass.skytrack.data.remote.MonthlyAttendanceRequest
import com.skypass.skytrack.data.remote.MonthlyAttendanceResponse
import com.skypass.skytrack.data.remote.SkyTrackApi
import javax.inject.Inject

class MonthlyStatisticsRepository @Inject constructor(
    private val api: SkyTrackApi
) {
    suspend fun getMonthlyAttendance(
        token: String,
        month: String
    ): Result<MonthlyAttendanceResponse> = runCatching {
        val response = api.attendanceMonthly(
            endpoint = com.skypass.skytrack.BuildConfig.SKYTRACK_API_URL,
            request = MonthlyAttendanceRequest(token = token, month = month)
        )
        if (!response.success) {
            throw IllegalStateException(response.message ?: response.error ?: "Unable to load monthly attendance.")
        }
        response
    }
}
