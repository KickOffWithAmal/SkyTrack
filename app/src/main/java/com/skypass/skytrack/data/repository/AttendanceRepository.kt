package com.skypass.skytrack.data.repository

import com.skypass.skytrack.BuildConfig
import com.skypass.skytrack.data.remote.AttendanceDto
import com.skypass.skytrack.data.remote.AttendanceRequest
import com.skypass.skytrack.data.remote.SkyTrackApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor(
    private val api: SkyTrackApi
) {
    suspend fun getToday(token: String): Result<AttendanceDto?> = request("attendance_today", token)

    suspend fun startWork(token: String): Result<AttendanceDto?> = request("start_work", token)

    suspend fun endWork(token: String): Result<AttendanceDto?> = request("end_work", token)

    private suspend fun request(action: String, token: String): Result<AttendanceDto?> =
        runCatching {
            val response = when (action) {
                "start_work" -> api.startWork(
                    BuildConfig.SKYTRACK_API_URL,
                    AttendanceRequest(action, token)
                )
                "end_work" -> api.endWork(
                    BuildConfig.SKYTRACK_API_URL,
                    AttendanceRequest(action, token)
                )
                else -> api.attendanceToday(
                    BuildConfig.SKYTRACK_API_URL,
                    AttendanceRequest(action, token)
                )
            }

            if (!response.success) {
                throw AttendanceApiException(
                    code = response.error,
                    message = response.message ?: "Attendance request failed."
                )
            }

            response.attendance
        }
}

class AttendanceApiException(
    val code: String?,
    override val message: String
) : Exception(message)
