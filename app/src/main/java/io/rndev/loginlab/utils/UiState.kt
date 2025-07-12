package io.rndev.loginlab.utils

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
    val errorMessage: String?

)  {
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
        errorMessage = null
    )
}