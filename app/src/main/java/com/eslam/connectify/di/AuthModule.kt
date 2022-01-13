package com.eslam.connectify.di

import com.eslam.connectify.data.repositories.AuthRepositoryImpl
import com.eslam.connectify.domain.datasources.AuthDataSource
import com.eslam.connectify.domain.usecases.SignInUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @ActivityScoped
    fun provideRepository(): AuthDataSource = AuthRepositoryImpl()


    @Provides
    @ActivityScoped
    fun provideAuthUseCases(dataSource: AuthDataSource):SignInUseCase = SignInUseCase(dataSource)





}