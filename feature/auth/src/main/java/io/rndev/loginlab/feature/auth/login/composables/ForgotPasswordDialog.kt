package io.rndev.loginlab.feature.auth.login.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.requestFocus
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.error
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun ForgotPasswordDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onSendResetLink: (email: String) -> Unit // Callback con el email ingresado
) {
    var email by remember(showDialog) { mutableStateOf("") } // Reinicia email si el diálogo se recrea
    var emailError by remember { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    if (showDialog) {
        LaunchedEffect(Unit) {
            // Solicitar foco al campo de texto cuando el diálogo aparece
            focusRequester.requestFocus()
        }

        AlertDialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(dismissOnClickOutside = false), // Opcional: evitar cierre al tocar fuera
            icon = {
                Icon(
                    imageVector = Icons.Outlined.LockReset,
                    contentDescription = "Reset Password Icon",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = "Reset Password", // stringResource(R.string.forgot_password_dialog_title)
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter your email address and we'll send you a link to reset your password.", // stringResource(R.string.forgot_password_dialog_message)
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError != null) emailError = null // Limpiar error al escribir
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        label = { Text("Email") }, // stringResource(R.string.email_label)
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = "Email Icon"
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
//                                if (isValidEmail(email)) {
//                                    keyboardController?.hide()
//                                    onSendResetLink(email)
//                                } else {
//                                    emailError = "Please enter a valid email address." // stringResource(R.string.invalid_email_format)
//                                }
                            }
                        ),
                        isError = emailError != null,
                        supportingText = {
                            if (emailError != null) {
                                Text(emailError!!, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
//                        if (isValidEmail(email)) {
//                            keyboardController?.hide()
//                            onSendResetLink(email)
//                            // El diálogo se cerrará con onDismissRequest llamado por el ViewModel
//                            // después de que la lógica de envío sea exitosa.
//                        } else {
//                            emailError = "Please enter a valid email address." // stringResource(R.string.invalid_email_format)
//                        }
                    }
                ) {
                    Text("Send Reset Link") // stringResource(R.string.send_reset_link_button)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel") // stringResource(R.string.cancel_button)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface, // Fondo del diálogo
            tonalElevation = 8.dp // Sombra sutil
        )
    }
}