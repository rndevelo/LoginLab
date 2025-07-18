package io.rndev.loginlab

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.rndev.loginlab.domain.AuthRepository
import io.rndev.loginlab.data.AuthRepositoryImpl
import io.rndev.loginlab.domain.CredentialRepository
import io.rndev.loginlab.data.CredentialRepositoryImpl
import io.rndev.loginlab.data.datasources.AuthRemoteDataSource
import io.rndev.loginlab.data.datasources.CredentialRemoteDataSource
import io.rndev.loginlab.data.datasources.FirebaseAuthDataSource
import io.rndev.loginlab.data.datasources.FirebaseCredentialDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryBindsModule {

//    Auth
    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindAuthRemoteDataSource(impl: FirebaseAuthDataSource): AuthRemoteDataSource

    @Binds
    abstract fun bindCredentialRepository(impl: CredentialRepositoryImpl): CredentialRepository

    @Binds
    abstract fun bindCredentialRemoteDataSource(impl: FirebaseCredentialDataSource): CredentialRemoteDataSource
}

@Module
@InstallIn(SingletonComponent::class)
internal class FirebaseAuthModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth() = Firebase.auth

    @Singleton
    @Provides
    fun googleIdOption() =
        GetSignInWithGoogleOption.Builder(BuildConfig.WEB_GOOGLE_ID_CLIENT)
            .build()

    // Create the Credential Manager request
    @Singleton
    @Provides
    fun request(signInWithGoogleOption: GetSignInWithGoogleOption) = GetCredentialRequest.Builder()
        .addCredentialOption(signInWithGoogleOption)
        .build()

    @Singleton
    @Provides
    fun credentialManager(@ApplicationContext context: Context): CredentialManager =
        CredentialManager.create(context)

    @Singleton
    @Provides
    fun callbackManager() = CallbackManager.Factory.create()


    @Singleton
    @Provides
    fun loginManager() = LoginManager.getInstance()
}
