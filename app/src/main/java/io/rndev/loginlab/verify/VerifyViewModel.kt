package io.rndev.loginlab.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rndev.loginlab.Result
import io.rndev.loginlab.UiEvent
import io.rndev.loginlab.domain.AuthRepository
import io.rndev.loginlab.utils.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

        val credential = PhoneAuthProvider.getCredential(navKey.verificationId, otpCode)

        when (val result = authRepository.credentialSingIn(credential)) {
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

