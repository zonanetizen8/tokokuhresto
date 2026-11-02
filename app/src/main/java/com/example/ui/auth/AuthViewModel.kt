package com.example.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.UserEntity
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LoginMode {
    PIN,
    PASSWORD
}

data class LoginUiState(
    val loginMode: LoginMode = LoginMode.PIN,
    val usernameInput: String = "",
    val passwordInput: String = "",
    val pinInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedInUser: UserEntity? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser: StateFlow<UserEntity?> = authRepository.currentUser

    val allUsers: StateFlow<List<UserEntity>> = authRepository.getAllUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun switchLoginMode(mode: LoginMode) {
        _uiState.value = _uiState.value.copy(
            loginMode = mode,
            errorMessage = null,
            pinInput = ""
        )
    }

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(usernameInput = username, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(passwordInput = password, errorMessage = null)
    }

    fun onPinDigit(digit: String) {
        val currentPin = _uiState.value.pinInput
        if (currentPin.length < 6) {
            val updated = currentPin + digit
            _uiState.value = _uiState.value.copy(pinInput = updated, errorMessage = null)
            if (updated.length == 6) {
                loginWithPin(updated)
            }
        }
    }

    fun onPinBackspace() {
        val currentPin = _uiState.value.pinInput
        if (currentPin.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                pinInput = currentPin.dropLast(1),
                errorMessage = null
            )
        }
    }

    fun clearPin() {
        _uiState.value = _uiState.value.copy(pinInput = "", errorMessage = null)
    }

    fun loginWithPin(pin: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.loginWithPin(pin)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loggedInUser = user,
                    pinInput = "",
                    errorMessage = null
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    pinInput = "",
                    errorMessage = err.message ?: "PIN salah. Silakan coba lagi."
                )
            }
        }
    }

    fun loginWithPassword() {
        val state = _uiState.value
        if (state.usernameInput.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Username tidak boleh kosong.")
            return
        }
        if (state.passwordInput.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Password tidak boleh kosong.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.loginWithPassword(state.usernameInput, state.passwordInput)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loggedInUser = user,
                    passwordInput = "",
                    errorMessage = null
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err.message ?: "Login gagal."
                )
            }
        }
    }

    fun selectUserToLogin(user: UserEntity) {
        _uiState.value = _uiState.value.copy(
            usernameInput = user.username,
            loginMode = LoginMode.PIN,
            pinInput = "",
            errorMessage = null
        )
    }

    fun quickDirectLogin(user: UserEntity) {
        authRepository.setCurrentUser(user)
        _uiState.value = _uiState.value.copy(loggedInUser = user)
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = LoginUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    class Factory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(authRepository) as T
        }
    }
}
