package io.rndev.loginlab

import android.app.Application
import android.location.Geocoder
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.rndev.loginlab.data.AuthRepository
import io.rndev.loginlab.data.AuthRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryBindsModule {

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}

@Module
@InstallIn(SingletonComponent::class)
internal class FirebaseAuthModule {

    @Provides
    fun provideFirebaseAuth() = Firebase.auth
}