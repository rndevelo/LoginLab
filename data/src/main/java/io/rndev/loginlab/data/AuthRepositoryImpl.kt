package io.rndev.loginlab.data

import android.app.Activity
import android.content.Context
import io.rndev.loginlab.AuthRepository
import io.rndev.loginlab.Result
import io.rndev.loginlab.User
import io.rndev.loginlab.data.datasource.AuthRemoteDataSource
import io.rndev.loginlab.data.datasource.TokenRemoteDataSource
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

//Implementación del repositorio de autenticación
class AuthRepositoryImpl @Inject constructor(
    val authRemoteDataSource: AuthRemoteDataSource,
    val tokenRemoteDataSource: TokenRemoteDataSource
) : AuthRepository {

    override fun currentUser(): Flow<Result<User>> = authRemoteDataSource.currentUser()
    override fun isAuthenticated() = authRemoteDataSource.isAuthenticated()
    override suspend fun emailSignIn(email: String, password: String) =
        authRemoteDataSource.emailSignIn(email, password)

    override suspend fun emailSignUp(email: String, password: String) =
        authRemoteDataSource.emailSignUp(email, password)

    override suspend fun googleSingIn(context: Context): Flow<Result<Boolean>> {
        return when (val idTokenResult = tokenRemoteDataSource.getGoogleIdToken(context)) {
            is Result.Success -> authRemoteDataSource.googleSingIn(idTokenResult.data)
            is Result.Error -> flowOf(Result.Error(idTokenResult.exception))
        }
    }

    override suspend fun facebookSingIn(token: String) = authRemoteDataSource.facebookSingIn(token)

    override suspend fun phoneWithOtpSignIn(verificationId: String, otpCode: String) =
        authRemoteDataSource.phoneWithOtpSignIn(verificationId, otpCode)

    override suspend fun phoneSingIn(phoneNumber: String, activity: Activity) =
        authRemoteDataSource.phoneSingIn(phoneNumber, activity)

    override suspend fun resetPassword(email: String) =
        authRemoteDataSource.resetPassword(email)

    override fun isEmailVerified() = authRemoteDataSource.isEmailVerified()
    override fun signOut() = authRemoteDataSource.signOut()
}