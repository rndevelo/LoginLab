package io.rndev.loginlab

sealed class FacebookAuthEvent {
    data class Success(val token: String) : FacebookAuthEvent()
    object Cancelled : FacebookAuthEvent()
    data class Error(val message: String) : FacebookAuthEvent()
}
