package com.eslam.connectify.domain.datasources

import android.net.Uri
import com.eslam.connectify.domain.models.Response
import kotlinx.coroutines.flow.Flow

interface ProfileRepository  {


    fun createProfile(img: Uri?, name:String):Flow<Response<Boolean>>
}