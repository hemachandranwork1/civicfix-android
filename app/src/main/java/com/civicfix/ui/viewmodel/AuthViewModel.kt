package com.civicfix.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.civicfix.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    val token = repo.token.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val userName = repo.userName.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val userRole = repo.userRole.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val userId = repo.userId.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String) = viewModelScope.launch {
        _uiState.value = AuthUiState(isLoading = true)
        val result = repo.login(email, password)
        _uiState.value = if (result.isSuccess) AuthUiState(isSuccess = true)
        else AuthUiState(error = result.exceptionOrNull()?.message ?: "Login failed")
    }

    fun register(name: String, email: String, password: String) = viewModelScope.launch {
        _uiState.value = AuthUiState(isLoading = true)
        val result = repo.register(name, email, password)
        _uiState.value = if (result.isSuccess) AuthUiState(isSuccess = true)
        else AuthUiState(error = result.exceptionOrNull()?.message ?: "Registration failed")
    }

    fun logout() = viewModelScope.launch { repo.logout() }
    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
}
