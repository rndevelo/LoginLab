package io.rndev.loginlab.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import io.rndev.loginlab.R
import io.rndev.loginlab.UiEvent
import io.rndev.loginlab.home.ErrorContent
import kotlinx.serialization.Serializable

@Serializable
data object Splash : NavKey

@Composable
fun SplashScreen(
    vm: SplashViewModel = hiltViewModel(),
    onLogin: () -> Unit,
    onHome: () -> Unit,
    onRetry: () -> Unit
) {

    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                is UiEvent.NavigateToHome -> onHome()
                is UiEvent.NavigateToVerification -> TODO()
                is UiEvent.ShowError -> errorMessage = event.message
                is UiEvent.NavigateToLogin -> onLogin()
            }
        }
    }

    AnimatedVisibility(errorMessage != null) {
        ErrorContent(
            message = errorMessage ?: stringResource(R.string.app_text_unknown_error),
            onRetry = onRetry
        )
    }
}