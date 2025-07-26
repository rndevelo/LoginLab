package io.rndev.loginlab

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
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose()
    }

    override fun getFacebookCredential(token: String) = FacebookAuthProvider.getCredential(token)

    override fun getVerifyPhoneCredential(
        verificationId: String,
        otpCode: String
    ): AuthCredential = PhoneAuthProvider.getCredential(verificationId, otpCode)
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