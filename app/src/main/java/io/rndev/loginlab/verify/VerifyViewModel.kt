package io.rndev.loginlab.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rndev.loginlab.Result
import io.rndev.loginlab.UiEvent
import io.rndev.loginlab.data.AuthRepository
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
class VerifyViewModel @Inject constructor(
    private val authRepository: AuthRepository,
//    @VerificationId private val verificationId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _eventChannel = Channel<UiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onVerifyPhoneNumberWithCode(verificationId: String, otpCode: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)

        authRepository.credentialSingIn(credential).collectLatest { result ->
            when (result) {
                is Result.Success -> if (result.data) {
                    _eventChannel.send(UiEvent.NavigateToHome)
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _eventChannel.send(
                        UiEvent.ShowError(
                            result.exception.localizedMessage ?: "Error desconocido"
                        )
                    )
                }
                is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
            }
        }
    }
}

