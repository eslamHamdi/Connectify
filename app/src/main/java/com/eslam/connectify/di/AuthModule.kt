package com.eslam.connectify.di

import com.eslam.connectify.data.repositories.AuthRepository
import com.eslam.connectify.domain.datasources.AuthDataSource
import com.eslam.connectify.domain.usecases.SignInUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    fun provideRepository(): AuthDataSource = AuthRepository()


    @Provides
    fun provideAuthUseCases(dataSource: AuthDataSource):SignInUseCase = SignInUseCase(dataSource)





}