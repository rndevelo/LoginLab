package io.rndev.loginlab.domain

import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import io.rndev.loginlab.Result
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val credentialManager: CredentialManager,
    private val getCredentialRequest: GetCredentialRequest,
    private val signInWithCredentialUseCase: SignInWithCredentialUseCase
) {
    suspend operator fun invoke(context: Context): Result<Boolean> {
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

    private suspend fun googleCredentialResult(credential: Credential): Result<Boolean> =
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                signInWithCredentialUseCase(authCredential)

            } catch (e: GoogleIdTokenParsingException) {
                Result.Error(e)
            }
        } else {
            Result.Error(IllegalStateException("Credential is not of type Google ID Token."))
        }
}

