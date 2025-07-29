package io.rndev.loginlab.feature.core.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.rndev.loginlab.feature.core.R

@Composable
fun PasswordTextField(
    value: String,
    passwordError: String? = null,
    confirmPasswordError: String? = null,
    localError: Boolean,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions = KeyboardActions(),
    onValueChange: (String) -> Unit,
) {

    var isPasswordVisible by remember { mutableStateOf(false) } // 👈 estado para visibilidad
    val icon = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
    val description = if (isPasswordVisible) stringResource(R.string.login_text_hide_password)
    else stringResource(R.string.login_text_show_password)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.login_text_password)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = Icons.Default.Lock.toString()
            )
        },
        trailingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = description,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { isPasswordVisible = !isPasswordVisible }
            )
        },
        supportingText = passwordSupportingText(passwordError,confirmPasswordError),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        isError = localError && passwordError != null || localError && confirmPasswordError != null,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun passwordSupportingText(
    passwordError: String?,
    confirmPasswordError: String?,
): @Composable (() -> Unit)? = when {
    passwordError != null -> { // Condición aquí
        {
            AnimatedVisibility(visible = true) { // 'visible = true' porque el if ya lo controla
                Text(passwordError)
            }
        }
    }

    confirmPasswordError != null -> {
        {
            AnimatedVisibility(visible = true) { // 'visible = true' porque el if ya lo controla
                Text(confirmPasswordError)
            }
        }
    }
    else -> null // Pasar null cuando no hay error
}
