package io.rndev.loginlab.data

import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow
import io.rndev.loginlab.Result

interface AuthRepository {
    fun currentUser(): Flow<Result<User>>
    fun isAuthenticated(): Flow<Result<Boolean>>
    suspend fun emailSignIn(email: String, password: String): Result<Boolean>
    suspend fun emailSignUp(email: String, password: String): Result<Boolean>
    fun credentialSingIn(credential: AuthCredential): Flow<Result<Boolean>>
    fun isEmailVerified(): Flow<Boolean>
    fun signOut()
}