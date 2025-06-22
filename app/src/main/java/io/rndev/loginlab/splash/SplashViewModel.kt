package io.rndev.loginlab.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rndev.loginlab.Result
import io.rndev.loginlab.data.AuthRepository
import io.rndev.loginlab.data.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(authRepository: AuthRepository) : ViewModel() {

    val uiState: StateFlow<UiState> = authRepository.isAuthenticated()
        .map { result ->

            when (result) {
                is Result.Success -> UiState(isLoggedIn = result.data)
                is Result.Error -> UiState(error = result.exception.message)
                is Result.Loading -> UiState()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = UiState()
        )
}