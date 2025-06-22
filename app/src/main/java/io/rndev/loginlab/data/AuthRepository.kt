package io.rndev.loginlab.data

import kotlinx.coroutines.flow.Flow
import io.rndev.loginlab.Result


interface AuthRepository {
    fun currentUser(): Flow<Result<User>>
    fun isAuthenticated(): Flow<Result<Boolean>>
    fun signIn(email: String, password: String): Flow<Result<Boolean>>
    fun signUp(email: String, password: String): Flow<Result<Boolean>>
    fun signOut()
}