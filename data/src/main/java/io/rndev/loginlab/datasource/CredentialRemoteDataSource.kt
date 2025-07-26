package io.rndev.loginlab.datasource

import android.app.Activity
import android.content.Context
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow

interface CredentialRemoteDataSource {
    suspend fun getGoogleCredential(context: Context): Result<AuthCredential>
    fun getPhoneAuthProcessEvent(phoneNumber: String, activity: Activity): Flow<PhoneAuthProcessEvent>
    fun getFacebookCredential(token: String): AuthCredential
    fun getVerifyPhoneCredential(verificationId: String, otpCode: String): AuthCredential
}