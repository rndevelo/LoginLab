package io.rndev.loginlab.utils

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

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
            isLoading = false
        )
    }
    Log.d("onValidateInputs", "onValidateInputs: $errors")

    if (isValid) {
        onSign()
    }
}