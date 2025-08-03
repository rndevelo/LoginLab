package io.rndev.loginlab.feature.core.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.rndev.loginlab.feature.core.R

@Composable
fun EmailOptionContent(
    isLoading: Boolean,
    title: String,
    email: String,
    isEnabled: Boolean,
    emailError: String?,
    errorMessage: String?,
    onEmailValueChange: (String) -> Unit,
    textButton: String,
    onBack: () -> Unit,
    onClick: () -> Unit = { },
    firstPasswordTextField: @Composable () -> Unit,
    secondPasswordTextField: @Composable () -> Unit = {},
    forgotYourPasswordContent: @Composable () -> Unit = {},
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
            leadingIcon = { Icon(Icons.Default.Email, null) },
            supportingText = emailSupportingText(emailError),
            singleLine = true,
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))

        firstPasswordTextField()
        secondPasswordTextField()
        forgotYourPasswordContent()

        Spacer(Modifier.height(16.dp))

        // Botón iniciar sesión
        CustomButton(
            onClick = onClick,
            buttonContent = {
                if (isLoading) LoadingAnimation()
                else Text(textButton)
            },
            isEnabled = isEnabled,
        )

        buttonContent()
    }
}

@Composable
fun emailSupportingText(emailError: String?): @Composable (() -> Unit)? =
    if (emailError != null) { // Condición aquí
        {
            AnimatedVisibility(visible = true) { // 'visible = true' porque el if ya lo controla
                Text(emailError)
            }
        }
    } else {
        null // Pasar null cuando no hay error
    }
