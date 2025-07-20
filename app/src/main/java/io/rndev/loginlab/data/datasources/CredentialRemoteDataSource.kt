package io.rndev.loginlab.data.datasources

import android.app.Activity
import android.content.Context
import com.google.firebase.auth.AuthCredential
import io.rndev.loginlab.Result
import kotlinx.coroutines.flow.Flow

interface CredentialRemoteDataSource {
    suspend fun getGoogleCredential(context: Context): Result<AuthCredential>
    fun getPhoneAuthProcessEvent(phoneNumber: String, activity: Activity): Flow<PhoneAuthProcessEvent>
    fun getFacebookCredential(token: String): AuthCredential
}