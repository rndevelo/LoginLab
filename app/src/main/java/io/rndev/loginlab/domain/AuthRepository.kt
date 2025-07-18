package io.rndev.loginlab.domain

import com.google.firebase.auth.AuthCredential
import io.rndev.loginlab.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun currentUser(): Flow<Result<User>>
    fun isAuthenticated(): Flow<Result<Boolean>>
    suspend fun emailSignIn(email: String, password: String): Result<Boolean>
    suspend fun emailSignUp(email: String, password: String): Result<Boolean>
    suspend fun credentialSingIn(credential: AuthCredential): Result<Boolean>
    fun isEmailVerified(): Flow<Boolean>
    fun signOut()
}