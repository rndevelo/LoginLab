package io.rndev.loginlab.login.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.rndev.loginlab.R

@Composable
fun EmailOptionContent(
    title: String,
    textButton: String,
    onBack: () -> Unit,
    onSign: (String, String) -> Unit = { _, _ -> },
    passwordTextField: @Composable (
        isPasswordValid: Boolean,
        localError: Boolean,
        onLocalError: () -> Unit
    ) -> Unit = { _, _, _ -> },
    forgotYourPasswordText: @Composable () -> Unit = {},
    buttonContent: @Composable () -> Unit = {},
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var localError by rememberSaveable { mutableStateOf(false) }

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    val isPasswordValid by rememberSaveable(password) { mutableStateOf(password.length >= 8) }

    Column(Modifier.animateContentSize()) {

        SignInOptionTitle(title = title, onBack = onBack)

        Spacer(Modifier.height(16.dp))

        // Campo Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                localError = false
            },
            label = { Text(stringResource(R.string.login_text_email)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            isError = !isEmailValid(email) && localError,
            supportingText = if (!isEmailValid(email) && localError) { // Condición aquí
                { // Lambda que devuelve el Composable
                    AnimatedVisibility(visible = true) { // 'visible = true' porque el if ya lo controla
                        Text("El email no es válido.")
                    }
                }
            } else {
                null // Pasar null cuando no hay error
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))

        // Campo Contraseña
        passwordTextField(isPasswordValid, localError, { localError = false })

        // Campo Contraseña
        PasswordTextField(
            value = password,
            isPasswordValid = isPasswordValid,
            localError = localError,
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions {
                onClickSign(
                    isValid = isEmailValid(email) && isPasswordValid,
                    onSign = { onSign(email.trim(), password.trim()) },
                    onLocalError = { localError = true }
                )
            },
            onValueChange = {
                password = it
                localError = false
            },
        )

        forgotYourPasswordText()

        Spacer(Modifier.height(16.dp))

        // Botón iniciar sesión
        Button(
            onClick = {
                onClickSign(
                    isValid = isEmailValid(email) && isPasswordValid,
                    onSign = { onSign(email.trim(), password.trim()) },
                    onLocalError = { localError = true }
                )
            },
            enabled = email.isNotBlank() && password.isNotBlank(),
            shape = OutlinedTextFieldDefaults.shape,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(textButton)
        }
        buttonContent()
    }
}

fun onClickSign(isValid: Boolean, onSign: () -> Unit, onLocalError: () -> Unit) {
    if (isValid) {
        onSign()
    } else {
        onLocalError()
    }
}

