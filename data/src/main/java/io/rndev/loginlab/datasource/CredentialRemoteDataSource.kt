package io.rndev.loginlab.datasource

import android.content.Context
import io.rndev.loginlab.Result

interface CredentialRemoteDataSource {
    suspend fun getGoogleIdToken(context: Context): Result<String>
}