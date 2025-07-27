package io.rndev.loginlab.feature.auth.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rndev.loginlab.AuthRepository
import io.rndev.loginlab.Result
import io.rndev.loginlab.feature.auth.UiEvent
import io.rndev.loginlab.feature.core.UiState
import io.rndev.loginlab.feature.core.Verify
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = VerifyViewModel.Factory::class)
class VerifyViewModel @AssistedInject constructor(
    private val authRepository: AuthRepository,
    @Assisted val navKey: Verify
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(navKey: Verify): VerifyViewModel
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _eventChannel = Channel<UiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onVerifyPhoneNumberWithCode(otpCode: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        authRepository.phoneWithOtpSignIn(navKey.verificationId, otpCode).collectLatest { result ->
            when (result) {
                is Result.Success -> _eventChannel.send(UiEvent.NavigateToHome)
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
