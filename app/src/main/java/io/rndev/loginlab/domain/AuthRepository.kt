package io.rndev.loginlab.domain

import android.app.Activity
import android.content.Context
import com.google.firebase.auth.AuthCredential
import io.rndev.loginlab.domain.Result
import io.rndev.loginlab.data.datasources.PhoneAuthProcessEvent
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun currentUser(): Flow<Result<User>>
    fun isAuthenticated(): Flow<Result<Boolean>>
    suspend fun emailSignIn(email: String, password: String): Result<Boolean>
    suspend fun emailSignUp(email: String, password: String): Result<Boolean>
    suspend fun googleSingIn(context: Context): Result<Boolean>
    suspend fun phoneSingIn(
        phoneNumber: String,
        activity: Activity
    ): Flow<Result<PhoneAuthProcessEvent>>

    suspend fun verifyPhoneSingIn(verificationId: String, otpCode: String): Result<Boolean>
    suspend fun facebookSingIn(token: String): Result<Boolean>
    fun isEmailVerified(): Flow<Boolean>
    fun signOut()
}