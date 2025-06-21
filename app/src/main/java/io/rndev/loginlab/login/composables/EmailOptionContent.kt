package io.rndev.loginlab.login.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
    error: String?,
    onBack: () -> Unit,
    onSign: (String, String) -> Unit = { _, _ -> },
    passwordTextField: @Composable () -> Unit = {},
    forgotYourPasswordText: @Composable () -> Unit = {},
    buttonContent: @Composable () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column {
        // Encabezado con botón atrás
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = Icons.AutoMirrored.Default.ArrowBack.toString()
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(Modifier.height(16.dp))

        // Campo Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.login_text_email)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            isError = error != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // Campo Contraseña
        passwordTextField()

        // Campo Contraseña
        PasswordTextField(
            value = password,
            error = error,
            imeAction = ImeAction.Done,
            onValueChange = { password = it },
            onDone = { onSign(email.trim(), password.trim()) },
        )

        Spacer(Modifier.height(16.dp))

        // Botón iniciar sesión
        Button(
            onClick = { onSign(email.trim(), password.trim()) },
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
        ) {
            Text(textButton)
        }

        buttonContent()
    }
}
