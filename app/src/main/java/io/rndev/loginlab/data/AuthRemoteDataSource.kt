package io.rndev.loginlab.data

import kotlinx.coroutines.flow.Flow

interface AuthRemoteDataSource {
    fun isAuthenticated(): Flow<Boolean>
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun signOut()
}