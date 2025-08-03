package io.rndev.loginlab.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.rndev.loginlab.AuthRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryBindsModule {

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}