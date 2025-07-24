package io.rndev.loginlab.data

import android.app.Activity
import android.content.Context
import io.rndev.loginlab.domain.Result
import io.rndev.loginlab.data.datasources.AuthRemoteDataSource
import io.rndev.loginlab.data.datasources.CredentialRemoteDataSource
import io.rndev.loginlab.data.datasources.PhoneAuthProcessEvent
import io.rndev.loginlab.domain.AuthRepository
import io.rndev.loginlab.domain.User
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.transformLatest

//Implementación del repositorio de autenticación
class AuthRepositoryImpl @Inject constructor(
    val authRemoteDataSource: AuthRemoteDataSource,
    val credentialRemoteDataSource: CredentialRemoteDataSource
) : AuthRepository {

    override fun currentUser(): Flow<Result<User>> = authRemoteDataSource.currentUser()
    override fun isAuthenticated() = authRemoteDataSource.isAuthenticated()
    override suspend fun emailSignIn(email: String, password: String) =
        authRemoteDataSource.emailSignIn(email, password)

    override suspend fun emailSignUp(email: String, password: String) =
        authRemoteDataSource.emailSignUp(email, password)

    override suspend fun googleSingIn(context: Context): Result<Boolean> {
        return when (val credentialResult =
            credentialRemoteDataSource.getGoogleCredential(context)) {
            is Result.Success -> {
                when (val signInResult =
                    authRemoteDataSource.credentialSingIn(credentialResult.data)) {
                    is Result.Success -> Result.Success(signInResult.data)
                    is Result.Error -> Result.Error(signInResult.exception)
                }
            }

            is Result.Error -> Result.Error(credentialResult.exception)
        }
    }

    override suspend fun phoneSingIn(
        phoneNumber: String,
        activity: Activity
    ): Flow<Result<PhoneAuthProcessEvent>> {
        return credentialRemoteDataSource.getPhoneAuthProcessEvent(
            phoneNumber,
            activity
        ) // Este es Flow<PhoneAuthProcessEvent_DataSource>
            .transformLatest { dataSourceEvent -> // o flatMapConcat, etc.
                when (dataSourceEvent) {
                    is PhoneAuthProcessEvent.VerificationCompleted -> {
                        // dataSourceEvent.result es AuthCredential
                        when (val signInResult =
                            authRemoteDataSource.credentialSingIn(dataSourceEvent.result)
                        ) {
                            is Result.Success -> emit(Result.Success(dataSourceEvent))
                            is Result.Error -> emit(Result.Error(signInResult.exception))
                        }
                    }

                    is PhoneAuthProcessEvent.VerificationFailed -> emit(Result.Error(dataSourceEvent.error))
                    is PhoneAuthProcessEvent.CodeSent -> emit(Result.Success(dataSourceEvent))

                }
            }
            .catch { e -> // Capturar excepciones del flujo en sí mismo
                emit(Result.Error(e))
            }
    }

    override suspend fun verifyPhoneSingIn(
        verificationId: String,
        otpCode: String
    ): Result<Boolean> {
        val credentialResult = credentialRemoteDataSource.getVerifyPhoneCredential(verificationId, otpCode)
        return when (val signInResult = authRemoteDataSource.credentialSingIn(credentialResult)) {
            is Result.Success -> Result.Success(signInResult.data)
            is Result.Error -> Result.Error(signInResult.exception)
        }
    }

    override suspend fun facebookSingIn(token: String): Result<Boolean> {
        val credentialResult = credentialRemoteDataSource.getFacebookCredential(token)
        return when (val signInResult = authRemoteDataSource.credentialSingIn(credentialResult)) {
            is Result.Success -> Result.Success(signInResult.data)
            is Result.Error -> Result.Error(signInResult.exception)
        }
    }

    override fun isEmailVerified() = authRemoteDataSource.isEmailVerified()
    override fun signOut() = authRemoteDataSource.signOut()
}
