package io.rndev.loginlab.login.composables

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
import io.rndev.loginlab.R

@Composable
fun PasswordTextField(
    value: String,
    isPasswordValid: Boolean,
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
        supportingText = if (!isPasswordValid && localError) { // Condición aquí
            { // Lambda que devuelve el Composable
                AnimatedVisibility(visible = true) { // 'visible = true' porque el if ya lo controla
                    Text("La contraseña debe tener al menos 8 caracteres.")
                }
            }
        } else {
            null // Pasar null cuando no hay error
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        isError = localError && !isPasswordValid,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        modifier = Modifier.fillMaxWidth()
    )
}