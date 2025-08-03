package io.rndev.loginlab.feature.auth.verify

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rndev.loginlab.AuthRepository
import io.rndev.loginlab.PhoneAuthEvent
import io.rndev.loginlab.Result
import io.rndev.loginlab.feature.auth.UiEvent
import io.rndev.loginlab.feature.auth.UiEvent.NavigateToHome
import io.rndev.loginlab.feature.auth.UiEvent.ShowError
import io.rndev.loginlab.feature.core.UiState
import io.rndev.loginlab.feature.core.Verify
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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

    private val _timerSeconds = MutableStateFlow(60)
    val timerSeconds: StateFlow<Int> = _timerSeconds

    private val _eventChannel = Channel<UiEvent>()
    val events = _eventChannel.receiveAsFlow()

    var verificationId = navKey.verificationId

    fun onVerifyPhoneNumberWithCode(otpCode: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        authRepository.phoneWithOtpSignIn(verificationId, otpCode).collectLatest { result ->
            when (result) {
                is Result.Success -> _eventChannel.send(NavigateToHome)
                is Result.Error -> onShowError(result.exception.localizedMessage)
            }
        }
    }

    fun handlePhoneSignIn(activity: Activity) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.phoneSingIn(navKey.phoneNumber, activity) // Pasar firebaseAuth aquí
                .collectLatest { event ->
                    when (event) {
                        is PhoneAuthEvent.VerificationCompleted -> {
                            event.result.collectLatest { result ->
                                when (result) {
                                    is Result.Success -> _eventChannel.send(NavigateToHome)
                                    is Result.Error -> onShowError(result.exception.localizedMessage)
                                }
                            }
                        }

                        is PhoneAuthEvent.VerificationFailed -> onShowError(event.error.localizedMessage)
                        is PhoneAuthEvent.CodeSent -> {
                            _uiState.update { it.copy(isLoading = false) }
                            verificationId = event.verificationId
                            resetTimer()
                        }
                    }
                }
        }
    }

    fun resetTimer() {
        _timerSeconds.value = 60
        startTimer()
    }

    fun startTimer() {
        viewModelScope.launch {
            while (_timerSeconds.value > 0) {
                delay(1000)
                _timerSeconds.value = _timerSeconds.value - 1
            }
        }
    }

    suspend fun onShowError(error: String?) {
        _uiState.update { it.copy(isLoading = false) }
        _eventChannel.send(ShowError(error ?: "Unknown error"))
    }
}
