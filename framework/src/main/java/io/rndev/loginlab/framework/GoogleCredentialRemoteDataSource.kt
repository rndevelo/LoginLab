package io.rndev.loginlab.framework

import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import io.rndev.loginlab.Result
import io.rndev.loginlab.datasource.CredentialRemoteDataSource
import javax.inject.Inject

class GoogleCredentialRemoteDataSource @Inject constructor(
    private val getCredentialRequest: GetCredentialRequest,
) : CredentialRemoteDataSource {

    override suspend fun getGoogleIdToken(context: Context): Result<String> {
        return try {
            val credentialManager = CredentialManager.create(context)
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
}

private fun googleCredentialResult(credential: Credential): Result<String> =
    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(credential.data)
            Result.Success(googleIdTokenCredential.idToken)

        } catch (e: GoogleIdTokenParsingException) {
            Result.Error(e)
        }
    } else {
        Result.Error(IllegalStateException("Credential is not of type Google ID Token."))
    }