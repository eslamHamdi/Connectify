package com.eslam.connectify.di

import com.eslam.connectify.data.repositories.ProfileRepositoryImpl
import com.eslam.connectify.domain.datasources.ProfileRepository
import com.eslam.connectify.domain.usecases.AccountSetupUseCases
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module(includes = [FireBaseModule::class])
@InstallIn(SingletonComponent::class)
object ProfileModule {


    @Provides
    @Singleton
    fun provideProfileRepository(auth: FirebaseAuth,  database: FirebaseDatabase, storage: FirebaseStorage
    ):ProfileRepository {

        return ProfileRepositoryImpl(auth,database,storage)
    }

    @Provides
    @Singleton
    fun provideAccountSetUpUseCases(repository: ProfileRepository)= AccountSetupUseCases(repository)





}