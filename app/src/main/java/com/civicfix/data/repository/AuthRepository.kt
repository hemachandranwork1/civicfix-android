package com.civicfix.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.civicfix.data.remote.ApiService
import com.civicfix.data.remote.models.LoginRequest
import com.civicfix.data.remote.models.RegisterRequest
import com.civicfix.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val KEY_TOKEN      = stringPreferencesKey("token")
        val KEY_USER_ID    = intPreferencesKey("user_id")
        val KEY_USER_NAME  = stringPreferencesKey("user_name")
        val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        val KEY_USER_ROLE  = stringPreferencesKey("user_role")
    }

    val token:    Flow<String?> = dataStore.data.map { it[KEY_TOKEN] }
    val userId:   Flow<Int?>    = dataStore.data.map { it[KEY_USER_ID] }
    val userName: Flow<String?> = dataStore.data.map { it[KEY_USER_NAME] }
    val userRole: Flow<String?> = dataStore.data.map { it[KEY_USER_ROLE] }

    suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val resp = api.register(RegisterRequest(name, email, password))
            saveSession(resp.token, resp.user.id, resp.user.name, resp.user.email, resp.user.role)
            Result.success(User(resp.user.id, resp.user.name, resp.user.email, resp.user.role))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val resp = api.login(LoginRequest(email, password))
            saveSession(resp.token, resp.user.id, resp.user.name, resp.user.email, resp.user.role)
            Result.success(User(resp.user.id, resp.user.name, resp.user.email, resp.user.role))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun logout() {
        dataStore.edit { it.clear() }
    }

    private suspend fun saveSession(
        token: String, id: Int, name: String, email: String, role: String
    ) {
        dataStore.edit {
            it[KEY_TOKEN]      = token
            it[KEY_USER_ID]    = id
            it[KEY_USER_NAME]  = name
            it[KEY_USER_EMAIL] = email
            it[KEY_USER_ROLE]  = role
        }
    }
}
