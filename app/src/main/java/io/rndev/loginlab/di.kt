package io.rndev.loginlab

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.rndev.loginlab.data.AuthRepository
import io.rndev.loginlab.data.AuthRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryBindsModule {

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
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
        GetSignInWithGoogleOption.Builder("263101724279-50gq008mptu0rf9r26jc4prcgmp32h6q.apps.googleusercontent.com")
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