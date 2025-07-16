package io.rndev.loginlab.domain

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import io.rndev.loginlab.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed interface PhoneAuthProcessEvent {
    data class VerificationCompleted(val result: Result<Boolean>) : PhoneAuthProcessEvent
    data class VerificationFailed(val error: FirebaseException) : PhoneAuthProcessEvent
    data class CodeSent(val verificationId: String) : PhoneAuthProcessEvent
}

class StartPhoneAuthVerificationUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth, // Se inyecta para crear PhoneAuthOptions
    private val signInWithCredentialUseCase: SignInWithCredentialUseCase
) {
    operator fun invoke(
        phoneNumber: String,
        activity: Activity,
        timeoutSeconds: Long = 60L // Hacerlo configurable es una buena práctica
    ): Flow<PhoneAuthProcessEvent> = callbackFlow {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                launch {
                    trySend(
                        PhoneAuthProcessEvent.VerificationCompleted(
                            signInWithCredentialUseCase(
                                credential
                            )
                        )
                    )
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                trySend(PhoneAuthProcessEvent.VerificationFailed(e))
                // Podrías cerrar aquí si consideras el fallo terminal para este flujo específico.
                // close(e)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                trySend(PhoneAuthProcessEvent.CodeSent(verificationId))
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose()
    }
}