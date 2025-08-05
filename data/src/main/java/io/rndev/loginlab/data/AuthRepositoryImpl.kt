package io.rndev.loginlab.data

import android.app.Activity
import android.content.Context
import io.rndev.loginlab.AuthRepository
import io.rndev.loginlab.Result
import io.rndev.loginlab.User
import io.rndev.loginlab.datasource.AuthRemoteDataSource
import io.rndev.loginlab.datasource.FacebookLoginHandler
import io.rndev.loginlab.datasource.GoogleTokenRemoteDataSource
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

//Implementación del repositorio de autenticación
class AuthRepositoryImpl @Inject constructor(
    val authRemoteDataSource: AuthRemoteDataSource,
    val googleTokenRemoteDataSource: GoogleTokenRemoteDataSource,
    val facebookLoginHandler: FacebookLoginHandler
) : AuthRepository {

    override fun currentUser(): Flow<Result<User>> = authRemoteDataSource.currentUser()

    override fun isAuthenticated() = authRemoteDataSource.isAuthenticated()

    override suspend fun emailSignIn(email: String, password: String) =
        authRemoteDataSource.emailSignIn(email, password)

    override suspend fun emailSignUp(email: String, password: String) =
        authRemoteDataSource.emailSignUp(email, password)

    override suspend fun googleSignIn(context: Context): Flow<Result<Boolean>> {
        return when (val idTokenResult = googleTokenRemoteDataSource.getGoogleIdToken(context)) {
            is Result.Success -> authRemoteDataSource.googleSingIn(idTokenResult.data)
            is Result.Error -> flowOf(Result.Error(idTokenResult.exception))
        }
    }

    override fun getFbLoginActivityResultContract() =
        facebookLoginHandler.getLoginActivityResultContract()

    override suspend fun facebookSignIn(): Flow<Result<Boolean>> = channelFlow {
        facebookLoginHandler.registerCallback(
            onSuccess = { token ->
                launch {
                    authRemoteDataSource.facebookSingIn(token).collectLatest {
                        trySend(it) // emitimos el resultado del login remoto
                    }
                }
            },
            onError = { message ->
                trySend(Result.Error(Exception(message)))
            },
            onCancel = {
                trySend(Result.Error(Exception("Facebook login cancelled")))
            }
        )

        awaitClose()
    }

    override suspend fun phoneWithOtpSignIn(verificationId: String, otpCode: String) =
        authRemoteDataSource.phoneWithOtpSignIn(verificationId, otpCode)

    override suspend fun phoneSignIn(phoneNumber: String, activity: Activity) =
        authRemoteDataSource.phoneSingIn(phoneNumber, activity)

    override suspend fun resetPassword(email: String) =
        authRemoteDataSource.resetPassword(email)

    override fun isEmailVerified() = authRemoteDataSource.isEmailVerified()

    override fun signOut() = authRemoteDataSource.signOut()
}