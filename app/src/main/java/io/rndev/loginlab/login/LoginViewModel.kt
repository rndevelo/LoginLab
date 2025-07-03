package io.rndev.loginlab.login

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import io.rndev.loginlab.data.AuthRepository
import io.rndev.loginlab.data.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed interface LoginAction {
    data class OnEmailSignIn(val user: String, val password: String) : LoginAction
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

    var verificationId by mutableStateOf<String?>(null)

    fun onAction(action: LoginAction) = viewModelScope.launch {
        when (action) {
            is LoginAction.OnEmailSignIn -> onSignIn(action.user, action.password)
            is LoginAction.OnPhoneSignIn -> onPhoneSignIn(action.phoneNumber, action.activity)
            is LoginAction.OnGoogleSignIn -> onGoogleSignIn(action.context)
            is LoginAction.OnFacebookSignIn -> onFacebookSignIn()
        }
    }

    fun onSignIn(user: String, password: String) = viewModelScope.launch {

        viewModelScope.launch {
            authRepository.signIn(user, password).collectLatest { result ->
                _uiState.update {
                    when (result) {
                        is Result.Success -> it.copy(isLoggedIn = result.data)
                        is Result.Error -> it.copy(error = result.exception.localizedMessage)
                        is Result.Loading -> it.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun onPhoneSignIn(phoneNumber: String, activity: Activity) = viewModelScope.launch {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Autoverificación en algunos dispositivos
                    authRepository.phoneSingIn(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _uiState.update {
                        it.copy(error = e.localizedMessage)
                    }
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@LoginViewModel.verificationId = verificationId
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyPhoneNumberWithCode(code: String) {
        val storedId = verificationId ?: return
        val credential = PhoneAuthProvider.getCredential(storedId, code)
        authRepository.phoneSingIn(credential)
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
            _uiState.update { it.copy(error = e.localizedMessage) }
        }
    }

    private suspend fun tryCatchGoogleCredential(credential: Credential) {

        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential =
                    GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                authRepository.googleSingIn(authCredential)
                    .collectLatest { result ->
                        _uiState.update {
                            when (result) {
                                is Result.Success -> it.copy(isLoggedIn = result.data)
                                is Result.Error -> it.copy(error = result.exception.localizedMessage)
                                is Result.Loading -> it.copy(isLoading = true)
                            }
                        }
                    }
            } catch (e: GoogleIdTokenParsingException) {
                _uiState.update {
                    it.copy(error = e.message ?: "Received an invalid google id token response")
                }
            }
        } else {
            _uiState.update { it.copy(error = "Credential is not of type Google ID!") }
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
                        authRepository.facebookSingIn(authCredential)
                            .collectLatest { result ->
                                _uiState.update {
                                    when (result) {
                                        is Result.Success -> it.copy(isLoggedIn = result.data)
                                        is Result.Error -> it.copy(error = result.exception.localizedMessage)
                                        is Result.Loading -> it.copy(isLoading = true)
                                    }
                                }
                            }
                    }
                }

                override fun onCancel() {
                    _uiState.update { it.copy(error = "On Cancel") }
                }

                override fun onError(error: FacebookException) {
                    _uiState.update {
                        it.copy(error = error.localizedMessage ?: "Facebook login error")
                    }
                }
            }
        )

    }

    fun onClearError() {
        _uiState.update { it.copy(error = null) }
    }
}



