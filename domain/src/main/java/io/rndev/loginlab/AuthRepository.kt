package io.rndev.loginlab

import android.app.Activity
import android.content.Context
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun currentUser(): Flow<Result<User>>
    fun isAuthenticated(): Flow<Result<Boolean?>>
    suspend fun emailSignIn(email: String, password: String): Flow<Result<Boolean>>
    suspend fun emailSignUp(email: String, password: String): Flow<Result<Boolean>>
    suspend fun googleSingIn(context: Context): Flow<Result<Boolean>>
    suspend fun facebookSingIn(token: String): Flow<Result<Boolean>>
    suspend fun phoneWithOtpSignIn(verificationId: String, otpCode: String): Flow<Result<Boolean>>
    suspend fun phoneSingIn(phoneNumber: String, activity: Activity): Flow<PhoneAuthEvent>
    suspend fun resetPassword(email: String): Flow<Result<Boolean>>
    fun isEmailVerified(): Flow<Boolean>
    fun signOut()
}