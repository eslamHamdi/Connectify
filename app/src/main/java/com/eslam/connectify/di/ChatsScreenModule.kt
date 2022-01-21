package com.eslam.connectify.di

import com.eslam.connectify.data.repositories.ChatRepositoryImpl
import com.eslam.connectify.domain.datasources.ChatRepository
import com.eslam.connectify.domain.usecases.ChatsScreenUseCases
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module(includes = [FireBaseModule::class])
@InstallIn(SingletonComponent::class)
object ChatsScreenModule {


    @Provides
    @Singleton
    fun providesChatRepository(auth:FirebaseAuth,dataBase:FirebaseDatabase):ChatRepository {
        return ChatRepositoryImpl(auth,dataBase)
    }

    @Provides
    @Singleton
    fun providesChatUseCases(repository: ChatRepository):ChatsScreenUseCases
    {
        return ChatsScreenUseCases(repository)
    }
}