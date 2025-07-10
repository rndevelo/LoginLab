package io.rndev.loginlab

sealed class UiEvent {
    object NavigateToHome : UiEvent()
    data class NavigateToVerification(val verificationId: String) : UiEvent()
    data class ShowError(val message: String) : UiEvent()
}