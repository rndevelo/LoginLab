package io.rndev.loginlab.feature.auth.login

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import io.rndev.loginlab.feature.auth.UiEvent
import io.rndev.loginlab.feature.auth.login.composables.CreateAccountContent
import io.rndev.loginlab.feature.auth.login.composables.DropDownMenu
import io.rndev.loginlab.feature.auth.login.composables.ForgotYourPasswordContent
import io.rndev.loginlab.feature.auth.login.composables.LoginHeaderContent
import io.rndev.loginlab.feature.auth.login.composables.LoginOptionsContent
import io.rndev.loginlab.feature.core.Home
import io.rndev.loginlab.feature.core.LoginFormType
import io.rndev.loginlab.feature.core.R
import io.rndev.loginlab.feature.core.Register
import io.rndev.loginlab.feature.core.Verify
import io.rndev.loginlab.feature.core.composables.EmailOptionContent
import io.rndev.loginlab.feature.core.composables.PasswordTextField
import io.rndev.loginlab.feature.core.composables.PhoneOptionContent

@Composable
fun LoginScreen(
    onNavigate: (NavKey) -> Unit,
    vm: LoginViewModel = hiltViewModel(),
) {

    val state by vm.uiState.collectAsState()
    val errorMessage = state.errorMessage
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                is UiEvent.NavigateToHome -> onNavigate(Home)
                is UiEvent.NavigateToVerification -> onNavigate(Verify(verificationId = event.verificationId))
                is UiEvent.ShowError -> snackBarHostState.showSnackbar(event.message)
                is UiEvent.NavigateToLogin -> TODO()
            }
        }
    }

    val facebookLoginLauncher = rememberLauncherForActivityResult(
        contract = vm.loginManager.createLogInActivityResultContract(vm.callbackManager, null),
        onResult = {
            // El resultado se maneja en el callback registrado en el ViewModel
            // a través del vm.callbackManager
        }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()                          // 👈 añade padding automático cuando aparece el teclado
            .navigationBarsPadding(),
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LoginContent(
                isLoading = state.isLoading == true,
                email = state.email,
                password = state.password,
                emailError = state.emailError,
                passwordError = state.passwordError,
                localError = state.localError,
                errorMessage = errorMessage,
                loginFormType = state.loginFormType,
                onRegister = { onNavigate(Register) },
                onAction = vm::onAction,
                onFbActivityResult = {
                    facebookLoginLauncher.launch(listOf("email", "public_profile"))
                },
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun LoginContent(
    isLoading: Boolean,
    email: String,
    password: String,
    emailError: String?,
    passwordError: String?,
    localError: Boolean,
    errorMessage: String?,
    loginFormType: LoginFormType?,
    onRegister: () -> Unit,
    onAction: (LoginAction) -> Unit,
    onFbActivityResult: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    BackHandler(enabled = loginFormType != null) {
        onAction(LoginAction.OnLoginFormTypeChanged(null))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .animateContentSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState()), // 👈 permite hacer scroll si el teclado tapa
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AnimatedVisibility(visible = loginFormType == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LoginHeaderContent()
                LoginOptionsContent(
                    isLoading = isLoading,
                    onShowForm = { onAction(LoginAction.OnLoginFormTypeChanged(it)) },
                    onGoogleSignIn = { onAction(LoginAction.OnGoogleSignIn(context)) },
                    onFacebookSignIn = onFbActivityResult
                )
            }
        }

        // Formulario de Email
        AnimatedVisibility(visible = loginFormType == LoginFormType.EMAIL) {

            EmailOptionContent(
                isLoading = isLoading,
                title = stringResource(R.string.login_text_sign_in_with_email),
                email = email,
                emailError = emailError,
                password = password,
                localError = localError,
                onEmailValueChange = { onAction(LoginAction.OnEmailChanged(it)) },
                textButton = stringResource(R.string.login_text_sign_in),
                onBack = { onAction(LoginAction.OnLoginFormTypeChanged(null)) },
                onClick = { onAction(LoginAction.OnEmailSignIn) },
                firstPasswordTextField = {
                    PasswordTextField(
                        value = password,
                        passwordError = passwordError,
                        localError = localError,
                        imeAction = ImeAction.Done,
                        keyboardActions = KeyboardActions { onAction(LoginAction.OnEmailSignIn) },
                        onValueChange = { onAction(LoginAction.OnPasswordChanged(it)) },
                    )
                },
                forgotYourPasswordText = {
                    ForgotYourPasswordContent {}
                },
                buttonContent = {
                    CreateAccountContent(
                        onRegister = onRegister,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                },
            )
        }


        var codeSelected by rememberSaveable { mutableStateOf("+34") }
        var phone by rememberSaveable { mutableStateOf("") }
        val fullPhone = codeSelected + phone.trim()
        val isValidPhone = fullPhone.matches(Regex("^\\+\\d{8,15}$"))

        // Formulario de Phone
        getActivity()?.let { activity ->
            // Usar activity, por ejemplo, para Firebase PhoneAuthProvider.verifyPhoneNumber
            AnimatedVisibility(visible = loginFormType == LoginFormType.PHONE) {

                PhoneOptionContent(
                    isLoading = isLoading,
                    title = stringResource(R.string.login_text_sign_in_with_phone),
                    label = stringResource(R.string.login_text_phone),
                    initialValue = phone,
                    textButton = stringResource(R.string.login_text_send_code),
                    isEnabled = isValidPhone,
                    error = errorMessage,
                    leadingIconContent = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = Icons.Default.Phone.toString()
                        )
                    },
                    dropDownContent = { DropDownMenu(onCodeSelected = { codeSelected = it }) },
                    onInitialValue = { phone = it },
                    onClick = { onAction(LoginAction.OnPhoneSignIn(fullPhone, activity)) },
                    onBack = { onAction(LoginAction.OnLoginFormTypeChanged(null)) }
                )
            }
        }
    }
}

@Composable
fun getActivity(): Activity? {
    val context = LocalContext.current
    return context.findActivity()
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
