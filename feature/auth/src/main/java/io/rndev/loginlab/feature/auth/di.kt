package io.rndev.loginlab.feature.auth

import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class FacebookAuthModule {

    @Provides
    fun callbackManager() = CallbackManager.Factory.create()

    @Provides
    fun loginManager() = LoginManager.getInstance()
}