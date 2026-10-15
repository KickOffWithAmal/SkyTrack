package com.skypass.skytrack.data.repository

import com.skypass.skytrack.BuildConfig
import com.skypass.skytrack.data.local.ConnectivityObserver
import com.skypass.skytrack.data.local.SessionDataStore
import com.skypass.skytrack.data.local.db.dao.MonthlyDao
import com.skypass.skytrack.data.local.db.entity.MonthlyAttendanceCacheEntity
import com.skypass.skytrack.data.local.db.entity.MonthlyAttendanceSummaryCacheEntity
import com.skypass.skytrack.data.remote.MonthlyAttendanceRequest
import com.skypass.skytrack.data.remote.MonthlyAttendanceResponse
import com.skypass.skytrack.data.remote.SkyTrackApi
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class MonthlyStatisticsRepository @Inject constructor(
    private val api: SkyTrackApi,
    private val sessionDataStore: SessionDataStore,
    private val monthlyDao: MonthlyDao,
    private val connectivityObserver: ConnectivityObserver
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

            try {
                val response = api.attendanceMonthly(
                    endpoint = BuildConfig.SKYTRACK_API_URL,
                    authorization = "Bearer ${session.token}",
                    request = MonthlyAttendanceRequest(
                        token = session.token,
                        month = month
                    )
                )

                if (response.success) {
                    val summary = response.actualSummary
                    val list = response.actualAttendance
                    monthlyDao.replaceMonthlyData(
                        month = month,
                        summary = summary?.let { MonthlyAttendanceSummaryCacheEntity.fromModel(it) },
                        attendance = list.map { MonthlyAttendanceCacheEntity.fromDto(month, it) }
                    )
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
            } catch (httpException: retrofit2.HttpException) {
                val friendly = com.skypass.skytrack.data.remote.ApiErrorExtractor.getMessage(
                    httpException,
                    "Unable to load monthly attendance."
                )
                Result.failure(
                    MonthlyApiException(
                        code = "HTTP_${httpException.code()}",
                        message = friendly
                    )
                )
            } catch (ioException: IOException) {
                // Fallback to local cache if network is unavailable
                val cachedSummary = monthlyDao.getMonthlySummary(month)
                val cachedList = monthlyDao.getMonthlyAttendance(month)

                if (cachedSummary != null || cachedList.isNotEmpty()) {
                    Result.success(
                        MonthlyAttendanceResponse(
                            success = true,
                            summary = cachedSummary?.toModel(),
                            attendance = cachedList.map { it.toDto() }
                        )
                    )
                } else {
                    Result.failure(
                        MonthlyApiException(
                            code = "OFFLINE",
                            message = "No cached data available for $month. Check your internet connection."
                        )
                    )
                }
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
