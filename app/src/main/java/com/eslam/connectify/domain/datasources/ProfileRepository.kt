package com.eslam.connectify.domain.datasources

import android.net.Uri
import com.eslam.connectify.domain.models.Response
import com.eslam.connectify.domain.models.User
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface ProfileRepository  {


    suspend fun createProfile(img: Uri?, name:String):Response<Any>

    fun updateProfile(img: Uri?, name:String?):Flow<Response<Any>>

    fun getUserInfo():Flow<Response<User?>>

    fun getUserAuthId():String?

    fun getSignInState():FirebaseUser?
}