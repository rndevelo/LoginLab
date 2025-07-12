package io.rndev.loginlab.login

import android.app.Activity
import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed interface LoginAction {
    data object OnEmailSignIn : LoginAction
    data class OnPhoneSignIn(val phoneNumber: String, val activity: Activity) : LoginAction
    data class OnGoogleSignIn(val context: Context) : LoginAction
    data object OnFacebookSignIn : LoginAction
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firebaseAuth: FirebaseAuth,
    private val credentialManager: CredentialManager,
    private val credentialRequest: GetCredentialRequest,
    internal val callbackManager: CallbackManager,
    val loginManager: LoginManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _eventChannel = Channel<UiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onAction(action: LoginAction) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        when (action) {
            is LoginAction.OnEmailSignIn -> onValidateInputs()
            is LoginAction.OnPhoneSignIn -> onPhoneSignIn(action.phoneNumber, action.activity)
            is LoginAction.OnGoogleSignIn -> onGoogleSignIn(action.context)
            is LoginAction.OnFacebookSignIn -> onFacebookSignIn()
        }
    }

    fun onValidateInputs() {
        io.rndev.loginlab.utils.onValidateInputs(_uiState) { onEmailSignIn() }
    }

    fun onEmailSignIn() = viewModelScope.launch {
        viewModelScope.launch {
            authRepository.emailSignIn(uiState.value.email, uiState.value.password).let { result ->
                when (result) {
                    is Result.Success -> _eventChannel.send(UiEvent.NavigateToHome)
                    is Result.Error -> onShowError(result.exception.localizedMessage)
                    is Result.Loading -> {}
                }
            }
        }
    }

    fun onPhoneSignIn(phoneNumber: String, activity: Activity) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    viewModelScope.launch {
                        authRepository.credentialSingIn(credential).collectLatest { result ->
                            when (result) {
                                is Result.Success -> _eventChannel.send(UiEvent.NavigateToHome)
                                is Result.Error -> onShowError(result.exception.localizedMessage)
                                is Result.Loading -> {}
                            }
                        }
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    viewModelScope.launch {
                        onShowError(e.localizedMessage)
                    }
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    _uiState.update { it.copy(isLoading = false) }
                    viewModelScope.launch {
                        _eventChannel.send(UiEvent.NavigateToVerification(verificationId))
                    }
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun onGoogleSignIn(context: Context) = viewModelScope.launch {
        try {
            val credentialResponse = credentialManager.getCredential(
                request = credentialRequest,
                context = context,
            )
            val credential = credentialResponse.credential
            tryCatchGoogleCredential(credential)
        } catch (e: GetCredentialCancellationException) {
            onShowError(e.localizedMessage)
        }
    }

    private suspend fun tryCatchGoogleCredential(credential: Credential) {

        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

            try {

                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential =
                    GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                authRepository.credentialSingIn(authCredential)
                    .collectLatest { result ->
                        when (result) {
                            is Result.Success -> _eventChannel.send(UiEvent.NavigateToHome)
                            is Result.Error -> onShowError(result.exception.localizedMessage)
                            is Result.Loading -> {}
                        }
                    }
            } catch (e: GoogleIdTokenParsingException) {
                onShowError(e.localizedMessage)
            }
        } else {
            _eventChannel.send(UiEvent.ShowError("Credential is not of type Google ID!"))
        }
    }

    fun onFacebookSignIn() {

        loginManager.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {

                override fun onSuccess(loginResult: LoginResult) {

                    val token = loginResult.accessToken.token
                    val authCredential = FacebookAuthProvider.getCredential(token)

                    viewModelScope.launch {

                        authRepository.credentialSingIn(authCredential)
                            .collectLatest { result ->
                                when (result) {
                                    is Result.Success -> _eventChannel.send(UiEvent.NavigateToHome)
                                    is Result.Error -> onShowError(result.exception.localizedMessage)
                                    is Result.Loading -> {}
                                }
                            }
                    }
                }

                override fun onCancel() {
                    viewModelScope.launch { onShowError("OnCancel") }
                }

                override fun onError(error: FacebookException) {
                    viewModelScope.launch { onShowError(error.localizedMessage) }
                }
            }
        )

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

    suspend fun onShowError(error: String?) {
        _uiState.update { it.copy(isLoading = false) }
        _eventChannel.send(
            UiEvent.ShowError(
                error ?: "Unknown error"
            )
        )
    }
}

