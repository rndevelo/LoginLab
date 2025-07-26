package io.rndev.loginlab.feature.core

enum class LoginFormType { EMAIL, PHONE }

data class UiState(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val emailError: String?,
    val passwordError: String?,
    val confirmPasswordError: String?,
    val localError: Boolean,
    val isEmailSent: Boolean?,
    val isEmailVerified: Boolean?,
    val isLoading: Boolean?,
    val errorMessage: String?,
    val loginFormType: LoginFormType?,
) {
    constructor() : this(
        email = "",
        password = "",
        confirmPassword = "",
        emailError = null,
        passwordError = null,
        confirmPasswordError = null,
        localError = false,
        isEmailSent = null,
        isEmailVerified = null,
        isLoading = null,
        errorMessage = null,
        loginFormType = null,
    )
}