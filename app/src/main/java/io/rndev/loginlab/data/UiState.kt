package io.rndev.loginlab.data

data class UiState(
    val isLoading: Boolean? = null,
    val isLoggedIn: Boolean? = null,
    val user: User? = null,
    val error: String? = null,
)