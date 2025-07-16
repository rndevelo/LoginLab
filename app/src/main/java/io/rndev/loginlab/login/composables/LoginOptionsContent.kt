package io.rndev.loginlab.login.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.rndev.loginlab.utils.CustomButton
import io.rndev.loginlab.utils.LoginFormType

@Composable
fun LoginOptionsContent(
    isLoading: Boolean,
    onShowForm: (LoginFormType) -> Unit,
    onGoogleSignIn: () -> Unit,
    onFacebookSignIn: () -> Unit,
) {
    val containerColor = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.onSurface
    )

    val buttonConfigs = getLoginButtonConfigs(
        isLoading = isLoading,
        onShowForm = onShowForm,
        onGoogleSignIn = onGoogleSignIn,
        onFacebookSignIn = onFacebookSignIn
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        buttonConfigs.forEach { config ->
            CustomButton(
                onClick = config.onClick,
                buttonContent = {
                    config.icon()
                    Spacer(Modifier.width(8.dp))
                    Text(config.text)
                },
                isEnabled = true,
                colors = containerColor
            )
        }
    }
}