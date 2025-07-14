package io.rndev.loginlab.login.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.rndev.loginlab.R
import io.rndev.loginlab.utils.CustomButton

@Composable
fun RegisterButton(
    onRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    Spacer(Modifier.height(8.dp))

    // Divider
    Text(
        text = stringResource(R.string.login_text_or),
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier
    )

    Spacer(Modifier.height(8.dp))

    // Button to create account
    CustomButton(
        buttonContent = { Text(stringResource(R.string.login_text_create_account)) },
        onClick = onRegister,
    )
}