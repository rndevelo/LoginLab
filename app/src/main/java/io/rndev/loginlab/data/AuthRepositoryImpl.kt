package io.rndev.loginlab.data

import com.google.firebase.auth.AuthCredential
import io.rndev.loginlab.Result
import io.rndev.loginlab.data.datasources.AuthRemoteDataSource
import io.rndev.loginlab.data.datasources.CredentialRemoteDataSource
import io.rndev.loginlab.domain.AuthRepository
import io.rndev.loginlab.domain.User
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

//Implementación del repositorio de autenticación
class AuthRepositoryImpl @Inject constructor(
    val authRemoteDataSource: AuthRemoteDataSource,
) : AuthRepository {

    override fun currentUser(): Flow<Result<User>> = authRemoteDataSource.currentUser()
    override fun isAuthenticated() = authRemoteDataSource.isAuthenticated()
    override suspend fun emailSignIn(email: String, password: String) = authRemoteDataSource.emailSignIn(email, password)
    override suspend fun emailSignUp(email: String, password: String)= authRemoteDataSource.emailSignUp(email, password)
    override suspend fun credentialSingIn(credential: AuthCredential) = authRemoteDataSource.credentialSingIn(credential)
    override fun isEmailVerified() = authRemoteDataSource.isEmailVerified()
    override fun signOut() = authRemoteDataSource.signOut()
}

