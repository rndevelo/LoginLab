package io.rndev.loginlab.domain

import android.app.Activity
import android.content.Context
import com.google.firebase.auth.AuthCredential
import io.rndev.loginlab.Result
import io.rndev.loginlab.data.datasources.PhoneAuthProcessEvent
import kotlinx.coroutines.flow.Flow

interface CredentialRepository {
    suspend fun getGoogleCredential(context: Context): Result<AuthCredential>
    fun getPhoneAuthProcessEvent(phoneNumber: String, activity: Activity, timeoutSeconds: Long): Flow<PhoneAuthProcessEvent>
    fun getFacebookCredential(token: String): AuthCredential
}