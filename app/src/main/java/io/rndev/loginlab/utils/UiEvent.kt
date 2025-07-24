package io.rndev.loginlab.utils

sealed class UiEvent {
    object NavigateToLogin : UiEvent()
    object NavigateToHome : UiEvent()
    data class NavigateToVerification(val verificationId: String) : UiEvent()
    data class ShowError(val message: String) : UiEvent()
}