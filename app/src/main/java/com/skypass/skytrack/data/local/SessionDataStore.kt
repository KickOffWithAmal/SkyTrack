package com.skypass.skytrack.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(name = "skytrack_session")

@Singleton
class SessionDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val token = stringPreferencesKey("session_token")
        val employeeId = stringPreferencesKey("employee_id")
        val employeeName = stringPreferencesKey("employee_name")
        val employeeRole = stringPreferencesKey("employee_role")
    }

    val session: Flow<Session?> = context.sessionDataStore.data.map { prefs ->
        val token = prefs[Keys.token]
        if (token.isNullOrBlank()) {
            null
        } else {
            Session(
                token = token,
                employeeId = prefs[Keys.employeeId].orEmpty(),
                name = prefs[Keys.employeeName].orEmpty(),
                role = prefs[Keys.employeeRole].orEmpty()
            )
        }
    }

    suspend fun saveSession(session: Session) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.token] = session.token
            prefs[Keys.employeeId] = session.employeeId
            prefs[Keys.employeeName] = session.name
            prefs[Keys.employeeRole] = session.role
        }
    }

    suspend fun clear() {
        context.sessionDataStore.edit { it.clear() }
    }
}

