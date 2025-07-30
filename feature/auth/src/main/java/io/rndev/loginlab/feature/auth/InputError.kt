package io.rndev.loginlab.feature.auth

import android.util.Patterns
import io.rndev.loginlab.feature.core.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed class InputError() {

    abstract fun validate(uiState: UiState): String?

    object InvalidEmail : InputError() {
        override fun validate(uiState: UiState): String? {
            return if (!Patterns.EMAIL_ADDRESS.matcher(uiState.email)
                    .matches() && uiState.email.isNotBlank()
            ) "El email no es válido." else null
        }
    }

    object PasswordTooShort : InputError() {
        override fun validate(uiState: UiState): String? {
            return if (uiState.password.length < 8 && uiState.password.isNotBlank())
                "La contraseña debe tener al menos 8 caracteres." else null
        }
    }

    object PasswordsDoNotMatch : InputError() {
        override fun validate(uiState: UiState): String? {
            return if (uiState.password != uiState.confirmPassword && uiState.confirmPassword.isNotBlank()) {
                "Las contraseñas no coinciden."
            } else null
        }
    }
}

