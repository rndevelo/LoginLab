package io.rndev.loginlab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rndev.loginlab.domain.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(authRepository: AuthRepository) : ViewModel() {

    val isAuthenticated: StateFlow<Boolean?> = authRepository.isAuthenticated()
        .map { authResult ->
            when (authResult) {
                is Result.Success -> authResult.data // El booleano de si está autenticado
                is Result.Error -> false
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = null
        )
}