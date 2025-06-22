package io.rndev.loginlab.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rndev.loginlab.Result
import io.rndev.loginlab.data.AuthRepository
import io.rndev.loginlab.data.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    val uiState: StateFlow<UiState> = combine(
        authRepository.isAuthenticated(),
        authRepository.currentUser()
    ) { authResult, userResult ->

        when {
            authResult is Result.Error -> UiState(
                isLoggedIn = false,
                error = authResult.exception.message
            )

            userResult is Result.Error -> UiState(
                isLoggedIn = false,
                error = userResult.exception.message
            )

            authResult is Result.Success && userResult is Result.Success -> UiState(
                isLoggedIn = authResult.data,
                user = userResult.data
            )

            else -> UiState() // Loading o estado inicial
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState()
    )


    fun onSignOut() = viewModelScope.launch {
        authRepository.signOut()
    }
}

