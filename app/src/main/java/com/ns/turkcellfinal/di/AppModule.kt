package com.ns.turkcellfinal.di

import com.ns.turkcellfinal.presentation.account.UserManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideUserManager(): UserManager {
        return UserManager()
    }

}