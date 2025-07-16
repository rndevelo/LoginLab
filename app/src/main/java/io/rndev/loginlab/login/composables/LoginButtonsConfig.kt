package io.rndev.loginlab.login.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.rndev.loginlab.R
import io.rndev.loginlab.utils.LoginFormType

data class LoginButtonConfig(
    val icon: @Composable () -> Unit,
    val text: String,
    val onClick: () -> Unit
)

@Composable
fun getLoginButtonConfigs(
    isLoading: Boolean,
    onShowForm: (LoginFormType) -> Unit,
    onGoogleSignIn: () -> Unit,
    onFacebookSignIn: () -> Unit,
) = listOf(
    LoginButtonConfig(
        icon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = stringResource(R.string.login_text_sign_in_with_email)
            )
        },
        text = stringResource(R.string.login_text_sign_in_with_email),
        onClick = { onShowForm(LoginFormType.EMAIL) }
    ),
    LoginButtonConfig(
        icon = {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = stringResource(R.string.login_text_sign_in_with_phone)
            )
        },
        text = stringResource(R.string.login_text_sign_in_with_phone),
        onClick = { onShowForm(LoginFormType.PHONE) }
    ),
    LoginButtonConfig(
        icon = {
            Image(
                painter = painterResource(R.drawable.ic_google),
                contentDescription = stringResource(R.string.login_text_sign_in_with_google),
                modifier = Modifier.size(20.dp)
            )
        },
        text = if (isLoading)
            stringResource(R.string.login_text_signing_in)
        else
            stringResource(R.string.login_text_sign_in_with_google),
        onClick = onGoogleSignIn
    ),
    LoginButtonConfig(
        icon = {
            Image(
                painter = painterResource(R.drawable.ic_facebook),
                contentDescription = stringResource(R.string.login_text_sign_in_with_facebook),
                modifier = Modifier.size(20.dp)
            )
        },
        text = stringResource(R.string.login_text_sign_in_with_facebook),
        onClick = onFacebookSignIn
    )
)