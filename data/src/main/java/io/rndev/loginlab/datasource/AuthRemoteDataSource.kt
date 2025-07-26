package io.rndev.loginlab.datasource

import com.google.firebase.auth.AuthCredential
import io.rndev.loginlab.User
import kotlinx.coroutines.flow.Flow

//Interfaz del repositorio de autenticación, ha sido creada porque ahora mismo la autenticación se hace con Firebase
interface AuthRemoteDataSource {
    fun currentUser(): Flow<Result<User>>
    fun isAuthenticated(): Flow<Result<Boolean>>
    suspend fun emailSignIn(email: String, password: String): Result<Boolean>
    suspend fun emailSignUp(email: String, password: String): Result<Boolean>
    suspend fun credentialSingIn(credential: AuthCredential): Result<Boolean>
    fun isEmailVerified(): Flow<Boolean>
    fun signOut()
}