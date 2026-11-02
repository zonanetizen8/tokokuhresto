package com.example.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterOwnerUiState(
    val fullName: String = "",
    val username: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val pin: String = "",
    val confirmPin: String = "",
    val securityQuestion: String = "Nama brand restoran pertama Anda?",
    val securityAnswer: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

data class ForgotPinUiState(
    val username: String = "",
    val securityAnswer: String = "",
    val newPin: String = "",
    val confirmNewPin: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class RegistrationViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow(RegisterOwnerUiState())
    val registerState: StateFlow<RegisterOwnerUiState> = _registerState.asStateFlow()

    private val _forgotPinState = MutableStateFlow(ForgotPinUiState())
    val forgotPinState: StateFlow<ForgotPinUiState> = _forgotPinState.asStateFlow()

    fun updateRegisterForm(
        fullName: String? = null,
        username: String? = null,
        phone: String? = null,
        password: String? = null,
        confirmPassword: String? = null,
        pin: String? = null,
        confirmPin: String? = null,
        securityQuestion: String? = null,
        securityAnswer: String? = null
    ) {
        _registerState.value = _registerState.value.copy(
            fullName = fullName ?: _registerState.value.fullName,
            username = username ?: _registerState.value.username,
            phone = phone ?: _registerState.value.phone,
            password = password ?: _registerState.value.password,
            confirmPassword = confirmPassword ?: _registerState.value.confirmPassword,
            pin = pin ?: _registerState.value.pin,
            confirmPin = confirmPin ?: _registerState.value.confirmPin,
            securityQuestion = securityQuestion ?: _registerState.value.securityQuestion,
            securityAnswer = securityAnswer ?: _registerState.value.securityAnswer,
            errorMessage = null
        )
    }

    fun registerOwner() {
        val state = _registerState.value
        if (state.fullName.isBlank() || state.username.isBlank() || state.password.isBlank() || state.pin.isBlank() || state.securityAnswer.isBlank()) {
            _registerState.value = state.copy(errorMessage = "Mohon lengkapi semua kolom wajib.")
            return
        }
        if (state.password.length < 6) {
            _registerState.value = state.copy(errorMessage = "Password minimal 6 karakter.")
            return
        }
        if (state.password != state.confirmPassword) {
            _registerState.value = state.copy(errorMessage = "Konfirmasi password tidak cocok.")
            return
        }
        if (state.pin.length != 6 || !state.pin.all { it.isDigit() }) {
            _registerState.value = state.copy(errorMessage = "PIN harus berupa 6 angka.")
            return
        }
        if (state.pin != state.confirmPin) {
            _registerState.value = state.copy(errorMessage = "Konfirmasi PIN tidak cocok.")
            return
        }

        viewModelScope.launch {
            _registerState.value = state.copy(isLoading = true, errorMessage = null)
            val result = authRepository.registerOwner(
                username = state.username,
                fullName = state.fullName,
                password = state.password,
                pin = state.pin,
                securityQuestion = state.securityQuestion,
                securityAnswer = state.securityAnswer,
                phone = state.phone
            )
            result.onSuccess {
                _registerState.value = _registerState.value.copy(
                    isLoading = false,
                    isSuccess = true
                )
            }.onFailure { err ->
                _registerState.value = _registerState.value.copy(
                    isLoading = false,
                    errorMessage = err.message ?: "Registrasi gagal."
                )
            }
        }
    }

    fun updateForgotPinForm(
        username: String? = null,
        securityAnswer: String? = null,
        newPin: String? = null,
        confirmNewPin: String? = null
    ) {
        _forgotPinState.value = _forgotPinState.value.copy(
            username = username ?: _forgotPinState.value.username,
            securityAnswer = securityAnswer ?: _forgotPinState.value.securityAnswer,
            newPin = newPin ?: _forgotPinState.value.newPin,
            confirmNewPin = confirmNewPin ?: _forgotPinState.value.confirmNewPin,
            errorMessage = null
        )
    }

    fun resetPin() {
        val state = _forgotPinState.value
        if (state.username.isBlank() || state.securityAnswer.isBlank() || state.newPin.isBlank()) {
            _forgotPinState.value = state.copy(errorMessage = "Mohon lengkapi semua data.")
            return
        }
        if (state.newPin.length != 6 || !state.newPin.all { it.isDigit() }) {
            _forgotPinState.value = state.copy(errorMessage = "PIN baru harus berupa 6 angka.")
            return
        }
        if (state.newPin != state.confirmNewPin) {
            _forgotPinState.value = state.copy(errorMessage = "Konfirmasi PIN baru tidak cocok.")
            return
        }

        viewModelScope.launch {
            _forgotPinState.value = state.copy(isLoading = true, errorMessage = null)
            val result = authRepository.resetPinWithSecurityAnswer(
                username = state.username,
                securityAnswer = state.securityAnswer,
                newPin = state.newPin
            )
            result.onSuccess {
                _forgotPinState.value = _forgotPinState.value.copy(
                    isLoading = false,
                    isSuccess = true
                )
            }.onFailure { err ->
                _forgotPinState.value = _forgotPinState.value.copy(
                    isLoading = false,
                    errorMessage = err.message ?: "Gagal mereset PIN."
                )
            }
        }
    }

    class Factory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RegistrationViewModel(authRepository) as T
        }
    }
}
