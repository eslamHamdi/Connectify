package com.eslam.connectify.di

import com.eslam.connectify.data.repositories.AuthRepositoryImpl
import com.eslam.connectify.domain.datasources.AuthDataSource
import com.eslam.connectify.domain.usecases.SignInUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
   @Singleton
    fun provideRepository(): AuthDataSource = AuthRepositoryImpl()


    @Provides
    @Singleton
    fun provideAuthUseCases(dataSource: AuthDataSource):SignInUseCase = SignInUseCase(dataSource)





}