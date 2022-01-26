package com.eslam.connectify.di

import com.eslam.connectify.data.repositories.ChatRepositoryImpl
import com.eslam.connectify.data.repositories.ChatRoomRepositoryImpl
import com.eslam.connectify.domain.datasources.ChatRoomRepository
import com.eslam.connectify.domain.usecases.ChatRoomUseCases
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [FireBaseModule::class])
@InstallIn(SingletonComponent::class)
object MessagesScreenModule {

    @Provides
    @Singleton
    fun provideChatRoomRepository(auth:FirebaseAuth,database: FirebaseDatabase):ChatRoomRepository
    {
        return ChatRoomRepositoryImpl(database,auth)
    }

    @Provides
    @Singleton
    fun provideChatRoomUseCases(repository:ChatRoomRepository):ChatRoomUseCases
    {
        return ChatRoomUseCases(repository)
    }
}