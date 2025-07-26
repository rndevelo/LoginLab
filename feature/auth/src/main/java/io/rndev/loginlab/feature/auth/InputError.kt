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

fun onValidateInputs(state: MutableStateFlow<UiState>, onSign: () -> Unit) {

    val errors = listOf(
        InputError.InvalidEmail to InputError.InvalidEmail.validate(state.value),
        InputError.PasswordTooShort to InputError.PasswordTooShort.validate(state.value),
        InputError.PasswordsDoNotMatch to InputError.PasswordsDoNotMatch.validate(state.value)
    )

    val isValid = errors.all { it.second == null }

    state.update {
        it.copy(
            emailError = errors.first { it.first is InputError.InvalidEmail }.second,
            passwordError = errors.first { it.first is InputError.PasswordTooShort }.second,
            confirmPasswordError = errors.first { it.first is InputError.PasswordsDoNotMatch }.second,
            localError = true,
            isLoading = isValid
        )
    }

    if (isValid) {
        onSign()
    }
}
