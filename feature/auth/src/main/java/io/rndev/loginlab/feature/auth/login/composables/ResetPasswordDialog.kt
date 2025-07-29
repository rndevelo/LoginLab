package io.rndev.loginlab.feature.auth.login.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.rndev.loginlab.feature.core.R
import io.rndev.loginlab.feature.core.composables.emailSupportingText

@Composable
fun ResetPasswordDialog(
    showDialog: Boolean,
    email: String,
    emailError: String?,
    localError: Boolean,
    onEmailValueChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onSendResetLink: (email: String) -> Unit,
) {

    AnimatedVisibility(showDialog) {

        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.LockReset,
                    contentDescription = Icons.Outlined.LockReset.toString(),
                    modifier = Modifier.size(45.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text(text = stringResource(R.string.login_text_reset_password)) },
            text = {
                Column {
                    Text(text = stringResource(R.string.login_text_we_ll_send_you_a_link_to_reset_your_password))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { onEmailValueChange(it) },
                        label = { Text(stringResource(R.string.login_text_email)) },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        supportingText = emailSupportingText(emailError),
                        singleLine = true,
                        isError = emailError != null && localError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onDismissRequest()
                                onSendResetLink(email)
                            }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = emailError == null,
                    onClick = {
                        onDismissRequest()
                        onSendResetLink(email)
                    }
                ) {
                    Text(stringResource(R.string.login_text_send_reset_link)) // stringResource(R.string.send_reset_link_button)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(R.string.login_text_cancel)) // stringResource(R.string.cancel_button)
                }
            },
            shape = OutlinedTextFieldDefaults.shape,
            tonalElevation = 8.dp // Sombra sutil
        )
    }
}