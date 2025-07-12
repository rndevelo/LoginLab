package io.rndev.loginlab.utils

sealed class InputError() {

    abstract fun validate(uiState: UiState): String?

    object InvalidEmail : InputError() {
        override fun validate(uiState: UiState): String? {
            return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email)
                    .matches()
            ) "El email no es válido." else null
        }
    }

    object PasswordTooShort : InputError() {
        override fun validate(uiState: UiState): String? {
            return if (uiState.password.length < 8
            ) "La contraseña debe tener al menos 8 caracteres." else null
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