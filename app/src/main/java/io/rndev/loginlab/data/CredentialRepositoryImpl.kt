package io.rndev.loginlab.data

import android.app.Activity
import android.content.Context
import io.rndev.loginlab.data.datasources.CredentialRemoteDataSource
import io.rndev.loginlab.domain.CredentialRepository
import jakarta.inject.Inject

class CredentialRepositoryImpl @Inject constructor(
    val authRemoteDataSource: CredentialRemoteDataSource,
) : CredentialRepository {

    override suspend fun getGoogleCredential(context: Context) = authRemoteDataSource.getGoogleCredential(context)

    override fun getPhoneAuthProcessEvent(
        phoneNumber: String,
        activity: Activity,
        timeoutSeconds: Long
    ) = authRemoteDataSource.getPhoneAuthProcessEvent(phoneNumber, activity, timeoutSeconds)

    override fun getFacebookCredential(token: String) = authRemoteDataSource.getFacebookCredential(token)
}