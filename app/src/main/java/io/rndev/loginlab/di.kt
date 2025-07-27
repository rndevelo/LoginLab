package io.rndev.loginlab

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("googleApiKey")
    fun provideApiKey() = BuildConfig.WEB_GOOGLE_ID_CLIENT

}
