package io.rndev.loginlab.feature.core

interface BaseUiState {
    val isLoading: Boolean?
    val errorMessage: String?
}