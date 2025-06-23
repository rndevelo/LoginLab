package io.rndev.loginlab.register

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
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun onSignUp(user: String, password: String) {
        _uiState.value = UiState(isLoading = true)

        viewModelScope.launch {
            authRepository.signUp(user, password).collectLatest { result ->
                _uiState.value = when (result) {
                    is Result.Success -> UiState(isLoggedIn = result.data)
                    is Result.Error -> UiState(error = result.exception.message)
                    is Result.Loading -> UiState(isLoading = true)
                }
            }
        }
    }

    fun onClearError() {
        _uiState.value = UiState(error = null)
    }
}

