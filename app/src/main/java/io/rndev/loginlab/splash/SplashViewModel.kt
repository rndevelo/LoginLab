package io.rndev.loginlab.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rndev.loginlab.Result
import io.rndev.loginlab.UiEvent
import io.rndev.loginlab.data.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(authRepository: AuthRepository) : ViewModel() {

    private val _eventChannel = Channel<UiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            authRepository.isAuthenticated().collect { result ->
                when (result) {
                    is Result.Success -> {
                        if (result.data) {
                            _eventChannel.send(UiEvent.NavigateToHome)
                        } else {
                            _eventChannel.send(UiEvent.NavigateToLogin)
                        }
                    }

                    is Result.Error -> {
                        _eventChannel.send(UiEvent.ShowError(result.exception.message ?: "Error desconocido"))
                    }

                    is Result.Loading -> {}
                }
            }
        }
    }
}
