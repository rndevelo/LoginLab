package io.rndev.loginlab.login.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.rndev.loginlab.R
import io.rndev.loginlab.composables.LoadingAnimation
import io.rndev.loginlab.utils.CustomButton

@Composable
fun EmailOptionContent(
    isLoading: Boolean,
    title: String,
    email: String,
    emailError: String?,
    password: String,
    localError: Boolean,
    onEmailValueChange: (String) -> Unit,
    textButton: String,
    onBack: () -> Unit,
    onClick: () -> Unit = { },
    firstPasswordTextField: @Composable () -> Unit,
    secondPasswordTextField: @Composable () -> Unit = {},
    forgotYourPasswordText: @Composable () -> Unit = {},
    buttonContent: @Composable () -> Unit = {},
) {

    Column(Modifier.animateContentSize()) {

        SignInOptionTitle(title = title, onBack = onBack)

        Spacer(Modifier.height(8.dp))

        // Campo Email
        OutlinedTextField(
            value = email,
            onValueChange = { onEmailValueChange(it) },
            label = { Text(stringResource(R.string.login_text_email)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            supportingText = if (emailError != null && localError) { // Condición aquí
                { // Lambda que devuelve el Composable
                    AnimatedVisibility(visible = true) { // 'visible = true' porque el if ya lo controla
                        Text(emailError)
                    }
                }
            } else {
                null // Pasar null cuando no hay error
            },
            singleLine = true,
            isError = emailError != null && localError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))

        firstPasswordTextField()
        secondPasswordTextField()
        forgotYourPasswordText()

        Spacer(Modifier.height(16.dp))

        // Botón iniciar sesión
        CustomButton(
            onClick = onClick,
            buttonContent = {
                if (isLoading) LoadingAnimation()
                else Text(textButton)
            },
            isEnabled = email.isNotBlank() && password.isNotBlank(),
        )

        buttonContent()
    }
}
