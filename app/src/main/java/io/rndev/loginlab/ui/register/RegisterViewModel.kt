package io.rndev.loginlab.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rndev.loginlab.domain.Result
import io.rndev.loginlab.utils.UiEvent
import io.rndev.loginlab.domain.AuthRepository
import io.rndev.loginlab.utils.UiState
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

    fun onValidateInputs() {
        io.rndev.loginlab.utils.onValidateInputs(_uiState) { onSignUp() }
    }

    private fun onSignUp() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            authRepository.emailSignUp(uiState.value.email, uiState.value.password).let { result ->
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
        _uiState.update {
            it.copy(
                email = value,
                localError = false
            )
        }
    }

    fun onPasswordValueChange(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                localError = false
            )
        }
    }

    fun onConfirmPasswordValueChange(value: String) {
        _uiState.update {
            it.copy(
                confirmPassword = value,
                localError = false
            )
        }
    }
}





