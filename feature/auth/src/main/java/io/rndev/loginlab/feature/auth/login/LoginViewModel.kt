package io.rndev.loginlab.feature.auth.login

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rndev.loginlab.AuthRepository
import io.rndev.loginlab.PhoneAuthEvent
import io.rndev.loginlab.Result
import io.rndev.loginlab.feature.auth.InputError
import io.rndev.loginlab.feature.auth.UiEvent
import io.rndev.loginlab.feature.auth.UiEvent.NavigateToHome
import io.rndev.loginlab.feature.auth.UiEvent.NavigateToVerification
import io.rndev.loginlab.feature.auth.UiEvent.ShowError
import io.rndev.loginlab.feature.core.LoginFormType
import io.rndev.loginlab.feature.core.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginAction {
    data object OnEmailSignIn : LoginAction
    data class OnPhoneSignIn(val phoneNumber: String, val activity: Activity) : LoginAction
    data class OnGoogleSignIn(val context: Context) : LoginAction
    data object OnFbSignIn : LoginAction
    data object OnResetPassword : LoginAction
    data class OnEmailChanged(val email: String) : LoginAction
    data class OnPasswordChanged(val password: String) : LoginAction
    data class OnLoginFormTypeChanged(val formType: LoginFormType?) : LoginAction
}

@HiltViewModel
class LoginViewModel @Inject constructor(internal val authRepository: AuthRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<UiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnEmailSignIn -> handleEmailSignIn()
            is LoginAction.OnPhoneSignIn -> handlePhoneSignIn(action.phoneNumber, action.activity)
            is LoginAction.OnGoogleSignIn -> handleGoogleSignIn(action.context)
            is LoginAction.OnFbSignIn -> handleFacebookCallback()
            is LoginAction.OnResetPassword -> handleResetPassword()
            is LoginAction.OnEmailChanged -> _uiState.update {
                val newState = it.copy(email = action.email)
                newState.copy(emailError = InputError.InvalidEmail.validate(newState))
            }

            is LoginAction.OnPasswordChanged -> _uiState.update {
                val newState = it.copy(password = action.password)
                newState.copy(passwordError = InputError.PasswordTooShort.validate(newState))
            }

            is LoginAction.OnLoginFormTypeChanged -> _uiState.update {
                it.copy(loginFormType = action.formType)
            }
        }
    }

    private fun handleEmailSignIn() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.emailSignIn(_uiState.value.email, _uiState.value.password)
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> onUiEvent()
                        is Result.Error -> onUiEvent(
                            ShowError(
                                result.exception.localizedMessage ?: "Unknown error"
                            )
                        )
                    }
                }
        }
    }

    private fun handlePhoneSignIn(phoneNumber: String, activity: Activity) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.phoneSignIn(phoneNumber, activity) // Pasar firebaseAuth aquí
                .collectLatest { event ->
                    when (event) {
                        is PhoneAuthEvent.VerificationCompleted -> {
                            event.result.collectLatest { result ->
                                when (result) {
                                    is Result.Success -> onUiEvent()
                                    is Result.Error -> onUiEvent(
                                        ShowError(
                                            result.exception.localizedMessage ?: "Unknown error"
                                        )
                                    )
                                }
                            }
                        }

                        is PhoneAuthEvent.VerificationFailed -> onUiEvent(
                            ShowError(
                                event.error.localizedMessage ?: "Unknown error"
                            )
                        )

                        is PhoneAuthEvent.CodeSent -> onUiEvent(
                            NavigateToVerification(event.verificationId, phoneNumber)
                        )
                    }
                }
        }
    }

    private fun handleGoogleSignIn(context: Context) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.googleSignIn(context).collectLatest { result ->
                when (result) {
                    is Result.Success -> onUiEvent()
                    is Result.Error -> onUiEvent(
                        ShowError(
                            result.exception.localizedMessage ?: "Unknown error"
                        )
                    )
                }
            }
        }
    }

    private fun handleFacebookCallback() = viewModelScope.launch {
        authRepository.facebookSignIn().collectLatest { result ->
            when (result) {
                is Result.Success -> onUiEvent()
                is Result.Error -> onUiEvent(
                    ShowError(result.exception.localizedMessage ?: "Unknown error")
                )
            }
        }
    }

    private fun handleResetPassword() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.resetPassword(uiState.value.email).collectLatest { result ->
                when (result) {
                    is Result.Success -> onUiEvent(ShowError("Email de recuperación enviado"))
                    is Result.Error -> onUiEvent(
                        ShowError(
                            result.exception.localizedMessage ?: "Unknown error"
                        )
                    )
                }
            }
        }
    }

    private suspend fun onUiEvent(event: UiEvent = NavigateToHome) {
        _uiState.update { it.copy(isLoading = false) }
        _eventChannel.send(event)
    }
}

