package io.rndev.loginlab.ui.login.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.rndev.loginlab.R

@Composable
fun CreateAccountContent(
    onRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    Spacer(Modifier.height(8.dp))

    Row(modifier = modifier) {
        Text(
            text = stringResource(R.string.login_text_no_account_prompt), // "Aún no tienes cuenta?"
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = stringResource(R.string.login_text_create_account), // " Crear cuenta" (con espacio)
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.clickable(onClick = onRegister)
        )
    }
}

