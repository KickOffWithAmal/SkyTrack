package com.skypass.skytrack.data.repository

import com.skypass.skytrack.BuildConfig
import com.skypass.skytrack.data.local.Session
import com.skypass.skytrack.data.local.SessionDataStore
import com.skypass.skytrack.data.remote.LoginRequest
import com.skypass.skytrack.data.remote.SkyTrackApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class AuthRepository @Inject constructor(
    private val api: SkyTrackApi,
    private val sessionDataStore: SessionDataStore
) {
    val session: Flow<Session?> = sessionDataStore.session

    suspend fun login(employeeId: String, password: String): Result<Session> = runCatching {
        val response = api.login(
            endpoint = BuildConfig.SKYTRACK_API_URL,
            request = LoginRequest(
                employeeId = employeeId.trim(),
                password = password
            )
        )

        if (!response.success || response.session == null || response.employee == null) {
            throw IllegalStateException(
                response.message ?: "Invalid employee ID or password."
            )
        }

        Session(
            token = response.session.token,
            employeeId = response.employee.employeeId,
            name = response.employee.name,
            role = response.employee.role
        ).also { sessionDataStore.saveSession(it) }
    }

    suspend fun logout() {
        sessionDataStore.clear()
    }
}
