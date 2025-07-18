package io.rndev.loginlab.login

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rndev.loginlab.Result
import io.rndev.loginlab.UiEvent
import io.rndev.loginlab.data.datasources.PhoneAuthProcessEvent
import io.rndev.loginlab.domain.AuthRepository
import io.rndev.loginlab.domain.CredentialRepository
import io.rndev.loginlab.utils.LoginFormType
import io.rndev.loginlab.utils.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginAction {
    data object OnEmailSignIn : LoginAction
    data class OnPhoneSignIn(val phoneNumber: String, val activity: Activity) : LoginAction
    data class OnGoogleSignIn(val context: Context) : LoginAction
    data class OnEmailChanged(val email: String) : LoginAction
    data class OnPasswordChanged(val password: String) : LoginAction
    data class OnLoginFormTypeChanged(val formType: LoginFormType?) : LoginAction
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val credentialRepository: CredentialRepository,
    internal val callbackManager: CallbackManager,
    internal val loginManager: LoginManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _eventChannel = Channel<UiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        registerFacebookCallback()
    }

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnEmailSignIn -> handleEmailSignIn()
            is LoginAction.OnPhoneSignIn -> handlePhoneSignIn(action.phoneNumber, action.activity)
            is LoginAction.OnGoogleSignIn -> handleGoogleSignIn(action.context)
            is LoginAction.OnEmailChanged -> _uiState.update {
                it.copy(email = action.email, localError = false)
            }
            is LoginAction.OnPasswordChanged -> _uiState.update {
                it.copy(password = action.password, localError = false)
            }
            is LoginAction.OnLoginFormTypeChanged -> _uiState.update {
                it.copy(loginFormType = action.formType)
            }
        }
    }

    private fun handleEmailSignIn() {
        _uiState.update { it.copy(isLoading = true) }
        io.rndev.loginlab.utils.onValidateInputs(_uiState) {
            viewModelScope.launch {
                val result = authRepository.emailSignIn(uiState.value.email, uiState.value.password)
                when (result) {
                    is Result.Success -> _eventChannel.send(UiEvent.NavigateToHome)
                    is Result.Error -> onShowError(result.exception.localizedMessage)
                }
            }
        }
    }

    private fun handlePhoneSignIn(phoneNumber: String, activity: Activity) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            credentialRepository.getPhoneAuthProcessEvent(phoneNumber, activity, 60L) // Pasar firebaseAuth aquí
                .collectLatest { event ->
                    when (event) {
                        is PhoneAuthProcessEvent.CodeSent -> {
                            _uiState.update { it.copy(isLoading = false) }
                            _eventChannel.send(UiEvent.NavigateToVerification(event.verificationId))
                        }

                        is PhoneAuthProcessEvent.VerificationCompleted -> {
                            val result = authRepository.credentialSingIn(event.result)
                            when (result) {
                                is Result.Success -> _eventChannel.send(UiEvent.NavigateToHome)
                                is Result.Error -> onShowError(result.exception.localizedMessage)
                            }
                        }

                        is PhoneAuthProcessEvent.VerificationFailed -> {
                            _uiState.update { it.copy(isLoading = false) }
                            onShowError(event.error.localizedMessage)
                        }
                    }
                }
        }
    }

    private fun handleGoogleSignIn(context: Context) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val credential = credentialRepository.getGoogleCredential(context)
            when (credential) {
                is Result.Success -> {
                    when (val result = authRepository.credentialSingIn(credential.data)) {
                        is Result.Success -> _eventChannel.send(UiEvent.NavigateToHome)
                        is Result.Error -> onShowError(result.exception.localizedMessage)
                    }
                }
                is Result.Error -> onShowError(credential.exception.localizedMessage)
            }
        }
    }

    private fun registerFacebookCallback() {
        loginManager.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    viewModelScope.launch {
                        val token = loginResult.accessToken.token
                        val credential = credentialRepository.getFacebookCredential(token)
                        when (val result = authRepository.credentialSingIn(credential)) {
                            is Result.Success -> _eventChannel.send(UiEvent.NavigateToHome)
                            is Result.Error -> onShowError(result.exception.localizedMessage)
                        }
                    }
                }

                override fun onCancel() {
                    viewModelScope.launch { onShowError("Facebook login cancelled") }
                }

                override fun onError(error: FacebookException) {
                    viewModelScope.launch { onShowError(error.localizedMessage) }
                }
            }
        )
    }

    suspend fun onShowError(error: String?) {
        _uiState.update { it.copy(isLoading = false) }
        _eventChannel.send(
            UiEvent.ShowError(
                error ?: "Unknown error"
            )
        )
    }
}

