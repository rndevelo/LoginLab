package io.rndev.loginlab.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.rndev.loginlab.R
import io.rndev.loginlab.login.composables.EmailOptionContent
import io.rndev.loginlab.login.composables.ForgotYourPasswordText
import io.rndev.loginlab.login.composables.RegisterButton
import kotlinx.serialization.Serializable

@Serializable
data object Login

@Composable
fun LoginScreen(
    vm: LoginViewModel,
    onRegister: () -> Unit,
    onHome: () -> Unit
) {

//    val state = vm.state

    var isShowEmailForm by remember { mutableStateOf(false) }

//    val isAuthenticated by vm.isAuthenticated.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .animateContentSize()
            .verticalScroll(rememberScrollState()) // 👈 permite hacer scroll si el teclado tapa
            .imePadding()                          // 👈 añade padding automático cuando aparece el teclado
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

//        Text(isAuthenticated.toString())

        // Título
        Text(
            text = "Login in...",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        // Tipos de inicio de sesión
        AnimatedVisibility(visible = !isShowEmailForm) {
            LoginOptionsContent { isShowEmailForm = true }
        }

        // Formulario de Email
        AnimatedVisibility(visible = isShowEmailForm) {
            EmailOptionContent(
                title = stringResource(R.string.login_text_sign_in_with_email),
                textButton = stringResource(R.string.login_text_sign_in),
                error = null,
                onBack = { isShowEmailForm = false },
                onSign = vm::onSignIn,
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
    }
}

@Composable
private fun LoginOptionsContent(onShowEmailForm: () -> Unit) {

    Column {
        // Botón Email
        OutlinedButton(
            onClick = onShowEmailForm,
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
        ) {
            Icon(
                Icons.Default.Email,
                contentDescription = stringResource(R.string.login_text_sign_in),
                tint = Color.Gray
            )
            Spacer(Modifier.width(8.dp))
            Text(text = stringResource(R.string.login_text_sign_in_with_email))
        }

        Spacer(Modifier.height(12.dp))

        // Botón Google
        OutlinedButton(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_google),
                contentDescription = R.drawable.ic_google.toString(),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                if (false) stringResource(R.string.login_text_signing_in)
                else stringResource(R.string.login_text_sign_in_with_google)
            )
        }
    }
}
