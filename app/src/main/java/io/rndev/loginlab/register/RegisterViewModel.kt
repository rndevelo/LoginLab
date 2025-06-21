package io.rndev.loginlab.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.rndev.loginlab.data.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    data class UiState(
        val loggedIn: Boolean = false,
        val error: String? = null,
    )


    fun onSignUp(user: String, password: String) = viewModelScope.launch{
        authRepository.signUp(user, password)
    }
}
