package io.rndev.loginlab.data

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.flow.Flow
import io.rndev.loginlab.Result


interface AuthRepository {
    fun currentUser(): Flow<Result<User>>
    fun isAuthenticated(): Flow<Result<Boolean>>
    fun signIn(email: String, password: String): Flow<Result<Boolean>>
    fun signUp(email: String, password: String): Flow<Result<Boolean>>
    fun phoneSingIn(credential: PhoneAuthCredential): Flow<Result<Boolean>>
    fun googleSingIn(credential: AuthCredential): Flow<Result<Boolean>>
    fun facebookSingIn(credential: AuthCredential): Flow<Result<Boolean>>
    fun signOut()
}