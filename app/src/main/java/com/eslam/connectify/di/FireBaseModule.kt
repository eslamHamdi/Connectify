package com.eslam.connectify.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FireBaseModule {


    @Provides
    fun providesFireAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun providesFireStorage(): FirebaseStorage = FirebaseStorage.getInstance()


    @Provides
    fun providesFireDataBase(): FirebaseDatabase = FirebaseDatabase.getInstance()
}