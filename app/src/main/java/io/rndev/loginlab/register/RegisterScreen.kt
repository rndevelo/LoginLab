package io.rndev.loginlab.register

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import io.rndev.loginlab.R
import io.rndev.loginlab.composables.LoadingAnimation
import io.rndev.loginlab.login.composables.EmailOptionContent
import io.rndev.loginlab.login.composables.PasswordTextField
import kotlinx.serialization.Serializable

@Serializable
data object Register : NavKey

@Composable
fun RegisterScreen(vm: RegisterViewModel = hiltViewModel(), onHome: () -> Unit, onBack: () -> Unit) {

    val state = vm.uiState.collectAsState()
    val isLoggedIn = state.value.isLoggedIn
    var confirmPassword by remember { mutableStateOf("") }
    val snackBarHostState = remember { SnackbarHostState() }
    val unknownError = stringResource(R.string.app_text_unknown_error)

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == true) {
            onHome()
        }
    }

    LaunchedEffect(state.value.error) {
        if (state.value.error != null) {
            snackBarHostState.showSnackbar(state.value.error ?: unknownError)
            vm.onClearError()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .animateContentSize()
                .verticalScroll(rememberScrollState()) // 👈 permite hacer scroll si el teclado tapa
                .imePadding()                          // 👈 añade padding automático cuando aparece el teclado
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            EmailOptionContent(
                title = stringResource(R.string.login_text_sign_up_with_email),
                textButton = stringResource(R.string.login_text_sign_up),
                error = null,
                onBack = onBack,
                onSign = vm::onSignUp,
                passwordTextField = {

                    PasswordTextField(
                        value = confirmPassword,
                        error = null,
                        imeAction = ImeAction.Next,
                        onValueChange = { confirmPassword = it },
                    )

                    Spacer(Modifier.height(12.dp))

                }
            )
        }
        if (state.value.isLoading == true) LoadingAnimation()
    }
}