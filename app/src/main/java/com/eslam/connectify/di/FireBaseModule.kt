package com.eslam.connectify.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.view.View
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object FireBaseModule {


    @Provides
    @ViewModelScoped
    fun providesFireAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @ViewModelScoped
    fun providesFireStorage(): FirebaseStorage = FirebaseStorage.getInstance()


    @Provides
    @ViewModelScoped
    fun providesFireDataBase(): FirebaseDatabase = FirebaseDatabase.getInstance()
}