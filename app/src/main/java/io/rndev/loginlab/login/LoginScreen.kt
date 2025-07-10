package io.rndev.loginlab.login

import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import io.rndev.loginlab.R
import io.rndev.loginlab.UiEvent
import io.rndev.loginlab.composables.LoadingAnimation
import io.rndev.loginlab.login.composables.DropDownMenu
import io.rndev.loginlab.login.composables.EmailOptionContent
import io.rndev.loginlab.login.composables.ForgotYourPasswordText
import io.rndev.loginlab.login.composables.PhoneOptionContent
import io.rndev.loginlab.login.composables.RegisterButton
import kotlinx.serialization.Serializable

enum class LoginFormType { EMAIL, PHONE }

@Serializable
data object Login : NavKey

@Composable
fun LoginScreen(
    vm: LoginViewModel = hiltViewModel(),
    onRegister: () -> Unit,
    onVerify: (String) -> Unit,
    onHome: () -> Unit,
) {

    val state = vm.uiState.collectAsState()
    val error = state.value.error
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                is UiEvent.NavigateToHome -> onHome()
                is UiEvent.NavigateToVerification -> onVerify(event.verificationId)
                is UiEvent.ShowError -> snackBarHostState.showSnackbar(event.message)
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
        Box(modifier = Modifier.fillMaxSize()){
            LoginContent(
                error = error,
                onRegister = onRegister,
                onAction = vm::onAction,
                onFbActivityResult = {
                    facebookLoginLauncher.launch(listOf("email", "public_profile"))
                    vm.onAction(LoginAction.OnFacebookSignIn)
                },
                modifier = Modifier.padding(innerPadding)
            )
            if (state.value.isLoading == true) LoadingAnimation()
        }
    }
}

@Composable
private fun LoginContent(
    error: String?,
    onRegister: () -> Unit,
    onAction: (LoginAction) -> Unit,
    onFbActivityResult: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var showForm by remember { mutableStateOf<LoginFormType?>(null) }


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

        AnimatedVisibility(visible = showForm == null) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.Science,
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(4.dp))

                // Título
                Text(
                    text = stringResource(R.string.app_name), // O usa stringResource(R.string.app_name)
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.login_text_securely_simply_swiftly),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(Modifier.height(24.dp))
            }
        }

        // Tipos de inicio de sesión
        AnimatedVisibility(visible = showForm == null) {
            LoginOptionsContent(
                onShowEmailForm = { showForm = LoginFormType.EMAIL },
                onShowPhoneForm = { showForm = LoginFormType.PHONE },
                onGoogleSignIn = { onAction(LoginAction.OnGoogleSignIn(context)) },
                onFacebookSignIn = {
                    onAction(LoginAction.OnFacebookSignIn)
                    onFbActivityResult()
                }
            )
        }

        // Formulario de Email
        AnimatedVisibility(visible = showForm == LoginFormType.EMAIL) {
            EmailOptionContent(
                title = stringResource(R.string.login_text_sign_in_with_email),
                textButton = stringResource(R.string.login_text_sign_in),
                onBack = { showForm = null },
                onSign = { user, password ->
                    onAction(LoginAction.OnEmailSignIn(user, password))
                },
                forgotYourPasswordText = {
                    ForgotYourPasswordText {}
                },
                buttonContent = {
                    RegisterButton(
                        onRegister = onRegister,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                },
            )
        }


        var codeSelected by rememberSaveable { mutableStateOf("+34") }
        var phone by rememberSaveable { mutableStateOf("") }

        getActivity()?.let { activity ->
            // Usar activity, por ejemplo, para Firebase PhoneAuthProvider.verifyPhoneNumber
            AnimatedVisibility(visible = showForm == LoginFormType.PHONE) {

                PhoneOptionContent(
                    title = stringResource(R.string.login_text_sign_in_with_phone),
                    label = stringResource(R.string.login_text_phone),
                    initialValue = phone,
                    textButton = stringResource(R.string.login_text_send_code),
                    isEnabled = (codeSelected + phone.trim()).matches(Regex("^\\+\\d{1,3}\\d{7,15}$")),
                    error = error,
                    leadingIconContent = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = Icons.Default.Phone.toString()
                        )
                    },
                    dropDownContent = { DropDownMenu(onCodeSelected = { codeSelected = it }) },
                    onInitialValue = { phone = it },
                    onClick = {
                        onAction(
                            LoginAction.OnPhoneSignIn(
                                codeSelected + phone.trim(),
                                activity
                            )
                        )
                    },
                    onBack = { showForm = null }
                )
            }
        }
    }
}

@Composable
private fun LoginOptionsContent(
    onShowEmailForm: () -> Unit,
    onShowPhoneForm: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onFacebookSignIn: () -> Unit,
) {

    val containerColor = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.onSurface,
    )
    val shape = OutlinedTextFieldDefaults.shape

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Botón Email
        Button(
            onClick = onShowEmailForm,
            colors = containerColor,
            shape = shape,
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
        ) {
            Icon(
                Icons.Default.Email,
                contentDescription = stringResource(R.string.login_text_sign_in),
            )
            Spacer(Modifier.width(8.dp))
            Text(text = stringResource(R.string.login_text_sign_in_with_email))
        }

        // Botón Teléfono
        Button(
            onClick = onShowPhoneForm,
            colors = containerColor,
            shape = shape,
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = stringResource(R.string.login_text_sign_in_with_phone),
            )
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.login_text_sign_in_with_phone))
        }

        // Botón Google
        Button(
            onClick = onGoogleSignIn,
            colors = containerColor,
            shape = shape,
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_google),
                contentDescription = stringResource(R.string.login_text_sign_in_with_google),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                if (false) stringResource(R.string.login_text_signing_in)
                else stringResource(R.string.login_text_sign_in_with_google)
            )
        }

        // Botón Facebook
        Button(
            onClick = onFacebookSignIn,
            colors = containerColor,
            shape = shape,
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_facebook), // Asegúrate de tener el icono
                contentDescription = stringResource(R.string.login_text_sign_in_with_facebook),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.login_text_sign_in_with_facebook))
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
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}
