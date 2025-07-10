package io.rndev.loginlab.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rndev.loginlab.Result
import io.rndev.loginlab.UiEvent
import io.rndev.loginlab.data.AuthRepository
import io.rndev.loginlab.data.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
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

    // Estado combinado para saber si puede navegar
    val canNavigateToHome: StateFlow<Boolean> = combine(
        authRepository.isAuthenticated(),
        authRepository.isEmailVerified()
    ) { authResult, isVerified ->
        (authResult as? Result.Success)?.data == true && isVerified
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun onSignUp(user: String, password: String) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            authRepository.emailSignUp(user, password).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { it.copy(isLoading = false, isEmailSent = result.data) }
                        if (result.data) {
                            authRepository.isEmailVerified().collectLatest { isEmailVerified ->
                                if (isEmailVerified) {
                                    _eventChannel.send(UiEvent.ShowEmailVerificationDialog)
                                }

                            }
                        }
                    }

                    is Result.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _eventChannel.send(UiEvent.ShowError(result.exception.message ?: "Error desconocido"))
                    }

                    is Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }
}

