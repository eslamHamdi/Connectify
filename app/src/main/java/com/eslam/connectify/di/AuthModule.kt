package com.eslam.connectify.di

import android.app.Application
import com.eslam.connectify.ConnectifyApp
import com.eslam.connectify.data.repositories.AuthRepository
import com.eslam.connectify.domain.AuthDataSource
import com.eslam.connectify.domain.usecases.SignInUseCase
import com.eslam.connectify.ui.sign.SignViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    fun provideRepository():AuthDataSource = AuthRepository()


    @Provides
    fun provideAuthUseCases(dataSource:AuthDataSource):SignInUseCase = SignInUseCase(dataSource)


//    @Provides
//    fun providesApp():Application = ConnectifyApp()

//    @Provides
//    fun provideViewModel(app:ConnectifyApp,useCases:SignInUseCase) = SignViewModel(useCases,app)

}