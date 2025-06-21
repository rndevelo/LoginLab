package io.rndev.loginlab.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.rndev.loginlab.data.AuthRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun onSignOut() = viewModelScope.launch {
        authRepository.signOut()
    }
}