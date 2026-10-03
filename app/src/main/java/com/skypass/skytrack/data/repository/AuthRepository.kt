package com.skypass.skytrack.data.repository

import com.skypass.skytrack.BuildConfig
import com.skypass.skytrack.data.local.RememberedAccount
import com.skypass.skytrack.data.local.Session
import com.skypass.skytrack.data.local.SessionDataStore
import com.skypass.skytrack.data.remote.LoginRequest
import com.skypass.skytrack.data.remote.SkyTrackApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.skypass.skytrack.data.remote.ApiErrorExtractor
import retrofit2.HttpException

@Singleton
class AuthRepository @Inject constructor(
    private val api: SkyTrackApi,
    private val sessionDataStore: SessionDataStore
) {
    private val _session = MutableStateFlow<Session?>(null)
    val session: Flow<Session?> = _session.asStateFlow()
    val rememberedAccount: Flow<RememberedAccount> = sessionDataStore.rememberedAccount

    suspend fun login(
        employeeId: String,
        password: String,
        rememberMe: Boolean = false
    ): Result<Session> = runCatching {
        val trimmedEmployeeId = employeeId.trim()
        val response = try {
            api.login(
                endpoint = BuildConfig.SKYTRACK_API_URL,
                request = LoginRequest(
                    employeeId = trimmedEmployeeId,
                    password = password
                )
            )
        } catch (httpException: HttpException) {
            val friendlyMsg = ApiErrorExtractor.getMessage(
                httpException,
                "Invalid Employee ID or password."
            )
            throw IllegalStateException(friendlyMsg)
        }

        if (!response.success || response.session == null || response.employee == null) {
            val message = response.message
                ?.takeIf { it.isNotBlank() }
                ?: "Invalid Employee ID or password."
            throw IllegalStateException(ApiErrorExtractor.cleanErrorString(message, "Invalid Employee ID or password."))
        }

        Session(
            token = response.session.token,
            employeeId = response.employee.employeeId,
            name = response.employee.name,
            role = response.employee.role,
            managerName = response.employee.managerName ?: "",
            departmentName = response.employee.departmentName ?: "",
            workShiftName = response.employee.workShiftName ?: "",
            attendancePercent = response.employee.attendancePercent ?: "",
            leaveQuota = response.employee.leaveQuota ?: "",
            ytdOvertime = response.employee.ytdOvertime ?: ""
        ).also {
            _session.value = it
            sessionDataStore.saveRememberMe(rememberMe, trimmedEmployeeId, password)
        }
    }

    suspend fun logout() {
        _session.value = null
    }
}
