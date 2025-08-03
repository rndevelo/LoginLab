package io.rndev.loginlab.data.datasource

import android.content.Context
import io.rndev.loginlab.Result

interface TokenRemoteDataSource {
    suspend fun getGoogleIdToken(context: Context): Result<String>
}