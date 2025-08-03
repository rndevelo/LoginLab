package io.rndev.loginlab.feature.auth

sealed class UiEvent {
    object NavigateToLogin : UiEvent()
    object NavigateToHome : UiEvent()
    data class NavigateToVerification(val verificationId: String, val phone: String) : UiEvent()
    data class ShowError(val message: String) : UiEvent()
}