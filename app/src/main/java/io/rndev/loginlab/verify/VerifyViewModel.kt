package io.rndev.loginlab.verify

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import javax.inject.Inject

@HiltViewModel
class VerifyViewModel @Inject constructor(
    private val authRepository: AuthRepository,
//    @VerificationId private val verificationId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun onVerifyPhoneNumberWithCode(verificationId: String, otpCode: String) = viewModelScope.launch {

        Log.d("OnPhoneSignIn", "onVerifyPhoneNumberWithCode: verificationId $verificationId")

        val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)

        authRepository.credentialSingIn(credential).collectLatest { result ->
            _uiState.update {
                when (result) {
                    is Result.Success -> it.copy(isLoggedIn = result.data)
                    is Result.Error -> it.copy(error = result.exception.localizedMessage)
                    is Result.Loading -> it.copy(isLoading = true)
                }
            }
        }
    }

    fun onClearError() {
        _uiState.update { it.copy(error = null) }
    }
}

