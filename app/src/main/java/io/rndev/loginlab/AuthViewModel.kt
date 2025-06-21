package io.rndev.loginlab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.rndev.loginlab.data.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val isAuthenticated: StateFlow<Result<Boolean>> = authRepository.isAuthenticated()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Result.Loading
        )
}