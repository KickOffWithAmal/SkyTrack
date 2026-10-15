package com.skypass.skytrack.data.repository

import com.google.gson.Gson
import com.skypass.skytrack.BuildConfig
import com.skypass.skytrack.data.local.ConnectivityObserver
import com.skypass.skytrack.data.local.db.dao.LeaveDao
import com.skypass.skytrack.data.local.db.dao.PendingActionDao
import com.skypass.skytrack.data.local.db.entity.HolidayCacheEntity
import com.skypass.skytrack.data.local.db.entity.LeaveBalanceCacheEntity
import com.skypass.skytrack.data.local.db.entity.LeaveHistoryCacheEntity
import com.skypass.skytrack.data.local.db.entity.PendingActionEntity
import com.skypass.skytrack.data.remote.AttendanceRequest
import com.skypass.skytrack.data.remote.LeaveBalanceDataDto
import com.skypass.skytrack.data.remote.LeaveBalanceRequest
import com.skypass.skytrack.data.remote.LeaveDto
import com.skypass.skytrack.data.remote.LeaveHistoryRequest
import com.skypass.skytrack.data.remote.LeaveSubmitRequest
import com.skypass.skytrack.data.remote.PublicHoliday
import com.skypass.skytrack.data.remote.SkyTrackApi
import com.skypass.skytrack.data.sync.LeaveSubmitPayload
import java.io.IOException
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaveRepository @Inject constructor(
    private val api: SkyTrackApi,
    private val leaveDao: LeaveDao,
    private val pendingActionDao: PendingActionDao,
    private val connectivityObserver: ConnectivityObserver,
    private val gson: Gson
) {
    suspend fun submit(
        token: String,
        from: String,
        to: String,
        type: String,
        reason: String
    ): Result<LeaveDto?> = runCatching {
        val isOnline = connectivityObserver.isConnected()

        if (!isOnline) {
            return@runCatching queueLeaveSubmit(token, from, to, type, reason)
        }

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
        } catch (ioException: IOException) {
            queueLeaveSubmit(token, from, to, type, reason)
        }
    }

    private suspend fun queueLeaveSubmit(
        token: String,
        from: String,
        to: String,
        type: String,
        reason: String
    ): LeaveDto {
        val payload = LeaveSubmitPayload(
            from = from,
            to = to,
            type = type,
            reason = reason
        )
        pendingActionDao.insert(
            PendingActionEntity(
                actionType = "LEAVE_SUBMIT",
                token = token,
                payloadJson = gson.toJson(payload)
            )
        )

        val provisional = LeaveDto(
            id = "pending_${UUID.randomUUID()}",
            from = from,
            to = to,
            type = type,
            reason = reason,
            status = "Pending"
        )

        // Prepend to cached list
        val current = leaveDao.getLeaveHistory()
        val updated = listOf(LeaveHistoryCacheEntity.fromDto(provisional)) + current
        leaveDao.insertLeaves(updated)

        return provisional
    }

    suspend fun history(token: String): Result<List<LeaveDto>> = runCatching {
        try {
            val response = api.leaveHistory(
                BuildConfig.SKYTRACK_API_URL,
                "Bearer $token",
                LeaveHistoryRequest("leave_history")
            )

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

            val list = response.leaves.orEmpty()
            leaveDao.replaceLeaveHistory(list.mapIndexed { idx, dto -> LeaveHistoryCacheEntity.fromDto(dto, idx) })
            list
        } catch (e: Exception) {
            if (e is LeaveApiException) {
                if (e.code.equals("invalid_session", ignoreCase = true) ||
                    e.code.equals("AUTH_REQUIRED", ignoreCase = true)
                ) {
                    throw e
                }
            }

            val cached = leaveDao.getLeaveHistory().map { it.toDto() }
            if (cached.isNotEmpty()) {
                cached
            } else if (e is IOException || !connectivityObserver.isConnected()) {
                emptyList()
            } else {
                throw e
            }
        }
    }

    suspend fun balance(token: String): Result<LeaveBalanceDataDto?> = runCatching {
        try {
            val response = api.leaveBalance(
                BuildConfig.SKYTRACK_API_URL,
                "Bearer $token",
                LeaveBalanceRequest("leave_balance")
            )

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

            val balance = response.balance
            if (balance != null) {
                leaveDao.insertLeaveBalance(LeaveBalanceCacheEntity.fromDto(balance))
            }
            balance
        } catch (e: Exception) {
            if (e is LeaveApiException) {
                if (e.code.equals("invalid_session", ignoreCase = true) ||
                    e.code.equals("AUTH_REQUIRED", ignoreCase = true)
                ) {
                    throw e
                }
            }

            val cached = leaveDao.getLeaveBalance()?.toDto()
            if (cached != null) {
                cached
            } else if (e is IOException || !connectivityObserver.isConnected()) {
                null
            } else {
                throw e
            }
        }
    }

    suspend fun publicHolidays(token: String): Result<List<PublicHoliday>> = runCatching {
        try {
            val response = api.publicHolidays(
                BuildConfig.SKYTRACK_API_URL,
                "Bearer $token",
                AttendanceRequest("public_holidays", token)
            )

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

            val holidays = response.holidays.orEmpty().mapNotNull { dto ->
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

            leaveDao.replaceHolidays(holidays.map { HolidayCacheEntity.fromModel(it) })
            holidays
        } catch (e: Exception) {
            if (e is LeaveApiException) {
                if (e.code.equals("invalid_session", ignoreCase = true) ||
                    e.code.equals("AUTH_REQUIRED", ignoreCase = true)
                ) {
                    throw e
                }
            }

            val cached = leaveDao.getHolidays().mapNotNull { it.toModel() }
            if (cached.isNotEmpty()) {
                cached
            } else if (e is IOException || !connectivityObserver.isConnected()) {
                emptyList()
            } else {
                throw e
            }
        }
    }

    suspend fun cancel(
        token: String,
        requestId: String
    ): Result<LeaveDto?> = runCatching {
        try {
            val response = api.cancelLeave(
                BuildConfig.SKYTRACK_API_URL,
                "Bearer $token",
                com.skypass.skytrack.data.remote.CancelLeaveRequest(requestId = requestId)
            )

            if (!response.success) {
                val cleanMsg = com.skypass.skytrack.data.remote.ApiErrorExtractor.cleanErrorString(
                    response.message ?: "Unable to cancel leave request.",
                    "Unable to cancel leave request."
                )
                throw LeaveApiException(
                    response.error,
                    cleanMsg
                )
            }

            // Update cached leave history in Room DB
            val current = leaveDao.getLeaveHistory()
            val updated = current.map { cached ->
                if (cached.id == requestId || cached.requestId == requestId) {
                    cached.copy(status = "cancelled")
                } else {
                    cached
                }
            }
            leaveDao.replaceLeaveHistory(updated)

            response.leave
        } catch (httpException: retrofit2.HttpException) {
            val userMsg = com.skypass.skytrack.data.remote.ApiErrorExtractor.getMessage(
                httpException,
                "Unable to cancel leave request."
            )
            throw LeaveApiException(null, userMsg)
        }
    }
}

class LeaveApiException(
    val code: String?,
    override val message: String
) : Exception(message)
