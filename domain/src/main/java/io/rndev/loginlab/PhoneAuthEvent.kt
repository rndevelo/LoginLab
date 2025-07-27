package io.rndev.loginlab

import kotlinx.coroutines.flow.Flow

sealed class PhoneAuthEvent {
    data class VerificationCompleted(val result: Flow<Result<Boolean>>) : PhoneAuthEvent()
    data class VerificationFailed(val error: Exception) : PhoneAuthEvent()
    data class CodeSent(val verificationId: String) : PhoneAuthEvent()
    // Podrías añadir más estados si son necesarios, ej: AutoRetrievalTimeout
}