package io.rndev.loginlab.data.datasources

import android.app.Activity
import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import io.rndev.loginlab.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed interface PhoneAuthProcessEvent {
    data class VerificationCompleted(val result: AuthCredential) : PhoneAuthProcessEvent
    data class VerificationFailed(val error: FirebaseException) : PhoneAuthProcessEvent
    data class CodeSent(val verificationId: String) : PhoneAuthProcessEvent
}

class FirebaseCredentialDataSource @Inject constructor(
    private val credentialManager: CredentialManager,
    private val getCredentialRequest: GetCredentialRequest,
    private val firebaseAuth: FirebaseAuth,
) : CredentialRemoteDataSource {

    override suspend fun getGoogleCredential(context: Context): Result<AuthCredential> {
        return try {
            val credentialResponse = credentialManager.getCredential(
                request = getCredentialRequest,
                context = context,
            )
            val credential = credentialResponse.credential
            googleCredentialResult(credential)

        } catch (e: GetCredentialException) { // Captura genérica para cancelaciones, etc.
            Result.Error(e)
        } catch (e: Exception) { // Otra excepción inesperada
            Result.Error(e)
        }
    }

    override fun getPhoneAuthProcessEvent(
        phoneNumber: String,
        activity: Activity,
        timeoutSeconds: Long // Hacerlo configurable es una buena práctica
    ): Flow<PhoneAuthProcessEvent> = callbackFlow {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                trySend(PhoneAuthProcessEvent.VerificationCompleted(credential))
            }

            override fun onVerificationFailed(e: FirebaseException) {
                trySend(PhoneAuthProcessEvent.VerificationFailed(e))
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

    override fun getFacebookCredential(token: String) = FacebookAuthProvider.getCredential(token)
}

private fun googleCredentialResult(credential: Credential): Result<AuthCredential> =
    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(credential.data)
            Result.Success( GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null))

        } catch (e: GoogleIdTokenParsingException) {
            Result.Error(e)
        }
    } else {
        Result.Error(IllegalStateException("Credential is not of type Google ID Token."))
    }