package io.rndev.loginlab.framework

import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.rndev.loginlab.data.datasource.AuthRemoteDataSource
import io.rndev.loginlab.data.datasource.TokenRemoteDataSource
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceBindsModule {

    @Binds
    abstract fun bindAuthRemoteDataSource(impl: FirebaseAuthDataSource): AuthRemoteDataSource

    @Binds
    abstract fun bindGoogleCredentialRemoteDataSource(impl: GoogleCredentialRemoteDataSource): TokenRemoteDataSource
}


@Module
@InstallIn(SingletonComponent::class)
internal class FirebaseAuthModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun googleIdOption(@Named("googleApiKey") googleApiKey: String) =
        GetSignInWithGoogleOption.Builder(googleApiKey)
            .build()

    @Singleton
    @Provides
    fun request(signInWithGoogleOption: GetSignInWithGoogleOption) = GetCredentialRequest.Builder()
        .addCredentialOption(signInWithGoogleOption)
        .build()
}
