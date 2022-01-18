package com.eslam.connectify.di

import com.eslam.connectify.data.repositories.AuthRepositoryImpl
import com.eslam.connectify.domain.datasources.AuthDataSource
import com.eslam.connectify.domain.usecases.SignInUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(ViewModelComponent::class)
object AuthModule {

    @Provides
    @ViewModelScoped
    fun provideRepository(): AuthDataSource = AuthRepositoryImpl()


    @Provides
    @ViewModelScoped
    fun provideAuthUseCases(dataSource: AuthDataSource):SignInUseCase = SignInUseCase(dataSource)





}