package io.rndev.loginlab.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.login.LoginManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rndev.loginlab.Result
import io.rndev.loginlab.domain.AuthRepository
import io.rndev.loginlab.BaseUiState
import io.rndev.loginlab.domain.User
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val loginManager: LoginManager
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        authRepository.isAuthenticated(),
        authRepository.currentUser()
    ) { authResult, userResult ->

        when {
            authResult is Result.Error -> HomeUiState().copy(
                isLoggedIn = false,
                errorMessage = authResult.exception.message
            )

            userResult is Result.Error -> HomeUiState().copy(
                isLoggedIn = false,
                errorMessage = userResult.exception.message
            )

            authResult is Result.Success && userResult is Result.Success -> HomeUiState().copy(
                isLoggedIn = authResult.data,
                user = userResult.data
            )

            else -> HomeUiState()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )


    fun onSignOut() = viewModelScope.launch {
        loginManager.logOut()
        authRepository.signOut()
    }
}

data class HomeUiState(
    val isLoggedIn: Boolean?,
    val user: User?,
    override val isLoading: Boolean?,
    override val errorMessage: String?
) : BaseUiState {

    constructor() : this(
        isLoggedIn = null,
        user = null,
        isLoading = null,
        errorMessage = null
    )
}

