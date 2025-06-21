package io.rndev.loginlab.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.rndev.loginlab.data.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import io.rndev.loginlab.Result

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    data class UiState(
        val loggedIn: Boolean = false,
        val error: String? = null,
    )


    fun onSignIn(user: String, password: String) = viewModelScope.launch{
        authRepository.signIn(user, password).collect { result ->

        }
    }

}