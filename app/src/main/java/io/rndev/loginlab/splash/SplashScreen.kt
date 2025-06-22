package io.rndev.loginlab.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import io.rndev.loginlab.R
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

    val state = vm.uiState.collectAsState()
    val isLoggedIn = state.value.isLoggedIn

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == true) {
            onHome()
        } else if (isLoggedIn == false) {
            onLogin()
        }
    }
    if (state.value.error != null) {
        ErrorContent(
            message = state.value.error ?: stringResource(R.string.app_text_unknown_error),
            onRetry = onRetry
        )
    }
}