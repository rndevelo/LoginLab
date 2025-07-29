package io.rndev.loginlab.feature.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rndev.loginlab.AuthRepository
import io.rndev.loginlab.Result
import io.rndev.loginlab.feature.auth.UiEvent
import io.rndev.loginlab.feature.auth.onValidateInputs
import io.rndev.loginlab.feature.core.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    // Eventos únicos (navegación, error, diálogos)
    private val _eventChannel = Channel<UiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onValidate() {
        onValidateInputs(_uiState)
    }

    private fun onSignUp() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            authRepository.emailSignUp(uiState.value.email, uiState.value.password)
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> onEmailVerified()
                        is Result.Error -> {
                            _uiState.update { it.copy(isLoading = false) }
                            _eventChannel.send(
                                UiEvent.ShowError(
                                    result.exception.localizedMessage ?: "Error desconocido"
                                )
                            )
                        }
                    }
                }
        }
    }

    private suspend fun onEmailVerified() {
        _uiState.update { it.copy(isLoading = false, isEmailSent = true) }
        authRepository.isEmailVerified().collectLatest { isEmailVerified ->
            if (isEmailVerified) {
                _uiState.update { it.copy(isEmailVerified = true) }
            }
        }
    }

    fun onNavigateToHomeEvent() = viewModelScope.launch {
        _eventChannel.send(UiEvent.NavigateToHome)
    }

    fun onEmailValueChange(value: String) {
        onValidateInputs(_uiState)
        _uiState.update {
            it.copy(
                email = value,
                localError = false
            )
        }
    }

    fun onPasswordValueChange(value: String) {
        onValidateInputs(_uiState)
        _uiState.update {
            it.copy(
                password = value,
                localError = false
            )
        }
    }

    fun onConfirmPasswordValueChange(value: String) {
        onValidateInputs(_uiState)
        _uiState.update {
            it.copy(
                confirmPassword = value,
                localError = false
            )
        }
    }
}





