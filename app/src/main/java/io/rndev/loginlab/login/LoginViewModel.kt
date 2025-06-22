package io.rndev.loginlab.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rndev.loginlab.Result
import io.rndev.loginlab.data.AuthRepository
import io.rndev.loginlab.data.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun onSignIn(user: String, password: String) = viewModelScope.launch {
        _uiState.value = UiState(isLoading = true)

        viewModelScope.launch {
            authRepository.signIn(user, password).collectLatest { result ->
                _uiState.value = when (result) {
                    is Result.Success -> UiState(isLoggedIn = result.data)
                    is Result.Error -> UiState(error = result.exception.message)
                    is Result.Loading -> UiState(isLoading = true)
                }
            }
        }
    }

    fun onPhoneSignIn() = viewModelScope.launch {

    }

    fun onGoogleSignIn() = viewModelScope.launch {

    }

    fun onFacebookSignIn() = viewModelScope.launch {

    }

    fun onClearError() {
        _uiState.value = UiState(error = null)
    }
}
