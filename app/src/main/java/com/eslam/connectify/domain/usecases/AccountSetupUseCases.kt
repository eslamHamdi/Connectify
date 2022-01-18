package com.eslam.connectify.domain.usecases

import android.net.Uri
import com.eslam.connectify.domain.datasources.ProfileRepository
import javax.inject.Inject

class AccountSetupUseCases @Inject constructor(private val repository: ProfileRepository){


    fun createProfile(image: Uri?,name:String) = repository.createProfile(image,name)

    fun updateProfileInfo(image: Uri?,name:String) = repository.updateProfile(image,name)

    fun getCurrentUserInfo() = repository.getUserInfo()

    fun getUserId()= repository.getUserAuthId()







}